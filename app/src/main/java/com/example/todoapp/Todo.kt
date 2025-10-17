package com.example.todoapp

data class Todo(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    var isCompleted: Boolean = false
)
