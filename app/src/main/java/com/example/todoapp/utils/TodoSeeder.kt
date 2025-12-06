package com.example.todoapp.utils

import com.example.todoapp.data.RecurrencePattern
import com.example.todoapp.data.RecurrenceType
import com.example.todoapp.data.TodoEntity
import java.util.*

object TodoSeeder {

    /**
     * Generate sample todos for testing.
     * Creates todos spread across the current week and next week.
     */
    fun generateSampleTodos(): List<TodoEntity> {
        val todos = mutableListOf<TodoEntity>()
        val now = Calendar.getInstance()

        // Get start of current week
        val weekStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Monday - 3 todos
        val monday = weekStart.clone() as Calendar
        todos.add(
            TodoEntity(
                text = "Team standup meeting",
                dueDateTime = monday.apply {
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )
        todos.add(
            TodoEntity(
                text = "Review pull requests",
                dueDateTime = monday.apply {
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 30)
                }.timeInMillis,
                isCompleted = true
            )
        )
        todos.add(
            TodoEntity(
                text = "Lunch with client",
                dueDateTime = monday.apply {
                    set(Calendar.HOUR_OF_DAY, 12)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )

        // Tuesday - 4 todos
        val tuesday = weekStart.clone() as Calendar
        tuesday.add(Calendar.DAY_OF_YEAR, 1)
        todos.add(
            TodoEntity(
                text = "Design review session",
                dueDateTime = tuesday.apply {
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 30)
                }.timeInMillis,
                isCompleted = false
            )
        )
        todos.add(
            TodoEntity(
                text = "Update documentation",
                dueDateTime = tuesday.apply {
                    set(Calendar.HOUR_OF_DAY, 11)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = true
            )
        )
        todos.add(
            TodoEntity(
                text = "Sprint planning",
                dueDateTime = tuesday.apply {
                    set(Calendar.HOUR_OF_DAY, 14)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )
        todos.add(
            TodoEntity(
                text = "Code review meeting",
                dueDateTime = tuesday.apply {
                    set(Calendar.HOUR_OF_DAY, 16)
                    set(Calendar.MINUTE, 30)
                }.timeInMillis,
                isCompleted = false
            )
        )

        // Wednesday - 2 todos
        val wednesday = weekStart.clone() as Calendar
        wednesday.add(Calendar.DAY_OF_YEAR, 2)
        todos.add(
            TodoEntity(
                text = "Gym workout",
                dueDateTime = wednesday.apply {
                    set(Calendar.HOUR_OF_DAY, 7)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = true
            )
        )
        todos.add(
            TodoEntity(
                text = "Fix production bug",
                dueDateTime = wednesday.apply {
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false,
                recurrencePattern = RecurrencePattern(
                    type = RecurrenceType.DAILY,
                    interval = 1
                )
            )
        )

        // Thursday - 5 todos (busiest day)
        val thursday = weekStart.clone() as Calendar
        thursday.add(Calendar.DAY_OF_YEAR, 3)
        todos.add(
            TodoEntity(
                text = "Morning coffee chat",
                dueDateTime = thursday.apply {
                    set(Calendar.HOUR_OF_DAY, 8)
                    set(Calendar.MINUTE, 30)
                }.timeInMillis,
                isCompleted = true
            )
        )
        todos.add(
            TodoEntity(
                text = "Quarterly review presentation",
                dueDateTime = thursday.apply {
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )
        todos.add(
            TodoEntity(
                text = "Database optimization task",
                dueDateTime = thursday.apply {
                    set(Calendar.HOUR_OF_DAY, 13)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )
        todos.add(
            TodoEntity(
                text = "Team building activity",
                dueDateTime = thursday.apply {
                    set(Calendar.HOUR_OF_DAY, 15)
                    set(Calendar.MINUTE, 30)
                }.timeInMillis,
                isCompleted = false
            )
        )
        todos.add(
            TodoEntity(
                text = "Prepare weekly report",
                dueDateTime = thursday.apply {
                    set(Calendar.HOUR_OF_DAY, 17)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )

        // Friday - 3 todos
        val friday = weekStart.clone() as Calendar
        friday.add(Calendar.DAY_OF_YEAR, 4)
        todos.add(
            TodoEntity(
                text = "Deploy to production",
                dueDateTime = friday.apply {
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )
        todos.add(
            TodoEntity(
                text = "Team retrospective",
                dueDateTime = friday.apply {
                    set(Calendar.HOUR_OF_DAY, 14)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )
        todos.add(
            TodoEntity(
                text = "Happy hour with team",
                dueDateTime = friday.apply {
                    set(Calendar.HOUR_OF_DAY, 17)
                    set(Calendar.MINUTE, 30)
                }.timeInMillis,
                isCompleted = false
            )
        )

        // Saturday - 2 todos
        val saturday = weekStart.clone() as Calendar
        saturday.add(Calendar.DAY_OF_YEAR, 5)
        todos.add(
            TodoEntity(
                text = "Morning run",
                dueDateTime = saturday.apply {
                    set(Calendar.HOUR_OF_DAY, 8)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false,
                recurrencePattern = RecurrencePattern(
                    type = RecurrenceType.WEEKLY,
                    interval = 1,
                    daysOfWeek = listOf(7) // Sunday
                )
            )
        )
        todos.add(
            TodoEntity(
                text = "Grocery shopping",
                dueDateTime = saturday.apply {
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 30)
                }.timeInMillis,
                isCompleted = false
            )
        )

        // Sunday - 1 todo
        val sunday = weekStart.clone() as Calendar
        sunday.add(Calendar.DAY_OF_YEAR, 6)
        todos.add(
            TodoEntity(
                text = "Meal prep for the week",
                dueDateTime = sunday.apply {
                    set(Calendar.HOUR_OF_DAY, 15)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )

        // Add some overdue todos from last week
        val lastWeek = weekStart.clone() as Calendar
        lastWeek.add(Calendar.WEEK_OF_YEAR, -1)
        todos.add(
            TodoEntity(
                text = "Overdue: Submit expense report",
                dueDateTime = lastWeek.apply {
                    add(Calendar.DAY_OF_YEAR, 4)
                    set(Calendar.HOUR_OF_DAY, 17)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )

        // Add some future todos for next week
        val nextWeek = weekStart.clone() as Calendar
        nextWeek.add(Calendar.WEEK_OF_YEAR, 1)
        todos.add(
            TodoEntity(
                text = "Next week: Client presentation",
                dueDateTime = nextWeek.apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )
        todos.add(
            TodoEntity(
                text = "Next week: Project deadline",
                dueDateTime = nextWeek.apply {
                    add(Calendar.DAY_OF_YEAR, 4)
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                }.timeInMillis,
                isCompleted = false
            )
        )

        return todos
    }

    /**
     * Generate minimal sample todos for quick testing.
     */
    fun generateMinimalSampleTodos(): List<TodoEntity> {
        val todos = mutableListOf<TodoEntity>()
        val today = Calendar.getInstance()

        // Today - morning
        todos.add(
            TodoEntity(
                text = "Morning standup",
                dueDateTime = today.apply {
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis,
                isCompleted = false
            )
        )

        // Today - afternoon
        todos.add(
            TodoEntity(
                text = "Lunch meeting",
                dueDateTime = today.apply {
                    set(Calendar.HOUR_OF_DAY, 12)
                    set(Calendar.MINUTE, 30)
                }.timeInMillis,
                isCompleted = false
            )
        )

        // Tomorrow
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
        }
        todos.add(
            TodoEntity(
                text = "Review code",
                dueDateTime = tomorrow.timeInMillis,
                isCompleted = false
            )
        )

        return todos
    }
}
