package com.example.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoCompletionHistoryDao {
    @Query("SELECT * FROM todo_completion_history ORDER BY completedAt DESC")
    fun getAllHistory(): Flow<List<TodoCompletionHistory>>

    @Query("SELECT * FROM todo_completion_history WHERE todoId = :todoId ORDER BY completedAt DESC")
    fun getHistoryForTodo(todoId: Long): Flow<List<TodoCompletionHistory>>

    @Query("SELECT * FROM todo_completion_history WHERE date(completedAt/1000, 'unixepoch', 'localtime') = date(:date/1000, 'unixepoch', 'localtime') ORDER BY completedAt DESC")
    fun getHistoryByDate(date: Long): Flow<List<TodoCompletionHistory>>

    @Insert
    suspend fun insert(history: TodoCompletionHistory)

    @Query("DELETE FROM todo_completion_history WHERE todoId = :todoId")
    suspend fun deleteHistoryForTodo(todoId: Long)
}
