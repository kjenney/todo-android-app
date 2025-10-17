package com.example.todoapp.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todoapp.data.TodoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            // Reschedule all notifications after boot
            CoroutineScope(Dispatchers.IO).launch {
                val database = TodoDatabase.getDatabase(context)
                val todos = database.todoDao().getAllTodos().first()
                TodoNotificationScheduler.rescheduleAllNotifications(context, todos)
            }
        }
    }
}
