package com.example.todoapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.todoapp.R
import com.example.todoapp.data.TodoDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class CalendarWidgetGridService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return CalendarGridFactory(this.applicationContext, intent)
    }
}

class CalendarGridFactory(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val calendar = Calendar.getInstance()
    private var daysInMonth = mutableListOf<Int>()
    private var daysWithTodos = setOf<Int>()

    override fun onCreate() {
        loadCalendarData()
    }

    override fun onDataSetChanged() {
        loadCalendarData()
    }

    private fun loadCalendarData() {
        try {
            calendar.time = Date()
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
            val daysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            daysInMonth.clear()

            // Add empty cells for days before the first day of month
            for (i in 0 until firstDayOfWeek) {
                daysInMonth.add(0)
            }

            // Add actual days of month
            for (day in 1..daysInCurrentMonth) {
                daysInMonth.add(day)
            }

            // Load days that have todos
            loadDaysWithTodos()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadDaysWithTodos() {
        try {
            val database = TodoDatabase.getDatabase(context)
            val todos = runBlocking {
                try {
                    database.todoDao().getAllTodos().first()
                } catch (e: Exception) {
                    emptyList()
                }
            }

            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)

            daysWithTodos = todos.mapNotNull { todo ->
                todo.dueDateTime?.let {
                    val todoCalendar = Calendar.getInstance().apply { timeInMillis = it }
                    if (todoCalendar.get(Calendar.MONTH) == currentMonth &&
                        todoCalendar.get(Calendar.YEAR) == currentYear
                    ) {
                        todoCalendar.get(Calendar.DAY_OF_MONTH)
                    } else null
                }
            }.toSet()
        } catch (e: Exception) {
            e.printStackTrace()
            daysWithTodos = emptySet()
        }
    }

    override fun onDestroy() {
        daysInMonth.clear()
    }

    override fun getCount(): Int = daysInMonth.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.calendar_widget_day_item)

        try {
            if (position in daysInMonth.indices) {
                val day = daysInMonth[position]

                if (day > 0) {
                    views.setTextViewText(R.id.calendarDayNumber, day.toString())
                    views.setInt(R.id.calendarDayNumber, "setTextColor", Color.BLACK)

                    // Highlight today
                    val today = Calendar.getInstance()
                    if (day == today.get(Calendar.DAY_OF_MONTH) &&
                        calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    ) {
                        views.setInt(R.id.calendarDayNumber, "setTextColor", Color.parseColor("#6200EE"))
                        views.setTextViewTextSize(R.id.calendarDayNumber, android.util.TypedValue.COMPLEX_UNIT_SP, 14f)
                    }

                    // Show indicator if day has todos
                    if (daysWithTodos.contains(day)) {
                        views.setViewVisibility(R.id.calendarDayIndicator, View.VISIBLE)
                    } else {
                        views.setViewVisibility(R.id.calendarDayIndicator, View.GONE)
                    }

                    // Set click intent
                    val fillInIntent = Intent().apply {
                        putExtra("SELECTED_DAY", day)
                    }
                    views.setOnClickFillInIntent(R.id.calendarDayNumber, fillInIntent)
                } else {
                    views.setTextViewText(R.id.calendarDayNumber, "")
                    views.setViewVisibility(R.id.calendarDayIndicator, View.GONE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
