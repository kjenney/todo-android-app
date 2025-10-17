package com.example.todoapp.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.todoapp.R
import com.example.todoapp.data.TodoDatabase
import com.example.todoapp.data.TodoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CalendarWidgetTodoService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return CalendarTodoFactory(this.applicationContext, intent)
    }
}

class CalendarTodoFactory(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private var todoList: List<TodoEntity> = emptyList()

    override fun onCreate() {
        loadData()
    }

    override fun onDataSetChanged() {
        loadData()
    }

    private fun loadData() {
        try {
            val database = TodoDatabase.getDatabase(context)
            todoList = runBlocking {
                try {
                    // Show today's todos by default
                    database.todoDao().getTodayTodos().first()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            todoList = emptyList()
        }
    }

    override fun onDestroy() {
        todoList = emptyList()
    }

    override fun getCount(): Int = todoList.size.coerceAtMost(5) // Limit to 5 items

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_todo_item)

        try {
            if (position in todoList.indices) {
                val todo = todoList[position]
                views.setTextViewText(R.id.widgetTodoCheckbox, todo.text)
                views.setBoolean(R.id.widgetTodoCheckbox, "setChecked", todo.isCompleted)

                val fillInIntent = Intent().apply {
                    putExtra("TODO_ID", todo.id)
                }
                views.setOnClickFillInIntent(R.id.widgetTodoCheckbox, fillInIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long {
        return if (position in todoList.indices) {
            todoList[position].id
        } else {
            position.toLong()
        }
    }

    override fun hasStableIds(): Boolean = true
}
