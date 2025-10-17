package com.example.todoapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.todoapp.MainActivity
import com.example.todoapp.R

class TodoWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Create an Intent to launch MainActivity
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Get the layout for the widget and attach click listener
            val views = RemoteViews(context.packageName, R.layout.todo_widget)
            views.setOnClickPendingIntent(R.id.widgetTodoList, pendingIntent)

            // Set up the RemoteViews service for the ListView
            val serviceIntent = Intent(context, TodoWidgetService::class.java)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            views.setRemoteAdapter(R.id.widgetTodoList, serviceIntent)

            // Set up the click intent template for list items
            val clickIntentTemplate = Intent(context, MainActivity::class.java)
            val clickPendingIntentTemplate = PendingIntent.getActivity(
                context,
                0,
                clickIntentTemplate,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setPendingIntentTemplate(R.id.widgetTodoList, clickPendingIntentTemplate)

            // Tell the AppWidgetManager to perform an update on the current widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
