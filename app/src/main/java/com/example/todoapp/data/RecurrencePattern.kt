package com.example.todoapp.data

enum class RecurrenceType {
    NONE,
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM
}

data class RecurrencePattern(
    val type: RecurrenceType = RecurrenceType.NONE,
    val interval: Int = 1, // e.g., every 2 days, every 3 hours
    val daysOfWeek: Set<Int> = emptySet(), // 1=Monday, 7=Sunday
    val dayOfMonth: Int? = null, // for monthly recurrence
    val endDate: Long? = null // null means no end date
) {
    companion object {
        fun none() = RecurrencePattern()

        fun hourly(interval: Int = 1) = RecurrencePattern(
            type = RecurrenceType.HOURLY,
            interval = interval
        )

        fun daily(interval: Int = 1) = RecurrencePattern(
            type = RecurrenceType.DAILY,
            interval = interval
        )

        fun weekly(interval: Int = 1, daysOfWeek: Set<Int> = emptySet()) = RecurrencePattern(
            type = RecurrenceType.WEEKLY,
            interval = interval,
            daysOfWeek = daysOfWeek
        )

        fun monthly(interval: Int = 1, dayOfMonth: Int? = null) = RecurrencePattern(
            type = RecurrenceType.MONTHLY,
            interval = interval,
            dayOfMonth = dayOfMonth
        )
    }
}
