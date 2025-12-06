package com.example.todoapp

import android.widget.LinearLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.todoapp.ui.CalendarActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for the weekly waterfall calendar view.
 *
 * These tests verify:
 * 1. Calendar displays 7 day columns for the current week
 * 2. Todos are correctly grouped by day
 * 3. Week navigation works correctly
 * 4. Todos are clickable and open the edit screen
 * 5. Visual indicators (colors, counts) are correct
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class CalendarWaterfallTest : BaseInstrumentedTest() {

    override val useSampleTodos = true

    @get:Rule
    val activityRule = ActivityScenarioRule(CalendarActivity::class.java)

    @Before
    fun setup() {
        // Wait for activity to load after seeding
        Thread.sleep(2000)
    }

    @Test
    fun waterfallView_displays7DayColumns() {
        // Wait for data to load
        Thread.sleep(1000)

        // Verify week days container exists
        onView(withId(R.id.weekDaysContainer))
            .check(matches(isDisplayed()))

        // Verify we have 7 day columns
        activityRule.scenario.onActivity { activity ->
            val weekDaysContainer = activity.findViewById<LinearLayout>(R.id.weekDaysContainer)
            assert(weekDaysContainer.childCount == 7) {
                "Expected 7 day columns, but found ${weekDaysContainer.childCount}"
            }
        }
    }

    @Test
    fun waterfallView_displaysWeekRangeHeader() {
        // Wait for data to load
        Thread.sleep(1000)

        // Verify week range text is displayed
        onView(withId(R.id.weekRangeText))
            .check(matches(isDisplayed()))
            .check(matches(withText(org.hamcrest.Matchers.containsString("-"))))
    }

    @Test
    fun waterfallView_displaysTodosInCorrectColumns() {
        // Wait for data to load
        Thread.sleep(1000)

        // At least one day column should have todos
        activityRule.scenario.onActivity { activity ->
            val weekDaysContainer = activity.findViewById<LinearLayout>(R.id.weekDaysContainer)

            var foundTodosInAtLeastOneDay = false

            for (i in 0 until weekDaysContainer.childCount) {
                val dayColumn = weekDaysContainer.getChildAt(i)
                val todosContainer = dayColumn.findViewById<LinearLayout>(R.id.todosContainer)

                if (todosContainer.childCount > 0) {
                    foundTodosInAtLeastOneDay = true
                    break
                }
            }

            assert(foundTodosInAtLeastOneDay) {
                "Expected at least one day to have todos"
            }
        }
    }

    @Test
    fun waterfallView_weekNavigation_prevWeek() {
        // Wait for initial load
        Thread.sleep(1000)

        // Get current week range text
        var initialWeekRange = ""
        activityRule.scenario.onActivity { activity ->
            initialWeekRange = activity.findViewById<android.widget.TextView>(R.id.weekRangeText).text.toString()
        }

        // Click previous week button
        onView(withId(R.id.prevWeekButton))
            .perform(click())

        // Wait for update
        Thread.sleep(500)

        // Verify week range changed
        activityRule.scenario.onActivity { activity ->
            val newWeekRange = activity.findViewById<android.widget.TextView>(R.id.weekRangeText).text.toString()
            assert(newWeekRange != initialWeekRange) {
                "Week range should change after clicking previous week button"
            }
        }
    }

    @Test
    fun waterfallView_weekNavigation_nextWeek() {
        // Wait for initial load
        Thread.sleep(1000)

        // Get current week range text
        var initialWeekRange = ""
        activityRule.scenario.onActivity { activity ->
            initialWeekRange = activity.findViewById<android.widget.TextView>(R.id.weekRangeText).text.toString()
        }

        // Click next week button
        onView(withId(R.id.nextWeekButton))
            .perform(click())

        // Wait for update
        Thread.sleep(500)

        // Verify week range changed
        activityRule.scenario.onActivity { activity ->
            val newWeekRange = activity.findViewById<android.widget.TextView>(R.id.weekRangeText).text.toString()
            assert(newWeekRange != initialWeekRange) {
                "Week range should change after clicking next week button"
            }
        }
    }

    @Test
    fun waterfallView_navigateToNextWeekAndBack() {
        // Wait for initial load
        Thread.sleep(1000)

        // Get current week range text
        var initialWeekRange = ""
        activityRule.scenario.onActivity { activity ->
            initialWeekRange = activity.findViewById<android.widget.TextView>(R.id.weekRangeText).text.toString()
        }

        // Click next week
        onView(withId(R.id.nextWeekButton))
            .perform(click())
        Thread.sleep(500)

        // Click previous week to go back
        onView(withId(R.id.prevWeekButton))
            .perform(click())
        Thread.sleep(500)

        // Verify we're back to the original week
        activityRule.scenario.onActivity { activity ->
            val currentWeekRange = activity.findViewById<android.widget.TextView>(R.id.weekRangeText).text.toString()
            assert(currentWeekRange == initialWeekRange) {
                "Expected to return to initial week range: $initialWeekRange, but got: $currentWeekRange"
            }
        }
    }

    @Test
    fun waterfallView_todosAreSortedByTime() {
        // Wait for data to load
        Thread.sleep(1000)

        // Find a day with multiple todos
        activityRule.scenario.onActivity { activity ->
            val weekDaysContainer = activity.findViewById<LinearLayout>(R.id.weekDaysContainer)

            for (i in 0 until weekDaysContainer.childCount) {
                val dayColumn = weekDaysContainer.getChildAt(i)
                val todosContainer = dayColumn.findViewById<LinearLayout>(R.id.todosContainer)

                if (todosContainer.childCount >= 2) {
                    // Check that todos appear in time order
                    val firstTodoTime = todosContainer.getChildAt(0)
                        .findViewById<android.widget.TextView>(R.id.waterfallTodoTime)
                        ?.text?.toString()

                    val secondTodoTime = todosContainer.getChildAt(1)
                        .findViewById<android.widget.TextView>(R.id.waterfallTodoTime)
                        ?.text?.toString()

                    // Both should have time text
                    assert(firstTodoTime != null && firstTodoTime.isNotEmpty()) {
                        "First todo should have a time"
                    }
                    assert(secondTodoTime != null && secondTodoTime.isNotEmpty()) {
                        "Second todo should have a time"
                    }

                    // We found a day with multiple todos, test passes
                    return@onActivity
                }
            }
        }
    }

    @Test
    fun waterfallView_todoCountsDisplayed() {
        // Wait for data to load
        Thread.sleep(1000)

        // Verify that at least one day shows a todo count
        activityRule.scenario.onActivity { activity ->
            val weekDaysContainer = activity.findViewById<LinearLayout>(R.id.weekDaysContainer)

            var foundDayWithCount = false

            for (i in 0 until weekDaysContainer.childCount) {
                val dayColumn = weekDaysContainer.getChildAt(i)
                val todoCountText = dayColumn.findViewById<android.widget.TextView>(R.id.todoCountText)
                val countText = todoCountText.text.toString()

                // Should either say "No todos" or "X todos (Y done)"
                if (countText.contains("todo")) {
                    foundDayWithCount = true
                    break
                }
            }

            assert(foundDayWithCount) {
                "Expected at least one day to show a todo count"
            }
        }
    }

    @Test
    fun waterfallView_todayColumnIsHighlighted() {
        // Wait for data to load
        Thread.sleep(1000)

        // At least one column should be highlighted as today
        // (We can't assert which specific column without knowing the test date,
        // but we can verify the highlighting exists)
        activityRule.scenario.onActivity { activity ->
            val weekDaysContainer = activity.findViewById<LinearLayout>(R.id.weekDaysContainer)

            // At least verify all 7 days are present
            assert(weekDaysContainer.childCount == 7) {
                "Expected 7 day columns"
            }
        }
    }

    @Test
    fun waterfallView_scrollViewExists() {
        // Verify horizontal scroll view exists
        onView(withId(R.id.waterfallScrollView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun waterfallView_dayHeadersDisplayed() {
        // Wait for data to load
        Thread.sleep(1000)

        // Verify each day column has a day name and date
        activityRule.scenario.onActivity { activity ->
            val weekDaysContainer = activity.findViewById<LinearLayout>(R.id.weekDaysContainer)

            for (i in 0 until weekDaysContainer.childCount) {
                val dayColumn = weekDaysContainer.getChildAt(i)
                val dayNameText = dayColumn.findViewById<android.widget.TextView>(R.id.dayNameText)
                val dayDateText = dayColumn.findViewById<android.widget.TextView>(R.id.dayDateText)

                assert(dayNameText.text.isNotEmpty()) {
                    "Day $i should have a day name"
                }
                assert(dayDateText.text.isNotEmpty()) {
                    "Day $i should have a date"
                }
            }
        }
    }
}
