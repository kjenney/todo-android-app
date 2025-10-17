package com.example.todoapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromRecurrencePattern(pattern: RecurrencePattern): String {
        return gson.toJson(pattern)
    }

    @TypeConverter
    fun toRecurrencePattern(value: String): RecurrencePattern {
        val type = object : TypeToken<RecurrencePattern>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromSetInt(set: Set<Int>): String {
        return gson.toJson(set)
    }

    @TypeConverter
    fun toSetInt(value: String): Set<Int> {
        val type = object : TypeToken<Set<Int>>() {}.type
        return gson.fromJson(value, type)
    }
}
