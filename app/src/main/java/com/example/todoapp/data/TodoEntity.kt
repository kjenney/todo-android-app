package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isCompleted: Boolean = false,
    val dueDateTime: Long? = null, // timestamp in milliseconds - time of day for the notification
    val createdAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val parentTodoId: Long? = null // links all daily instances of the same todo
)
