package com.example.todoapp.notifications

import android.content.Context
import androidx.work.*
import com.example.todoapp.data.TodoEntity
import java.util.concurrent.TimeUnit

object TodoNotificationScheduler {

    fun scheduleTodoNotification(context: Context, todo: TodoEntity) {
        if (!todo.notificationEnabled || todo.dueDateTime == null || todo.isCompleted) {
            return
        }

        val currentTime = System.currentTimeMillis()
        val dueTime = todo.dueDateTime

        // Only schedule if due time is in the future
        if (dueTime <= currentTime) {
            return
        }

        val delay = dueTime - currentTime

        val workRequest = OneTimeWorkRequestBuilder<TodoNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("TODO_ID" to todo.id))
            .addTag("todo_notification_${todo.id}")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "todo_notification_${todo.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    fun cancelTodoNotification(context: Context, todoId: Long) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("todo_notification_$todoId")
    }

    fun rescheduleAllNotifications(context: Context, todos: List<TodoEntity>) {
        todos.forEach { todo ->
            scheduleTodoNotification(context, todo)
        }
    }
}
