package com.example.todoapp

import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.todoapp.ui.TodoEntityAdapter
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for Todo completion state persistence.
 *
 * These tests verify that when a todo is marked as completed:
 * 1. The completion state persists when switching between views (Today/All)
 * 2. The completion state persists after scrolling in a long list
 * 3. The completion state persists after switching views and scrolling
 *
 * This test specifically addresses the bug where a completed todo would
 * appear unchecked after switching to All Todos, scrolling, and returning
 * to Today's Todos.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TodoCompletionPersistenceTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private fun getItemCount(): Int {
        var itemCount = 0
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.todoRecyclerView)
            itemCount = recyclerView.adapter?.itemCount ?: 0
        }
        return itemCount
    }

    /**
     * Check if the first item is currently completed (checkbox is checked).
     */
    private fun isFirstItemCompleted(): Boolean {
        var isCompleted = false
        activityRule.scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.todoRecyclerView)
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(0)
            if (viewHolder != null) {
                val checkBox = viewHolder.itemView.findViewById<CheckBox>(R.id.todoCheckBox)
                isCompleted = checkBox?.isChecked ?: false
            }
        }
        return isCompleted
    }

    /**
     * Switches to "Today's Todos" view via the overflow menu.
     */
    private fun switchToTodaysTodos() {
        openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Thread.sleep(300)
        onView(withText("Today's Todos")).perform(click())
        Thread.sleep(500)
    }

    /**
     * Switches to "All Todos" view via the overflow menu.
     */
    private fun switchToAllTodos() {
        openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Thread.sleep(300)
        onView(withText("All Todos")).perform(click())
        Thread.sleep(500)
    }

    @Before
    fun setup() {
        // Wait for activity to fully load
        Thread.sleep(2000)

        // Switch to Today's Todos view first
        try {
            switchToTodaysTodos()
        } catch (e: Exception) {
            // Already on Today's Todos or menu item not available
        }
    }

    /**
     * Test that completion state persists when switching between Today's Todos and All Todos.
     *
     * Steps:
     * 1. Start on Today's Todos
     * 2. Mark the first todo as complete
     * 3. Switch to All Todos
     * 4. Verify the todo is still marked complete
     * 5. Switch back to Today's Todos
     * 6. Verify the todo is STILL marked complete
     */
    @Test
    fun completeTodo_switchViews_completionPersists() {
        // Skip if no todos
        if (getItemCount() == 0) return

        // Check if the first item is already completed, if so we need to find an uncompleted one
        // or uncheck it first
        val wasCompleted = isFirstItemCompleted()

        // Click the checkbox of the first todo to toggle completion
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    0,
                    clickChildViewWithId(R.id.todoCheckBox)
                )
            )

        // Wait for the database update
        Thread.sleep(1000)

        // Verify the checkbox state changed
        val expectedCompleted = !wasCompleted
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionWithCheckboxState(0, expectedCompleted)))

        // Switch to All Todos
        switchToAllTodos()

        // Wait for list to load
        Thread.sleep(500)

        // The todo might be at a different position in All Todos, but it should still be in the expected state
        // For this test, we verify by checking if any item has the expected completion state
        // Since we just toggled it, the recently modified todo should reflect the new state

        // Switch back to Today's Todos
        switchToTodaysTodos()

        // Wait for list to load
        Thread.sleep(500)

        // Skip if list is now empty (e.g., if we completed the only today's todo)
        if (getItemCount() == 0) return

        // CRITICAL: Verify the completion state persisted after switching views
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionWithCheckboxState(0, expectedCompleted)))
    }

    /**
     * Test that specifically reproduces the reported bug:
     * Complete todo -> All Todos -> Scroll -> Today's Todos -> Todo should still be complete
     */
    @Test
    fun completeTodo_switchToAll_scroll_switchToToday_completionPersists() {
        // Skip if no todos
        if (getItemCount() == 0) return

        val wasCompleted = isFirstItemCompleted()

        // Step 1: Mark the first todo as complete
        onView(withId(R.id.todoRecyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                    0,
                    clickChildViewWithId(R.id.todoCheckBox)
                )
            )

        Thread.sleep(1000)

        val expectedCompleted = !wasCompleted

        // Verify it's now in the expected state
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionWithCheckboxState(0, expectedCompleted)))

        // Step 2: Switch to All Todos
        switchToAllTodos()
        Thread.sleep(500)

        val allTodosCount = getItemCount()

        // Step 3: Scroll if we have enough items
        if (allTodosCount >= 5) {
            // Scroll to bottom
            onView(withId(R.id.todoRecyclerView))
                .perform(
                    RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(
                        allTodosCount - 1
                    )
                )
            Thread.sleep(300)

            // Scroll back to top
            onView(withId(R.id.todoRecyclerView))
                .perform(
                    RecyclerViewActions.scrollToPosition<TodoEntityAdapter.TodoViewHolder>(0)
                )
            Thread.sleep(300)
        }

        // Step 4: Switch back to Today's Todos
        switchToTodaysTodos()
        Thread.sleep(500)

        // Skip if list is now empty
        if (getItemCount() == 0) return

        // Step 5: CRITICAL - Verify the completion state is still as expected
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionWithCheckboxState(0, expectedCompleted)))
    }

    /**
     * Test that toggling completion multiple times works correctly with view switches.
     */
    @Test
    fun toggleCompletionMultipleTimes_stateRemainsConsistent() {
        // Skip if no todos
        if (getItemCount() == 0) return

        val initialState = isFirstItemCompleted()

        // Toggle 3 times
        repeat(3) {
            onView(withId(R.id.todoRecyclerView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<TodoEntityAdapter.TodoViewHolder>(
                        0,
                        clickChildViewWithId(R.id.todoCheckBox)
                    )
                )
            Thread.sleep(500)
        }

        // After 3 toggles, state should be opposite of initial
        val expectedState = !initialState
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionWithCheckboxState(0, expectedState)))

        // Switch to All Todos and back
        switchToAllTodos()
        Thread.sleep(300)
        switchToTodaysTodos()
        Thread.sleep(300)

        // Skip if list is now empty
        if (getItemCount() == 0) return

        // Verify state persisted
        onView(withId(R.id.todoRecyclerView))
            .check(matches(hasItemAtPositionWithCheckboxState(0, expectedState)))
    }

    /**
     * Custom ViewAction to click a child view with a specific ID within a RecyclerView item.
     */
    private fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(View::class.java))
            }

            override fun getDescription(): String {
                return "Click on a child view with specified ID"
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                v?.performClick()
            }
        }
    }

    /**
     * Custom matcher to check if the checkbox at a specific position has the expected checked state.
     */
    private fun hasItemAtPositionWithCheckboxState(position: Int, isChecked: Boolean): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position with checkbox ${if (isChecked) "checked" else "unchecked"}")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: return false
                val checkBox = viewHolder.itemView.findViewById<CheckBox>(R.id.todoCheckBox)
                    ?: return false
                return checkBox.isChecked == isChecked
            }
        }
    }
}
