package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TodoEntity::class, TodoCompletionHistory::class],
    version = 2,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoCompletionHistoryDao(): TodoCompletionHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                )
                .fallbackToDestructiveMigration() // Simplified: recreate DB on schema changes
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
