package com.example.todoapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.data.TodoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getLongExtra("TODO_ID", -1)
        if (todoId == -1L) return

        CoroutineScope(Dispatchers.IO).launch {
            val database = TodoDatabase.getDatabase(context)
            val todo = database.todoDao().getTodoById(todoId)

            if (todo != null && !todo.isCompleted && todo.notificationEnabled) {
                showNotification(context, todo.text, todoId)
            }
        }
    }

    private fun showNotification(context: Context, todoText: String, todoId: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Todo Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for due todos"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open app when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            todoId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create intent for "Complete" action
        val completeIntent = Intent(context, TodoActionReceiver::class.java).apply {
            putExtra("TODO_ID", todoId)
            putExtra("ACTION", "COMPLETE")
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            (todoId + 10000).toInt(), // Different request code to avoid conflicts
            completeIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Use BigTextStyle to make notification expandable and show actions
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(todoText)
            .setBigContentTitle("Todo Reminder")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Todo Reminder")
            .setContentText(todoText)
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Mark Complete",
                completePendingIntent
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        notificationManager.notify(todoId.toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "todo_reminders"
    }
}
