package com.example.todoapp.data

import kotlinx.coroutines.flow.Flow
import java.util.*

class TodoRepository(
    private val todoDao: TodoDao,
    private val historyDao: TodoCompletionHistoryDao
) {
    fun getAllTodos(): Flow<List<TodoEntity>> = todoDao.getAllTodos()

    fun getTodayTodos(): Flow<List<TodoEntity>> = todoDao.getTodayTodos()

    fun getTodosByDate(date: Long): Flow<List<TodoEntity>> = todoDao.getTodosByDate(date)

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

            // If recurring, create next occurrence
            if (todo.recurrencePattern.type != RecurrenceType.NONE) {
                val nextOccurrence = createNextRecurrence(todo)
                if (nextOccurrence != null) {
                    todoDao.insert(nextOccurrence)
                }
            }
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
