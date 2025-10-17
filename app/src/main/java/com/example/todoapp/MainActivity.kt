package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.ui.AddTodoActivity
import com.example.todoapp.ui.CalendarActivity
import com.example.todoapp.ui.TodoEntityAdapter
import com.example.todoapp.ui.TodoViewModel
import com.example.todoapp.utils.PermissionHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var todoAdapter: TodoEntityAdapter

    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        recyclerView = findViewById(R.id.todoRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        fab = findViewById(R.id.fab)

        // Setup RecyclerView
        todoAdapter = TodoEntityAdapter(
            onToggleComplete = { todo -> viewModel.toggleTodoCompletion(todo) },
            onDeleteClick = { todo -> viewModel.deleteTodo(todo) },
            onItemClick = { todo ->
                // Open edit activity
                val intent = Intent(this, AddTodoActivity::class.java)
                intent.putExtra("TODO_ID", todo.id)
                startActivity(intent)
            }
        )
        recyclerView.adapter = todoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe todos from ViewModel
        viewModel.todos.observe(this) { todos ->
            todoAdapter.submitList(todos)
            updateEmptyView(todos.isEmpty())
        }

        // Setup FAB
        fab.setOnClickListener {
            startActivity(Intent(this, AddTodoActivity::class.java))
        }

        // Set default view mode to TODAY
        viewModel.setViewMode(TodoViewModel.ViewMode.TODAY)
        supportActionBar?.title = "Today's Todos"

        // Check and request notification permissions
        checkNotificationPermissions()
    }

    private fun checkNotificationPermissions() {
        // Request all necessary permissions for reliable notifications
        if (!PermissionHelper.checkAllNotificationPermissions(this)) {
            PermissionHelper.requestAllNotificationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PermissionHelper.REQUEST_NOTIFICATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    // Notification permission granted, check other permissions
                    checkNotificationPermissions()
                } else {
                    Toast.makeText(
                        this,
                        "Notification permission is required to receive todo reminders",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Recheck permissions after returning from settings
        when (requestCode) {
            PermissionHelper.REQUEST_BATTERY_OPTIMIZATION,
            PermissionHelper.REQUEST_EXACT_ALARM_PERMISSION -> {
                checkNotificationPermissions()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_today -> {
                viewModel.setViewMode(TodoViewModel.ViewMode.TODAY)
                supportActionBar?.title = "Today's Todos"
                true
            }
            R.id.action_view_all -> {
                viewModel.setViewMode(TodoViewModel.ViewMode.ALL)
                supportActionBar?.title = "All Todos"
                true
            }
            R.id.action_calendar -> {
                startActivity(Intent(this, CalendarActivity::class.java))
                true
            }
            R.id.action_hide_completed -> {
                item.isChecked = !item.isChecked
                viewModel.setHideCompleted(item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
