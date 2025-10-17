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

    override fun onEnabled(context: Context) {
        // Called when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Called when the last widget is removed
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            try {
                // Get the layout for the widget
                val views = RemoteViews(context.packageName, R.layout.todo_widget)

                // Create an Intent to launch MainActivity when header is clicked
                val headerIntent = Intent(context, MainActivity::class.java)
                val headerPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    headerIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widgetHeader, headerPendingIntent)

                // Set up the RemoteViews service for the ListView
                val serviceIntent = Intent(context, TodoWidgetService::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    // Add a unique data to ensure the intent is unique for each widget
                    data = android.net.Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                }
                views.setRemoteAdapter(R.id.widgetTodoList, serviceIntent)

                // Set empty view
                views.setEmptyView(R.id.widgetTodoList, R.id.widgetEmptyView)

                // Set up the click intent template for list items
                val clickIntent = Intent(context, MainActivity::class.java)
                val clickPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setPendingIntentTemplate(R.id.widgetTodoList, clickPendingIntent)

                // Tell the AppWidgetManager to perform an update on the current widget
                appWidgetManager.updateAppWidget(appWidgetId, views)

                // Notify the widget that data has changed
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetTodoList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
