package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.ui.AddTodoActivity
import com.example.todoapp.ui.CalendarActivity
import com.example.todoapp.ui.TodoEntityAdapter
import com.example.todoapp.ui.TodoViewModel
import com.example.todoapp.utils.PermissionHelper
import com.example.todoapp.utils.TodoExportImportManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var todoAdapter: TodoEntityAdapter

    private val viewModel: TodoViewModel by viewModels()

    // Activity result launcher for exporting todos
    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let { handleExport(it) }
    }

    // Activity result launcher for importing todos
    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { handleImport(it) }
    }

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

        // Clear any old WorkManager notifications (migration from old system)
        clearOldWorkManagerNotifications()
    }

    private fun clearOldWorkManagerNotifications() {
        try {
            androidx.work.WorkManager.getInstance(this).cancelAllWork()
        } catch (e: Exception) {
            // WorkManager might not be initialized, ignore
        }
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
            R.id.action_export_todos -> {
                startExport()
                true
            }
            R.id.action_import_todos -> {
                startImport()
                true
            }
            R.id.action_test_notification -> {
                showTestNotification()
                true
            }
            R.id.action_seed_data -> {
                seedSampleTodos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showTestNotification() {
        // Show a test notification with the complete action
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Create channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "todo_reminders",
                "Todo Reminders",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for due todos"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create main tap intent
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            this,
            999,
            intent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create complete action intent
        val completeIntent = Intent(this, com.example.todoapp.notifications.TodoActionReceiver::class.java).apply {
            putExtra("TODO_ID", 999L)
            putExtra("ACTION", "COMPLETE")
        }
        val completePendingIntent = android.app.PendingIntent.getBroadcast(
            this,
            1999,
            completeIntent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build notification
        val bigTextStyle = androidx.core.app.NotificationCompat.BigTextStyle()
            .bigText("This is a test notification. Swipe down to expand and see the 'Mark Complete' button.")
            .setBigContentTitle("Test Todo Reminder")

        val notification = androidx.core.app.NotificationCompat.Builder(this, "todo_reminders")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Test Todo Reminder")
            .setContentText("Swipe down to expand")
            .setStyle(bigTextStyle)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setDefaults(androidx.core.app.NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Mark Complete",
                completePendingIntent
            )
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(androidx.core.app.NotificationCompat.CATEGORY_REMINDER)
            .build()

        notificationManager.notify(999, notification)
        Toast.makeText(this, "Test notification sent! Swipe down on it to see actions.", Toast.LENGTH_LONG).show()
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

    private fun startExport() {
        val fileName = TodoExportImportManager.generateExportFileName()
        exportLauncher.launch(fileName)
    }

    private fun startImport() {
        // Show confirmation dialog before importing
        AlertDialog.Builder(this)
            .setTitle(R.string.import_confirmation_title)
            .setMessage(R.string.import_confirmation_message)
            .setPositiveButton(R.string.import_button) { _, _ ->
                importLauncher.launch(arrayOf("application/json"))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun handleExport(uri: Uri) {
        viewModel.getAllTodosForExport { todos ->
            val success = TodoExportImportManager.exportTodos(this, uri, todos)
            val message = if (success) {
                getString(R.string.export_success, todos.size)
            } else {
                getString(R.string.export_failed)
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun handleImport(uri: Uri) {
        val importedTodos = TodoExportImportManager.importTodos(this, uri)
        val message = if (importedTodos != null && importedTodos.isNotEmpty()) {
            viewModel.importTodos(importedTodos)
            getString(R.string.import_success, importedTodos.size)
        } else {
            getString(R.string.import_failed)
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun seedSampleTodos() {
        AlertDialog.Builder(this)
            .setTitle("Seed Sample Todos")
            .setMessage("This will add sample todos for testing. Continue?")
            .setPositiveButton("Seed Data") { _, _ ->
                val sampleTodos = com.example.todoapp.utils.TodoSeeder.generateSampleTodos()
                viewModel.importTodos(sampleTodos)
                Toast.makeText(
                    this,
                    "Added ${sampleTodos.size} sample todos",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
