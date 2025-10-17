package com.example.todoapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateText: TextView
    private lateinit var todosRecyclerView: RecyclerView
    private lateinit var emptyDateView: TextView
    private lateinit var todoAdapter: TodoEntityAdapter

    private val viewModel: TodoViewModel by viewModels()
    private var selectedDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Calendar"

        initViews()
        setupCalendar()
        setupTodosList()
    }

    private fun initViews() {
        calendarView = findViewById(R.id.calendarView)
        selectedDateText = findViewById(R.id.selectedDateText)
        todosRecyclerView = findViewById(R.id.todosForDateRecyclerView)
        emptyDateView = findViewById(R.id.emptyDateView)
    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                    container.textView.alpha = 1f
                } else {
                    container.textView.setTextColor(Color.GRAY)
                    container.textView.alpha = 0.3f
                }

                container.view.setOnClickListener {
                    if (data.position == DayPosition.MonthDate) {
                        onDateSelected(data)
                    }
                }
            }
        }
    }

    private fun setupTodosList() {
        todoAdapter = TodoEntityAdapter(
            onToggleComplete = { todo -> viewModel.toggleTodoCompletion(todo) },
            onDeleteClick = { todo -> viewModel.deleteTodo(todo) },
            onItemClick = { todo ->
                val intent = Intent(this, AddTodoActivity::class.java)
                intent.putExtra("TODO_ID", todo.id)
                startActivity(intent)
            }
        )
        todosRecyclerView.adapter = todoAdapter
        todosRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun onDateSelected(day: CalendarDay) {
        selectedDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, day.date.year)
            set(Calendar.MONTH, day.date.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, day.date.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        selectedDateText.text = format.format(selectedDate!!.time)

        // Load todos for this date
        viewModel.setSelectedDate(selectedDate!!.timeInMillis)
        viewModel.todos.observe(this) { todos ->
            todoAdapter.submitList(todos)
            if (todos.isEmpty()) {
                emptyDateView.visibility = View.VISIBLE
                todosRecyclerView.visibility = View.GONE
            } else {
                emptyDateView.visibility = View.GONE
                todosRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
    }
}
