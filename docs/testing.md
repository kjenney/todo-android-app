# Testing

The project includes comprehensive instrumented tests using Espresso with automatic test data seeding.

## Test Data Seeding

**All instrumented tests automatically seed sample data before running.** This ensures consistent and reliable test execution both locally and in CI/CD pipelines.

See the [androidTest README](../app/src/androidTest/java/com/example/todoapp/README.md) for complete seeding documentation.

## Prerequisites for Local Testing

### Option 1: Using Docker (Recommended)

Docker provides a consistent environment with Java and Android SDK pre-configured.

### Option 2: Local Installation

- **Java 17** (JDK): Required for building and running tests
- **Android SDK**: Platform SDK 34, Build Tools, Platform Tools
- **Android Emulator** or physical device (API 24+)

## Running Tests with Docker

**Unit Tests (no emulator required):**

```bash
# Run unit tests
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest ./gradlew test

# View results
open app/build/reports/tests/testDebugUnitTest/index.html
```

**Build APK for Testing:**

```bash
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest \
  ./gradlew assembleDebug assembleDebugAndroidTest
```

## Running Instrumented Tests

```bash
# Verify device is connected
adb devices

# Run all instrumented tests
./gradlew connectedAndroidTest

# Run a specific test class
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.example.todoapp.TodoSelectionTest
```

## Test Coverage

- **TodoSelectionTest** - Visual feedback and selection persistence
- **TodoSelectionWithDataTest** - Selection with guaranteed data
- **TodoCompletionPersistenceTest** - Completion state persistence
- **CalendarWaterfallTest** - Weekly waterfall calendar view

See the [androidTest README](../app/src/androidTest/java/com/example/todoapp/README.md) for details.

## Automated Testing with GitHub Actions

Tests automatically run on every pull request and push to main:

- Unit tests run on Ubuntu
- Instrumented tests run on Android emulator (API 29)
- Test results available as artifacts for 30 days
