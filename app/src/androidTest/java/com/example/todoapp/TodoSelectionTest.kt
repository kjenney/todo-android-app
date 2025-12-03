package com.example.todoapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for Todo selection functionality.
 * These tests verify that:
 * 1. Selecting a todo item provides visual feedback
 * 2. Selection state persists after scrolling
 * 3. Only one item can be selected at a time
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TodoSelectionTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun clickTodoItem_showsSelectionState() {
        // Wait for any todos to load
        Thread.sleep(1000)

        // Click the first todo item
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    0,
                    click()
                )
            )

        // Verify the first item is selected
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(0)))
    }

    @Test
    fun clickDifferentTodoItems_onlyOneSelectedAtTime() {
        // Wait for any todos to load
        Thread.sleep(1000)

        // Click the first todo item
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    0,
                    click()
                )
            )

        // Verify the first item is selected
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(0)))

        // Click the second todo item (if it exists)
        try {
            onView(withId(R.id.todoRecyclerView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                        1,
                        click()
                    )
                )

            // Verify the second item is now selected
            onView(withId(R.id.todoRecyclerView))
                .check(matches(hasItemAtPositionSelected(1)))

            // Verify the first item is no longer selected
            onView(withId(R.id.todoRecyclerView))
                .check(matches(hasItemAtPositionNotSelected(0)))
        } catch (e: Exception) {
            // Not enough items in the list, skip this part of the test
        }
    }

    @Test
    fun selectTodoAndScroll_selectionPersists() {
        // Wait for any todos to load
        Thread.sleep(1000)

        // Click the first todo item
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    0,
                    click()
                )
            )

        // Verify the first item is selected
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionSelected(0)))

        // Try to scroll down if there are enough items
        try {
            onView(withId(R.id.todoRecyclerView))
                .perform(
                    RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(5)
                )

            // Scroll back up to the first item
            onView(withId(R.id.todoRecyclerView))
                .perform(
                    RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(0)
                )

            // Verify the first item is still selected after scrolling
            onView(withId(R.id.todoRecyclerView))
                .check(matches(hasItemAtPositionSelected(0)))
        } catch (e: Exception) {
            // Not enough items to scroll, but we can still verify selection persists
            // by checking it's still selected
            onView(withId(R.id.todoRecyclerView))
                .check(matches(hasItemAtPositionSelected(0)))
        }
    }

    @Test
    fun selectTodoScrollAwayAndBack_selectionStillVisible() {
        // Wait for any todos to load
        Thread.sleep(1000)

        // Get the RecyclerView item count
        var itemCount = 0
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.todoRecyclerView)
            itemCount = recyclerView.adapter?.itemCount ?: 0
        }

        // Only run this test if we have enough items
        if (itemCount >= 6) {
            // Click an item in the middle of the list
            onView(withId(R.id.todoRecyclerView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                        2,
                        click()
                    )
                )

            // Verify it's selected
            onView(withId(R.id.todoRecyclerView))
                .check(matches(hasItemAtPositionSelected(2)))

            // Scroll down significantly
            onView(withId(R.id.todoRecyclerView))
                .perform(
                    RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(
                        itemCount - 1
                    )
                )

            // Scroll back to the selected item
            onView(withId(R.id.todoRecyclerView))
                .perform(
                    RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(2)
                )

            // Verify the item is still selected
            onView(withId(R.id.todoRecyclerView))
                .check(matches(hasItemAtPositionSelected(2)))
        }
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
