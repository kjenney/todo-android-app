package com.example.todoapp

import androidx.test.core.app.ApplicationProvider
import com.example.todoapp.data.TodoDatabase
import com.example.todoapp.utils.TodoSeeder
import kotlinx.coroutines.runBlocking
import org.junit.Before

/**
 * Base class for instrumented tests.
 * Provides common setup including database seeding.
 */
abstract class BaseInstrumentedTest {

    /**
     * Whether to seed sample data before each test.
     * Override in subclass to disable seeding.
     */
    open val shouldSeedData: Boolean = true

    /**
     * Which seeding method to use.
     * Override in subclass to use different seeding strategy.
     */
    open val useSampleTodos: Boolean = true

    @Before
    fun baseSetup() {
        if (shouldSeedData) {
            seedTestData()
        }
        // Give the database and UI time to initialize
        Thread.sleep(1000)
    }

    /**
     * Seeds the database with test data.
     * Clears all existing data first.
     */
    private fun seedTestData() {
        runBlocking {
            val database = TodoDatabase.getDatabase(ApplicationProvider.getApplicationContext())

            // Clear existing data
            database.clearAllTables()

            // Insert sample todos
            val todos = if (useSampleTodos) {
                TodoSeeder.generateSampleTodos()
            } else {
                TodoSeeder.generateMinimalSampleTodos()
            }

            todos.forEach { todo ->
                database.todoDao().insert(todo)
            }
        }
    }

    /**
     * Clear all data from the database.
     * Useful for tests that need to start with a clean slate.
     */
    protected fun clearDatabase() {
        runBlocking {
            val database = TodoDatabase.getDatabase(ApplicationProvider.getApplicationContext())
            database.clearAllTables()
        }
    }
}
