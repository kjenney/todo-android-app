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
