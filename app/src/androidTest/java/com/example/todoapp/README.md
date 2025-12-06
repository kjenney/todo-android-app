# Android Instrumented Tests

This directory contains instrumented tests for the Todo App, which run on an Android device or emulator.

## Test Data Seeding

**All instrumented tests automatically seed sample data before running.** This ensures consistent and reliable test execution both locally and in CI/CD pipelines.

### How Seeding Works

1. **BaseInstrumentedTest** - All test classes extend this base class
2. **Automatic Seeding** - The base class automatically:
   - Clears the database before each test
   - Seeds sample todos using `TodoSeeder.generateSampleTodos()`
   - Waits for initialization to complete

3. **Sample Data** - The seeder creates ~25 realistic todos:
   - Spread across the current week (Monday-Sunday)
   - Various times throughout each day
   - Mix of completed and pending todos
   - Some with recurrence patterns
   - Overdue todos from last week
   - Future todos for next week

## Test Files

### TodoSelectionTest.kt
Basic selection tests with seeded data:
- `clickTodoItem_showsSelectionState()` - Verifies that clicking a todo item shows visual selection feedback
- `clickDifferentTodoItems_onlyOneSelectedAtTime()` - Ensures only one todo can be selected at a time
- `selectTodoAndScroll_selectionPersists()` - Verifies selection state persists after scrolling
- `selectTodoScrollAwayAndBack_selectionStillVisible()` - Tests selection persistence with extensive scrolling

### TodoSelectionWithDataTest.kt
Comprehensive selection tests with guaranteed test data:
- `selectFirstTodo_hasVisualFeedback()` - Verifies selection visual feedback on first item
- `selectTodo_scrollDown_scrollUp_selectionPersists()` - Tests selection persistence through scroll operations
- `selectMultipleTodos_onlyLastOneRemembered()` - Verifies only the last selected item remains selected
- `selectMiddleTodo_scrollToEnds_selectionPersists()` - Tests selection persistence with scrolling to extremes

### TodoCompletionPersistenceTest.kt
Tests for completion state persistence (fixes checkbox listener bug):
- `completeTodo_switchViews_completionPersists()` - Tests completion state when switching between Today/All views
- `completeTodo_switchToAll_scroll_switchToToday_completionPersists()` - Reproduces and tests the reported bug scenario
- `toggleCompletionMultipleTimes_stateRemainsConsistent()` - Tests multiple toggle operations

### CalendarWaterfallTest.kt
Tests for the weekly waterfall calendar view:
- `waterfallView_displays7DayColumns()` - Verifies 7 day columns are displayed
- `waterfallView_displaysTodosInCorrectColumns()` - Todos appear in correct day columns
- `waterfallView_weekNavigation_prevWeek()` / `nextWeek()` - Week navigation buttons
- `waterfallView_todosAreSortedByTime()` - Todos sorted by time within each day
- `waterfallView_todoCountsDisplayed()` - Todo count badges shown
- `waterfallView_dayHeadersDisplayed()` - Day names and dates shown

## Running Tests Locally

### Prerequisites
- Android device or emulator running (API 24+)
- ADB connection established

### Run all instrumented tests
```bash
./gradlew connectedAndroidTest
```

### Run a specific test class
```bash
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.example.todoapp.TodoSelectionTest
```

### Run a specific test method
```bash
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.example.todoapp.TodoSelectionTest#clickTodoItem_showsSelectionState
```

### With Docker
```bash
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest \
  ./gradlew connectedAndroidTest
```

### View test results
After running tests, reports are generated at:
- HTML Report: `app/build/reports/androidTests/connected/index.html`
- XML Results: `app/build/outputs/androidTest-results/`

## CI/CD Integration

Tests automatically run on GitHub Actions when:
- A pull request is created or updated
- Code is pushed to the main branch

**Data seeding is automatic** - no special workflow configuration needed! Each test:
1. Extends `BaseInstrumentedTest`
2. Automatically seeds data in `@Before` setup
3. Runs with consistent, reliable test data

The workflow is defined in `.github/workflows/run-tests.yml`

### Test Results in CI
- Test results are uploaded as artifacts in GitHub Actions
- Available for 30 days after the workflow run
- Access via: Actions → Workflow Run → Artifacts

## Customizing Test Data

### Using Minimal Sample Data
```kotlin
@RunWith(AndroidJUnit4::class)
@LargeTest
class MyTest : BaseInstrumentedTest() {

    override val useSampleTodos = false  // Uses 3 minimal todos instead of 25

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
}
```

### Disabling Automatic Seeding
```kotlin
@RunWith(AndroidJUnit4::class)
@LargeTest
class MyTest : BaseInstrumentedTest() {

    override val shouldSeedData = false  // No automatic seeding

    @Test
    fun testWithCustomData() {
        // Clear and add your own data
        clearDatabase()
        // ... custom data setup
    }
}
```

### Manual Database Operations
```kotlin
@Test
fun testWithCustomData() {
    // Clear database
    clearDatabase()

    // Add your own test data
    runBlocking {
        val database = TodoDatabase.getDatabase(ApplicationProvider.getApplicationContext())
        database.todoDao().insert(myCustomTodo)
    }
}
```

## Test Architecture

### BaseInstrumentedTest
All test classes extend this base class which provides:
- Automatic database clearing before each test
- Automatic seeding with `TodoSeeder`
- Helper method `clearDatabase()` for manual control
- Configurable seeding behavior

### TodoSeeder Utility
Located in `app/src/main/java/com/example/todoapp/utils/TodoSeeder.kt`:
- `generateSampleTodos()` - Creates ~25 realistic todos across the week
- `generateMinimalSampleTodos()` - Creates 3 quick test todos

### Custom Matchers
Test files include custom Espresso matchers:
- `hasItemAtPositionSelected(position)` - Checks if RecyclerView item is selected
- `hasItemAtPositionNotSelected(position)` - Checks if RecyclerView item is NOT selected
- `hasItemAtPositionWithCheckboxState(position, isChecked)` - Checks checkbox state

## Troubleshooting

### Tests fail with "No activities found"
- Ensure an emulator is running or device is connected
- Check ADB connection: `adb devices`

### Tests fail with no data
- Verify test class extends `BaseInstrumentedTest`
- Check `shouldSeedData = true` (default)
- Ensure database permissions are correct

### Tests run slowly
- Seeding adds ~1-2 seconds per test
- Use `useSampleTodos = false` for faster minimal seeding
- Or `shouldSeedData = false` to disable seeding

### Tests timeout
- Increase timeout in test annotations
- Check emulator performance
- Reduce animation duration: Developer Options → Animation scale → 0.5x

### RecyclerView item not found
- Data is automatically seeded - should not happen
- Check view modes (Today/All) show expected todos
- Verify wait times are sufficient

## Adding New Tests

When creating new test classes, simply extend `BaseInstrumentedTest`:

```kotlin
package com.example.todoapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MyNewTest : BaseInstrumentedTest() {  // Data seeding is automatic!

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun myTest() {
        // Your test code - ~25 sample todos are already seeded!
    }
}
```

That's it! No manual seeding required - it's handled automatically by the base class.
