package com.example.todoapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import java.util.*

class TodoRepository(
    private val todoDao: TodoDao,
    private val historyDao: TodoCompletionHistoryDao
) {
    fun getAllTodos(): Flow<List<TodoEntity>> {
        return todoDao.getAllTodos().onStart {
            generateMissingRecurrences()
        }
    }

    fun getTodayTodos(): Flow<List<TodoEntity>> {
        return todoDao.getTodayTodos().onStart {
            generateMissingRecurrences()
        }
    }

    fun getTodosByDate(date: Long): Flow<List<TodoEntity>> {
        return todoDao.getTodosByDate(date).onStart {
            generateMissingRecurrences()
        }
    }

    suspend fun getTodoById(id: Long): TodoEntity? = todoDao.getTodoById(id)

    suspend fun insertTodo(todo: TodoEntity): Long = todoDao.insert(todo)

    suspend fun updateTodo(todo: TodoEntity) = todoDao.update(todo)

    suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.delete(todo)
        // Optionally delete history
        // historyDao.deleteHistoryForTodo(todo.id)
    }

    suspend fun toggleTodoCompletion(todo: TodoEntity) {
        if (!todo.isCompleted) {
            // Mark as completed and save to history
            val completedTodo = todo.copy(
                isCompleted = true,
                lastModifiedAt = System.currentTimeMillis()
            )
            todoDao.update(completedTodo)

            // Record completion in history
            historyDao.insert(
                TodoCompletionHistory(
                    todoId = todo.id,
                    completedAt = System.currentTimeMillis(),
                    dueDateTime = todo.dueDateTime,
                    text = todo.text
                )
            )

            // No need to create next occurrence here - it will be auto-generated
            // when viewing todos based on the recurrence schedule
        } else {
            // Mark as not completed
            val uncompletedTodo = todo.copy(
                isCompleted = false,
                lastModifiedAt = System.currentTimeMillis()
            )
            todoDao.update(uncompletedTodo)
        }
    }

    fun getCompletionHistory(): Flow<List<TodoCompletionHistory>> = historyDao.getAllHistory()

    fun getHistoryByDate(date: Long): Flow<List<TodoCompletionHistory>> = historyDao.getHistoryByDate(date)

    suspend fun getDueTodos(currentTime: Long): List<TodoEntity> = todoDao.getDueTodos(currentTime)

    /**
     * Generates all missing recurring todo occurrences up to today.
     * This ensures that a new instance is created for each scheduled occurrence,
     * regardless of whether previous instances were completed.
     */
    private suspend fun generateMissingRecurrences() {
        val now = System.currentTimeMillis()
        val todayEnd = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        // Get all parent todos (those with recurrence patterns)
        val parentTodos = todoDao.getAllParentTodos()

        for (parentTodo in parentTodos) {
            // Skip if this todo doesn't have a recurrence pattern
            if (parentTodo.recurrencePattern.type == RecurrenceType.NONE) {
                continue
            }

            // Skip if recurrence has ended
            val pattern = parentTodo.recurrencePattern
            if (pattern.endDate != null && pattern.endDate < now) {
                continue
            }

            // Get the latest occurrence for this todo family
            val latestOccurrence = todoDao.getLatestOccurrence(parentTodo.id)
            val startFrom = latestOccurrence?.dueDateTime ?: parentTodo.dueDateTime ?: continue

            // Generate all missing occurrences from the latest one up to today
            generateOccurrencesUpTo(parentTodo, startFrom, todayEnd)
        }
    }

    /**
     * Generates occurrences for a recurring todo from a start date up to an end date.
     */
    private suspend fun generateOccurrencesUpTo(todo: TodoEntity, startFrom: Long, endDate: Long) {
        val pattern = todo.recurrencePattern
        var currentDate = startFrom

        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentDate
        }

        val maxIterations = 1000 // Safety limit
        var iterations = 0

        while (iterations < maxIterations) {
            // Calculate next occurrence date
            val originalTime = calendar.timeInMillis

            when (pattern.type) {
                RecurrenceType.HOURLY -> {
                    calendar.add(Calendar.HOUR_OF_DAY, pattern.interval)
                }
                RecurrenceType.TWICE_DAILY -> {
                    calendar.add(Calendar.HOUR_OF_DAY, 12)
                }
                RecurrenceType.DAILY -> {
                    calendar.add(Calendar.DAY_OF_MONTH, pattern.interval)
                }
                RecurrenceType.WEEKLY -> {
                    if (pattern.daysOfWeek.isNotEmpty()) {
                        // Find next day of week
                        var found = false
                        var daysChecked = 0
                        val maxDays = 7 * pattern.interval
                        while (!found && daysChecked < maxDays) {
                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                            val convertedDay = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
                            if (pattern.daysOfWeek.contains(convertedDay)) {
                                found = true
                            }
                            daysChecked++
                        }
                        if (!found) break
                    } else {
                        calendar.add(Calendar.WEEK_OF_YEAR, pattern.interval)
                    }
                }
                RecurrenceType.MONTHLY -> {
                    calendar.add(Calendar.MONTH, pattern.interval)
                    if (pattern.dayOfMonth != null) {
                        calendar.set(Calendar.DAY_OF_MONTH, pattern.dayOfMonth)
                    }
                }
                RecurrenceType.YEARLY -> {
                    calendar.add(Calendar.YEAR, pattern.interval)
                }
                else -> break
            }

            val nextOccurrenceTime = calendar.timeInMillis

            // Stop if we've exceeded the end date
            if (nextOccurrenceTime > endDate) {
                break
            }

            // Stop if we've exceeded the recurrence end date
            if (pattern.endDate != null && nextOccurrenceTime > pattern.endDate) {
                break
            }

            // Create the new occurrence if it doesn't already exist
            val newOccurrence = todo.copy(
                id = 0, // New ID will be generated
                isCompleted = false,
                dueDateTime = nextOccurrenceTime,
                createdAt = System.currentTimeMillis(),
                lastModifiedAt = System.currentTimeMillis(),
                parentTodoId = todo.parentTodoId ?: todo.id
            )

            // Check if this occurrence already exists
            if (!isDuplicateOccurrence(todo, newOccurrence)) {
                todoDao.insert(newOccurrence)
            }

            iterations++
        }
    }

    private suspend fun isDuplicateOccurrence(todo: TodoEntity, nextOccurrence: TodoEntity): Boolean {
        // Get the parent ID (either this todo's parent or this todo itself if it's the parent)
        val parentId = todo.parentTodoId ?: todo.id
        val nextDueTime = nextOccurrence.dueDateTime ?: return false

        // Check if there's already a todo within 1 hour of the next occurrence time
        // This works for all recurrence types including TWICE_DAILY
        val hourBefore = nextDueTime - (60 * 60 * 1000)
        val hourAfter = nextDueTime + (60 * 60 * 1000)
        val existingTodos = todoDao.getRelatedTodosByDateRange(parentId, hourBefore, hourAfter)

        return existingTodos.isNotEmpty()
    }

    private fun createNextRecurrence(todo: TodoEntity): TodoEntity? {
        val currentDue = todo.dueDateTime ?: return null
        val pattern = todo.recurrencePattern

        // Check if recurrence has ended
        if (pattern.endDate != null && currentDue >= pattern.endDate) {
            return null
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentDue
        }

        when (pattern.type) {
            RecurrenceType.HOURLY -> {
                calendar.add(Calendar.HOUR_OF_DAY, pattern.interval)
            }
            RecurrenceType.TWICE_DAILY -> {
                calendar.add(Calendar.HOUR_OF_DAY, 12)
            }
            RecurrenceType.DAILY -> {
                calendar.add(Calendar.DAY_OF_MONTH, pattern.interval)
            }
            RecurrenceType.WEEKLY -> {
                if (pattern.daysOfWeek.isNotEmpty()) {
                    // Find next day of week
                    var daysToAdd = 1
                    var found = false
                    val maxDays = 7 * pattern.interval
                    while (!found && daysToAdd <= maxDays) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        // Convert to 1=Monday format
                        val convertedDay = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
                        if (pattern.daysOfWeek.contains(convertedDay)) {
                            found = true
                        }
                        daysToAdd++
                    }
                    if (!found) return null
                } else {
                    calendar.add(Calendar.WEEK_OF_YEAR, pattern.interval)
                }
            }
            RecurrenceType.MONTHLY -> {
                calendar.add(Calendar.MONTH, pattern.interval)
                if (pattern.dayOfMonth != null) {
                    calendar.set(Calendar.DAY_OF_MONTH, pattern.dayOfMonth)
                }
            }
            RecurrenceType.YEARLY -> {
                calendar.add(Calendar.YEAR, pattern.interval)
            }
            else -> return null
        }

        // If the next occurrence is in the past, advance it to today or the future
        // This handles cases where todos are completed late
        val now = System.currentTimeMillis()
        val todayStart = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // For time-based recurrences (HOURLY, TWICE_DAILY), use the current time
        // For date-based recurrences (DAILY, WEEKLY, MONTHLY, YEARLY), use today's start
        val referenceTime = when (pattern.type) {
            RecurrenceType.HOURLY, RecurrenceType.TWICE_DAILY -> now
            else -> todayStart
        }

        // Keep adding intervals until we reach or pass the reference time
        var iterations = 0
        val maxIterations = 1000 // Safety limit to prevent infinite loops
        while (calendar.timeInMillis < referenceTime && iterations < maxIterations) {
            when (pattern.type) {
                RecurrenceType.HOURLY -> calendar.add(Calendar.HOUR_OF_DAY, pattern.interval)
                RecurrenceType.TWICE_DAILY -> calendar.add(Calendar.HOUR_OF_DAY, 12)
                RecurrenceType.DAILY -> calendar.add(Calendar.DAY_OF_MONTH, pattern.interval)
                RecurrenceType.WEEKLY -> {
                    if (pattern.daysOfWeek.isNotEmpty()) {
                        // For weekly with specific days, advance day by day
                        var found = false
                        var daysChecked = 0
                        val maxDays = 7 * pattern.interval
                        while (!found && daysChecked < maxDays && calendar.timeInMillis < referenceTime) {
                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                            val convertedDay = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
                            if (pattern.daysOfWeek.contains(convertedDay)) {
                                found = true
                            }
                            daysChecked++
                        }
                        if (!found) break
                    } else {
                        calendar.add(Calendar.WEEK_OF_YEAR, pattern.interval)
                    }
                }
                RecurrenceType.MONTHLY -> calendar.add(Calendar.MONTH, pattern.interval)
                RecurrenceType.YEARLY -> calendar.add(Calendar.YEAR, pattern.interval)
                else -> break
            }
            iterations++
        }

        return todo.copy(
            id = 0, // New ID will be generated
            isCompleted = false,
            dueDateTime = calendar.timeInMillis,
            createdAt = System.currentTimeMillis(),
            lastModifiedAt = System.currentTimeMillis(),
            parentTodoId = todo.parentTodoId ?: todo.id
        )
    }
}
