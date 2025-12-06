# Android Instrumented Tests

This directory contains instrumented tests for the Todo App, which run on an Android device or emulator.

## Test Files

### TodoSelectionTest.kt
Basic selection tests that work with existing data in the app:
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
./gradlew connectedAndroidTest --tests "com.example.todoapp.TodoSelectionTest"
```

### Run a specific test method
```bash
./gradlew connectedAndroidTest --tests "com.example.todoapp.TodoSelectionTest.clickTodoItem_showsSelectionState"
```

### View test results
After running tests, reports are generated at:
- HTML Report: `app/build/reports/androidTests/connected/index.html`
- XML Results: `app/build/outputs/androidTest-results/`

## CI/CD Integration

Tests automatically run on GitHub Actions when:
- A pull request is created or updated
- Code is pushed to the main branch

The workflow is defined in `.github/workflows/run-tests.yml`

### Test Results in CI
- Test results are uploaded as artifacts in GitHub Actions
- Available for 30 days after the workflow run
- Access via: Actions → Workflow Run → Artifacts

## Test Architecture

### Custom Matchers
Both test files include custom Espresso matchers:
- `hasItemAtPositionSelected(position)` - Checks if a RecyclerView item at a specific position has `isSelected = true`
- `hasItemAtPositionNotSelected(position)` - Checks if a RecyclerView item at a specific position has `isSelected = false`

These matchers work by:
1. Finding the ViewHolder at the specified adapter position
2. Checking the `isSelected` state of the ViewHolder's itemView
3. Returning true/false based on the selection state

### Test Data Setup
`TodoSelectionWithDataTest` uses a `@Before` setup method that:
1. Adds 5 test todos before each test
2. Ensures consistent test environment
3. Provides enough data for scrolling tests

## Troubleshooting

### Tests fail with "No activities found"
- Ensure an emulator is running or device is connected
- Check ADB connection: `adb devices`

### Tests timeout
- Increase timeout in test annotations
- Check emulator performance
- Reduce animation duration on test device

### RecyclerView item not found
- Verify test data is being created
- Check that view modes (Today/All) show the expected todos
- Ensure proper wait times for data loading

## Adding New Tests

When adding new selection-related tests:
1. Use the existing custom matchers for consistency
2. Add appropriate wait times for UI operations
3. Consider both empty and populated data scenarios
4. Test edge cases (first item, last item, scrolling)
5. Verify cleanup between test runs
