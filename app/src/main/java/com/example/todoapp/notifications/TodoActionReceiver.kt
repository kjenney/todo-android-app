package com.example.todoapp.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.todoapp.data.TodoDatabase
import com.example.todoapp.data.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getLongExtra("TODO_ID", -1)
        val action = intent.getStringExtra("ACTION")

        if (todoId == -1L || action.isNullOrEmpty()) return

        when (action) {
            "COMPLETE" -> markTodoAsCompleted(context, todoId)
        }
    }

    private fun markTodoAsCompleted(context: Context, todoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = TodoDatabase.getDatabase(context)
            val repository = TodoRepository(
                database.todoDao(),
                database.todoCompletionHistoryDao()
            )

            val todo = repository.getTodoById(todoId)
            if (todo != null && !todo.isCompleted) {
                // Mark as completed
                repository.toggleTodoCompletion(todo)

                // Dismiss the notification
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(todoId.toInt())

                // Show a toast confirmation
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        "Todo completed: ${todo.text}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
