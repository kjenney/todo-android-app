package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_completion_history")
data class TodoCompletionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val todoId: Long,
    val completedAt: Long,
    val dueDateTime: Long?,
    val text: String // snapshot of todo text at completion time
)
