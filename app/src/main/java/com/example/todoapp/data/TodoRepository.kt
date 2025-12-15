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
     * Generates daily todo instances for all parent todos.
     * Every todo gets a new instance each day automatically.
     * Returns the list of newly created todos.
     */
    suspend fun generateMissingRecurrences(): List<TodoEntity> {
        val newlyCreatedTodos = mutableListOf<TodoEntity>()
        val todayStart = getTodayStart()

        // Get all parent todos (original todos without a parent)
        val parentTodos = todoDao.getAllParentTodos()

        for (parentTodo in parentTodos) {
            // Check if today's instance already exists
            val todayInstance = getTodayInstanceForParent(parentTodo.id, todayStart)

            if (todayInstance == null) {
                // Create today's instance with the same time of day as the parent
                val todayDateTime = if (parentTodo.dueDateTime != null) {
                    // Extract time of day from parent and apply to today
                    val parentCalendar = Calendar.getInstance().apply {
                        timeInMillis = parentTodo.dueDateTime
                    }
                    val hour = parentCalendar.get(Calendar.HOUR_OF_DAY)
                    val minute = parentCalendar.get(Calendar.MINUTE)

                    Calendar.getInstance().apply {
                        timeInMillis = todayStart
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                } else {
                    null
                }

                val newOccurrence = parentTodo.copy(
                    id = 0, // New ID will be generated
                    isCompleted = false,
                    dueDateTime = todayDateTime,
                    createdAt = System.currentTimeMillis(),
                    lastModifiedAt = System.currentTimeMillis(),
                    parentTodoId = parentTodo.id
                )

                val newId = todoDao.insert(newOccurrence)
                val insertedTodo = todoDao.getTodoById(newId)
                insertedTodo?.let { newlyCreatedTodos.add(it) }
            }
        }

        return newlyCreatedTodos
    }

    private fun getTodayStart(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private suspend fun getTodayInstanceForParent(parentId: Long, todayStart: Long): TodoEntity? {
        val todayEnd = todayStart + (24 * 60 * 60 * 1000) - 1
        val instances = todoDao.getRelatedTodosByDateRange(parentId, todayStart, todayEnd)
        return instances.firstOrNull()
    }
}
