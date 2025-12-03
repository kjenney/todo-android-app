package com.example.todoapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.todoapp.ui.TodoEntityAdapter
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for Todo selection functionality with guaranteed test data.
 * These tests create test todos before running to ensure consistent test behavior.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TodoSelectionWithDataTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        // Wait for activity to load
        Thread.sleep(500)

        // Add multiple test todos to ensure we have data
        addTestTodo("Test Todo 1")
        addTestTodo("Test Todo 2")
        addTestTodo("Test Todo 3")
        addTestTodo("Test Todo 4")
        addTestTodo("Test Todo 5")

        // Wait for todos to be added and displayed
        Thread.sleep(1000)
    }

    private fun addTestTodo(todoText: String) {
        // Click FAB to open add todo screen
        onView(withId(R.id.fab)).perform(click())

        // Wait for activity to open
        Thread.sleep(300)

        // Enter todo text
        onView(withId(R.id.editTextTodo)).perform(typeText(todoText), closeSoftKeyboard())

        // Save the todo
        onView(withId(R.id.buttonSave)).perform(click())

        // Wait for activity to close
        Thread.sleep(300)
    }

    @Test
    fun selectFirstTodo_hasVisualFeedback() {
        // Click the first todo item
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    0,
                    click()
                )
            )

        // Wait for selection to take effect
        Thread.sleep(200)

        // Verify the first item is selected
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(0)))
    }

    @Test
    fun selectTodo_scrollDown_scrollUp_selectionPersists() {
        // Select the first todo
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    0,
                    click()
                )
            )

        Thread.sleep(200)

        // Verify it's selected
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(0)))

        // Scroll to the last item
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(4)
            )

        Thread.sleep(200)

        // Scroll back to the first item
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(0)
            )

        Thread.sleep(200)

        // Verify the first item is STILL selected after scrolling
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(0)))
    }

    @Test
    fun selectMultipleTodos_onlyLastOneRemembered() {
        // Select first todo
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    0,
                    click()
                )
            )

        Thread.sleep(200)

        // Verify first is selected
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(0)))

        // Select second todo
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    1,
                    click()
                )
            )

        Thread.sleep(200)

        // Verify second is selected and first is not
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(1)))

        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionNotSelected(0)))

        // Select third todo
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    2,
                    click()
                )
            )

        Thread.sleep(200)

        // Verify third is selected, others are not
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(2)))

        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionNotSelected(0)))

        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionNotSelected(1)))
    }

    @Test
    fun selectMiddleTodo_scrollToEnds_selectionPersists() {
        // Select the middle todo (position 2)
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    2,
                    click()
                )
            )

        Thread.sleep(200)

        // Verify it's selected
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(2)))

        // Scroll to top
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(0)
            )

        Thread.sleep(200)

        // Scroll to bottom
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(4)
            )

        Thread.sleep(200)

        // Scroll back to middle
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(2)
            )

        Thread.sleep(200)

        // Verify middle todo is still selected
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(2)))
    }

    // Custom matcher to check if an item at a specific position is selected
    private fun hasItemAtPositionSelected(position: Int): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position selected")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: return false
                return viewHolder.itemView.isSelected
            }
        }
    }

    // Custom matcher to check if an item at a specific position is NOT selected
    private fun hasItemAtPositionNotSelected(position: Int): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position not selected")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: return false
                return !viewHolder.itemView.isSelected
            }
        }
    }
}
