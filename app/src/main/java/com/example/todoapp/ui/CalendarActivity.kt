package com.example.todoapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.R
import com.example.todoapp.data.TodoEntity
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var weekRangeText: TextView
    private lateinit var prevWeekButton: Button
    private lateinit var nextWeekButton: Button
    private lateinit var weekDaysContainer: LinearLayout

    private val viewModel: TodoViewModel by viewModels()
    private var currentWeekStart: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_waterfall)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Weekly Waterfall"

        initViews()
        setupWeekNavigation()

        // Set to start of current week
        currentWeekStart = getWeekStart(Calendar.getInstance())
        loadWeekData()
    }

    private fun initViews() {
        weekRangeText = findViewById(R.id.weekRangeText)
        prevWeekButton = findViewById(R.id.prevWeekButton)
        nextWeekButton = findViewById(R.id.nextWeekButton)
        weekDaysContainer = findViewById(R.id.weekDaysContainer)
    }

    private fun setupWeekNavigation() {
        prevWeekButton.setOnClickListener {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1)
            loadWeekData()
        }

        nextWeekButton.setOnClickListener {
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, 1)
            loadWeekData()
        }
    }

    private fun getWeekStart(date: Calendar): Calendar {
        val cal = date.clone() as Calendar
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    private fun loadWeekData() {
        // Update week range text
        val weekEnd = currentWeekStart.clone() as Calendar
        weekEnd.add(Calendar.DAY_OF_YEAR, 6)

        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        weekRangeText.text = "${dateFormat.format(currentWeekStart.time)} - ${dateFormat.format(weekEnd.time)}"

        // Clear existing day columns
        weekDaysContainer.removeAllViews()

        // Create 7 day columns for the week
        for (i in 0..6) {
            val dayDate = currentWeekStart.clone() as Calendar
            dayDate.add(Calendar.DAY_OF_YEAR, i)
            createDayColumn(dayDate)
        }

        // Load todos for all days
        loadTodosForWeek()
    }

    private fun createDayColumn(date: Calendar) {
        val columnView = LayoutInflater.from(this).inflate(
            R.layout.waterfall_day_column,
            weekDaysContainer,
            false
        )

        val dayNameText = columnView.findViewById<TextView>(R.id.dayNameText)
        val dayDateText = columnView.findViewById<TextView>(R.id.dayDateText)
        val todosContainer = columnView.findViewById<LinearLayout>(R.id.todosContainer)
        val todoCountText = columnView.findViewById<TextView>(R.id.todoCountText)

        // Set day name and date
        val dayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dayDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        dayNameText.text = dayNameFormat.format(date.time)
        dayDateText.text = dayDateFormat.format(date.time)

        // Highlight today
        val today = Calendar.getInstance()
        if (isSameDay(date, today)) {
            columnView.setBackgroundColor(Color.parseColor("#E8F5E9"))
            dayNameText.setTextColor(Color.parseColor("#2E7D32"))
        }

        // Store date as tag for later use
        columnView.tag = date.timeInMillis

        weekDaysContainer.addView(columnView)
    }

    private fun loadTodosForWeek() {
        // Switch to ALL mode to get all todos, then filter by week
        viewModel.setViewMode(TodoViewModel.ViewMode.ALL)

        viewModel.todos.observe(this) { allTodos ->
            // Group todos by day
            val todosByDay = mutableMapOf<Long, MutableList<TodoEntity>>()

            for (i in 0..6) {
                val dayDate = currentWeekStart.clone() as Calendar
                dayDate.add(Calendar.DAY_OF_YEAR, i)
                val dayStart = dayDate.timeInMillis

                todosByDay[dayStart] = mutableListOf()
            }

            // Filter and group todos
            allTodos.forEach { todo ->
                if (todo.dueDateTime != null) {
                    val todoDate = Calendar.getInstance()
                    todoDate.timeInMillis = todo.dueDateTime

                    // Find which day this todo belongs to
                    for (i in 0..6) {
                        val dayDate = currentWeekStart.clone() as Calendar
                        dayDate.add(Calendar.DAY_OF_YEAR, i)

                        if (isSameDay(todoDate, dayDate)) {
                            todosByDay[dayDate.timeInMillis]?.add(todo)
                            break
                        }
                    }
                }
            }

            // Update each day column with its todos
            for (i in 0 until weekDaysContainer.childCount) {
                val columnView = weekDaysContainer.getChildAt(i)
                val dayTimestamp = columnView.tag as Long
                val todosForDay = todosByDay[dayTimestamp] ?: emptyList()

                updateDayColumn(columnView, todosForDay)
            }
        }
    }

    private fun updateDayColumn(columnView: View, todos: List<TodoEntity>) {
        val todosContainer = columnView.findViewById<LinearLayout>(R.id.todosContainer)
        val todoCountText = columnView.findViewById<TextView>(R.id.todoCountText)

        todosContainer.removeAllViews()

        // Sort todos by time
        val sortedTodos = todos.sortedBy { it.dueDateTime ?: Long.MAX_VALUE }

        sortedTodos.forEach { todo ->
            val todoItemView = LayoutInflater.from(this).inflate(
                R.layout.waterfall_todo_item,
                todosContainer,
                false
            )

            val todoText = todoItemView.findViewById<TextView>(R.id.waterfallTodoText)
            val todoTime = todoItemView.findViewById<TextView>(R.id.waterfallTodoTime)
            val statusBar = todoItemView.findViewById<View>(R.id.waterfallStatusBar)

            todoText.text = todo.text

            // Format time
            if (todo.dueDateTime != null) {
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                todoTime.text = timeFormat.format(Date(todo.dueDateTime))
                todoTime.visibility = View.VISIBLE
            } else {
                todoTime.visibility = View.GONE
            }

            // Set status color
            if (todo.isCompleted) {
                statusBar.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                todoText.alpha = 0.6f
            } else {
                val now = System.currentTimeMillis()
                if (todo.dueDateTime != null && todo.dueDateTime < now) {
                    statusBar.setBackgroundColor(Color.parseColor("#F44336")) // Red (overdue)
                } else {
                    statusBar.setBackgroundColor(Color.parseColor("#2196F3")) // Blue (pending)
                }
            }

            // Click to view/edit todo
            todoItemView.setOnClickListener {
                val intent = Intent(this, AddTodoActivity::class.java)
                intent.putExtra("TODO_ID", todo.id)
                startActivity(intent)
            }

            todosContainer.addView(todoItemView)
        }

        // Update count
        val completedCount = todos.count { it.isCompleted }
        todoCountText.text = if (todos.isEmpty()) {
            "No todos"
        } else {
            "${todos.size} todos ($completedCount done)"
        }
    }

    private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
