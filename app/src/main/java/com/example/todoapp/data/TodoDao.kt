package com.example.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY dueDateTime ASC, createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE date(dueDateTime/1000, 'unixepoch', 'localtime') = date('now', 'localtime') ORDER BY dueDateTime ASC")
    fun getTodayTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE date(dueDateTime/1000, 'unixepoch', 'localtime') = date(:date/1000, 'unixepoch', 'localtime') ORDER BY dueDateTime ASC")
    fun getTodosByDate(date: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): TodoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity): Long

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM todos WHERE dueDateTime <= :currentTime")
    suspend fun getDueTodos(currentTime: Long): List<TodoEntity>

    @Query("SELECT * FROM todos WHERE (id = :parentId OR parentTodoId = :parentId) AND dueDateTime >= :startTime AND dueDateTime <= :endTime AND isCompleted = 0")
    suspend fun getRelatedTodosByDateRange(parentId: Long, startTime: Long, endTime: Long): List<TodoEntity>

    @Query("SELECT * FROM todos WHERE parentTodoId IS NULL")
    suspend fun getAllParentTodos(): List<TodoEntity>

    @Query("SELECT * FROM todos WHERE id = :parentId OR parentTodoId = :parentId ORDER BY dueDateTime DESC LIMIT 1")
    suspend fun getLatestOccurrence(parentId: Long): TodoEntity?
}
