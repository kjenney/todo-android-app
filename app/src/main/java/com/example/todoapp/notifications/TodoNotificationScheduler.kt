package com.example.todoapp.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.todoapp.data.TodoEntity

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

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TodoAlarmReceiver::class.java).apply {
            putExtra("TODO_ID", todo.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Use setExactAndAllowWhileIdle for precise timing even in Doze mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                dueTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                dueTime,
                pendingIntent
            )
        }
    }

    fun cancelTodoNotification(context: Context, todoId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TodoAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    fun rescheduleAllNotifications(context: Context, todos: List<TodoEntity>) {
        todos.forEach { todo ->
            scheduleTodoNotification(context, todo)
        }
    }
}
