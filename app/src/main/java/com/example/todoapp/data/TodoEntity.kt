package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "todos")
@TypeConverters(Converters::class)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isCompleted: Boolean = false,
    val dueDateTime: Long? = null, // timestamp in milliseconds
    val recurrencePattern: RecurrencePattern = RecurrencePattern.none(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis(),
    val notificationEnabled: Boolean = true,
    val parentTodoId: Long? = null // for recurring todos, links to the original
)
