package com.example.todoapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.data.TodoDatabase

class TodoNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val todoId = inputData.getLong("TODO_ID", -1)
        if (todoId == -1L) return Result.failure()

        val database = TodoDatabase.getDatabase(applicationContext)
        val todo = database.todoDao().getTodoById(todoId) ?: return Result.failure()

        if (!todo.isCompleted && todo.notificationEnabled) {
            showNotification(todo.text, todoId)
        }

        return Result.success()
    }

    private fun showNotification(todoText: String, todoId: Long) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Todo Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for due todos"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open app when notification is tapped
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            todoId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Todo Reminder")
            .setContentText(todoText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(todoId.toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "todo_reminders"
    }
}
