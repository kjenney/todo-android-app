package com.example.todoapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.todoapp.R
import com.example.todoapp.ui.CalendarActivity
import java.text.SimpleDateFormat
import java.util.*

class CalendarWidget : AppWidgetProvider() {

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
                val views = RemoteViews(context.packageName, R.layout.calendar_widget)

                // Set current month/year
                val calendar = Calendar.getInstance()
                val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                views.setTextViewText(R.id.calendarWidgetMonthYear, monthYearFormat.format(calendar.time))

                // Create intent to launch CalendarActivity when header is clicked
                val headerIntent = Intent(context, CalendarActivity::class.java)
                val headerPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    headerIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.calendarWidgetHeader, headerPendingIntent)

                // Set up calendar grid adapter
                val calendarGridIntent = Intent(context, CalendarWidgetGridService::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    data = android.net.Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                }
                views.setRemoteAdapter(R.id.calendarWidgetGrid, calendarGridIntent)

                // Set up todo list adapter
                val todoListIntent = Intent(context, CalendarWidgetTodoService::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    data = android.net.Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                }
                views.setRemoteAdapter(R.id.calendarWidgetTodoList, todoListIntent)

                // Set empty view
                views.setEmptyView(R.id.calendarWidgetTodoList, R.id.calendarWidgetEmptyView)

                // Set up click intent template for calendar days
                val dayClickIntent = Intent(context, CalendarActivity::class.java)
                val dayClickPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    dayClickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setPendingIntentTemplate(R.id.calendarWidgetGrid, dayClickPendingIntent)

                // Set up click intent template for todo list
                val todoClickIntent = Intent(context, CalendarActivity::class.java)
                val todoClickPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    todoClickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setPendingIntentTemplate(R.id.calendarWidgetTodoList, todoClickPendingIntent)

                // Update widget
                appWidgetManager.updateAppWidget(appWidgetId, views)

                // Notify data changed
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.calendarWidgetGrid)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.calendarWidgetTodoList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
