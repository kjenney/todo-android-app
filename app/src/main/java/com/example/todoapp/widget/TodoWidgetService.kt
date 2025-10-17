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

class TodoWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TodoRemoteViewsFactory(this.applicationContext)
    }
}

class TodoRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var todoList: List<TodoEntity> = emptyList()

    override fun onCreate() {
        // Initialize
    }

    override fun onDataSetChanged() {
        // Load today's todos
        val database = TodoDatabase.getDatabase(context)
        todoList = runBlocking {
            database.todoDao().getTodayTodos().first()
        }
    }

    override fun onDestroy() {
        // Cleanup
    }

    override fun getCount(): Int = todoList.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_todo_item)

        if (position < todoList.size) {
            val todo = todoList[position]
            views.setTextViewText(R.id.widgetTodoCheckbox, todo.text)
            views.setBoolean(R.id.widgetTodoCheckbox, "setChecked", todo.isCompleted)
        }

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
