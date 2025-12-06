# Todo App

A feature-rich Android todo list application with recurring tasks, notifications, calendar view, and home screen widget support.

## Features

### Core Todo Management
- **Checkbox Completion**: Check off todos to mark them as complete/incomplete
- **Strikethrough Effect**: Completed todos are visually distinguished with strikethrough text
- **Edit Todos**: Tap any todo to edit all its properties (text, due date, recurrence, notifications)
- **Delete Functionality**: Remove todos with a simple tap on the delete button
- **Floating Action Button**: Quick access to add new todos

### Smart Scheduling
- **Due Dates and Times**: Set specific due dates and times for todos
- **Date/Time Pickers**: Intuitive date and time selection dialogs
- **Custom Times**: Schedule todos at any time (e.g., 1:32 PM every Thursday)

### Recurring Tasks
- **Multiple Recurrence Types**:
  - Hourly (every N hours)
  - Daily (every N days)
  - Weekly (with specific days of the week selection)
  - Monthly (every N months)
  - Yearly (every N years)
- **Custom Intervals**: Set any interval (e.g., every 2 hours, every 3 days)
- **Days of Week**: For weekly recurrence, select specific days (Mon, Tue, Wed, etc.)
- **Automatic Generation**: When a recurring todo is completed, the next occurrence is automatically created

### Notifications
- **Due Date Reminders**: Receive notifications when todos are due
- **Enable/Disable**: Toggle notifications per todo
- **WorkManager Integration**: Reliable notification scheduling even when app is closed

### Local Storage & History
- **Room Database**: All todos stored locally with SQLite
- **Completion History**: Track when todos were completed
- **Historical Data**: View past completion records with date and time
- **Persistent Data**: Todos survive app restarts

### View Modes
- **Today View** (Default): Shows only todos due today
- **All Todos View**: Display all todos regardless of due date
- **Date-Specific View**: Filter todos by selecting a specific date in calendar

### Calendar View
- **Interactive Calendar**: Visual calendar interface with month navigation
- **Date Selection**: Tap any date to view todos for that day
- **Past, Current & Future**: View todos across all time periods
- **Visual Feedback**: See which dates have todos

### Home Screen Widgets

**Todo List Widget:**
- **Quick Access**: View today's todos directly from home screen
- **Check Off Items**: Complete todos without opening the app
- **Auto-Update**: Widget refreshes to show current todos
- **Resizable**: Adjust widget size to fit your home screen layout

**Calendar Widget:**
- **Monthly View**: See the current month at a glance
- **Visual Indicators**: Days with todos show a small dot indicator
- **Today Highlight**: Current day highlighted in purple
- **Quick Todo List**: Shows up to 5 of today's todos below the calendar
- **Tap to Open**: Tap header to open calendar view in app
- **Compact Design**: Fits nicely on home screen while showing useful info

## Technology Stack

- **Language**: Kotlin
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Async**: Kotlin Coroutines + Flow
- **UI Framework**: Material Design Components
- **Notifications**: WorkManager
- **Calendar**: Kizitonwose Calendar View

## Dependencies

### Core Libraries
- AndroidX Core KTX 1.12.0
- AndroidX AppCompat 1.6.1
- Material Components 1.11.0
- ConstraintLayout 2.1.4
- RecyclerView 1.3.2

### Architecture & Database
- Room Runtime 2.6.1
- Room KTX 2.6.1
- Lifecycle ViewModel KTX 2.7.0
- Lifecycle LiveData KTX 2.7.0

### Async & Background Work
- Kotlin Coroutines Android 1.7.3
- WorkManager Runtime KTX 2.9.0

### UI Components
- Calendar View 2.4.1
- Gson 2.10.1 (for Room converters)

## Project Structure

```
app/src/main/java/com/example/todoapp/
├── MainActivity.kt                       # Main activity with today's todos
├── data/
│   ├── TodoEntity.kt                    # Todo data model
│   ├── RecurrencePattern.kt             # Recurrence configuration
│   ├── TodoCompletionHistory.kt         # Completion tracking
│   ├── Converters.kt                    # Room type converters
│   ├── TodoDao.kt                       # Database access
│   ├── TodoCompletionHistoryDao.kt      # History access
│   ├── TodoDatabase.kt                  # Room database
│   └── TodoRepository.kt                # Data layer abstraction
├── ui/
│   ├── TodoViewModel.kt                 # ViewModel with view modes
│   ├── TodoEntityAdapter.kt             # RecyclerView adapter
│   ├── AddTodoActivity.kt               # Add/edit todo screen
│   └── CalendarActivity.kt              # Calendar view screen
├── notifications/
│   ├── TodoNotificationWorker.kt        # Notification worker
│   └── TodoNotificationScheduler.kt     # Notification scheduling
└── widget/
    ├── TodoWidget.kt                    # Home screen widget provider
    └── TodoWidgetService.kt             # Widget data service
```

## Building the Project

### Prerequisites

- Android Studio (Arctic Fox or later recommended)
- JDK 8 or higher
- Android SDK with API 34

### Build Steps

1. Clone the repository
2. Open the project in Android Studio
3. Let Gradle sync the project dependencies
4. Run the app on an emulator or physical device (API 24+)

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Clean build
./gradlew clean build
```

### Automated Builds with GitHub Actions

The project includes automated CI/CD workflows:

**On every push/PR to main:**
- Automatically builds debug and release APKs
- APKs available as artifacts in GitHub Actions (30 day retention)
- Download from: Actions → Latest Run → Artifacts

**On version tags (e.g., v1.0.0):**
- Builds signed release APK (requires keystore secrets)
- Automatically creates GitHub Release with APK attached
- See `.github/workflows/README.md` for setup instructions

**To create a release:**
```bash
git tag v1.0.0
git push origin v1.0.0
```

## Testing

The project includes comprehensive instrumented tests using Espresso.

### Prerequisites for Local Testing

**Option 1: Using Docker (Recommended)**

Docker provides a consistent environment with Java and Android SDK pre-configured:

```bash
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest ./gradlew assembleDebug
```

For full instrumented test support with emulator, see the Docker Compose setup below.

**Option 2: Local Installation**

- **Java 17** (JDK): Required for building and running tests
  ```bash
  # macOS (Homebrew)
  brew install openjdk@17

  # Ubuntu/Debian
  sudo apt install openjdk-17-jdk

  # Verify installation
  java -version
  ```

- **Android SDK**: Install via Android Studio or command line tools
  - Download from: https://developer.android.com/studio
  - Required SDK components: Platform SDK 34, Build Tools, Platform Tools

- **Android Emulator** or physical device (API 24+)

**Environment Setup (for local installation):**
```bash
# Set JAVA_HOME (add to ~/.bashrc or ~/.zshrc)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)  # macOS
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk     # Linux

# Set ANDROID_HOME (add to ~/.bashrc or ~/.zshrc)
export ANDROID_HOME=$HOME/Library/Android/sdk     # macOS
export ANDROID_HOME=$HOME/Android/Sdk             # Linux
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools
```

### Running Tests with Docker

**Unit Tests (no emulator required):**
```bash
# Run unit tests
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest ./gradlew test

# View results after running
open app/build/reports/tests/testDebugUnitTest/index.html
```

**Build APK for Testing:**
```bash
# Build debug APK
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest ./gradlew assembleDebug

# Build test APK
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest ./gradlew assembleDebugAndroidTest
```

**Instrumented Tests with Docker + Local Emulator:**

For instrumented tests, you can use Docker for building and a local emulator for running:

```bash
# 1. Build test APKs with Docker
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest \
  ./gradlew assembleDebug assembleDebugAndroidTest

# 2. Start your local emulator (see "Setting Up an Android Emulator" below)
emulator -avd test_device &
adb wait-for-device

# 3. Install and run tests
adb install app/build/outputs/apk/debug/app-debug.apk
adb install app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w com.example.todoapp.test/androidx.test.runner.AndroidJUnitRunner
```

### Setting Up an Android Emulator

**Option 1: Using Android Studio**
1. Open Android Studio → Tools → Device Manager
2. Click "Create Device"
3. Select a device (e.g., Pixel 6)
4. Select system image: API 29 (Android 10) or higher
5. Click "Finish" and start the emulator

**Option 2: Using Command Line**
```bash
# List available system images
sdkmanager --list | grep system-images

# Install a system image (API 29 recommended for CI parity)
sdkmanager "system-images;android-29;default;x86_64"

# Create an AVD (Android Virtual Device)
avdmanager create avd -n test_device -k "system-images;android-29;default;x86_64" --device "pixel"

# List available emulators
emulator -list-avds

# Start the emulator
emulator -avd test_device -no-window -gpu swiftshader_indirect &

# Wait for emulator to boot (check with adb)
adb wait-for-device
adb shell getprop sys.boot_completed  # Returns 1 when ready
```

### Running Unit Tests

Unit tests run on the JVM without an emulator:

```bash
# Run all unit tests
./gradlew test

# Run with detailed output
./gradlew test --info

# View results
open app/build/reports/tests/testDebugUnitTest/index.html
```

### Running Instrumented Tests

Instrumented tests require a running emulator or connected device:

```bash
# Verify device is connected
adb devices

# Run all instrumented tests
./gradlew connectedAndroidTest

# Run with stacktrace for debugging
./gradlew connectedAndroidTest --stacktrace

# Run a specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.todoapp.TodoSelectionTest

# Run a specific test method
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.todoapp.TodoSelectionTest#clickTodoItem_showsSelectionState

# Run tests matching a pattern
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=com.example.todoapp
```

### Viewing Test Results

```bash
# Instrumented test HTML report
open app/build/reports/androidTests/connected/index.html

# Instrumented test XML results (for CI parsing)
ls app/build/outputs/androidTest-results/connected/

# Unit test HTML report
open app/build/reports/tests/testDebugUnitTest/index.html
```

### Test Coverage

**Selection Tests (`TodoSelectionTest.kt`):**
- Visual feedback when selecting todos
- Selection persistence after scrolling
- Single selection enforcement (only one item selected at a time)
- Selection state retention through RecyclerView recycling

**Selection with Data Tests (`TodoSelectionWithDataTest.kt`):**
- Tests that create their own test data
- More reliable for fresh installations

**Completion Persistence Tests (`TodoCompletionPersistenceTest.kt`):**
- Completion state persists when switching views (Today/All Todos)
- Completion state persists after scrolling in long lists
- Multiple toggle operations maintain consistent state

### Troubleshooting

**"Unable to locate a Java Runtime"**
```bash
# Option 1: Use Docker instead (recommended)
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest ./gradlew test

# Option 2: Install Java 17 locally
brew install openjdk@17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

**Docker build is slow**
```bash
# Create a Gradle cache volume for faster subsequent builds
docker run --rm \
  -v "$(pwd)":/app \
  -v gradle-cache:/root/.gradle \
  -w /app \
  thyrlian/android-sdk:latest ./gradlew test
```

**"No connected devices"**
```bash
# Check if emulator is running
adb devices

# If empty, start an emulator
emulator -avd test_device &
adb wait-for-device
```

**"Test timed out"**
- Increase timeout: `./gradlew connectedAndroidTest --info -Pandroid.testInstrumentationRunnerArguments.timeout_msec=300000`
- Check if emulator is responsive: `adb shell input keyevent 82`

**"Could not install APK"**
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug assembleDebugAndroidTest
./gradlew connectedAndroidTest
```

**Tests pass locally but fail on CI**
- CI uses API 29 emulator with specific settings
- Run locally with similar config:
  ```bash
  emulator -avd test_device -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim
  ```

### Automated Testing with GitHub Actions

Tests automatically run on every pull request and push to main:
- Unit tests run on Ubuntu
- Instrumented tests run on Android emulator (API 29)
- Test results available as artifacts for 30 days
- Workflow: `.github/workflows/run-tests.yml`

## Usage Guide

### Adding a Todo
1. Tap the floating action button (FAB) in the bottom right
2. Enter the todo text
3. (Optional) Set due date and time using the date/time pickers
4. (Optional) Configure recurrence pattern:
   - Select recurrence type (None, Hourly, Daily, Weekly, Monthly, Yearly)
   - Set interval (e.g., every 2 days)
   - For weekly: select specific days of the week
5. Enable/disable notifications
6. Tap "Save"

### Editing a Todo
1. Tap on any todo item in the list
2. The edit screen opens with all current values pre-filled
3. Modify any fields you want to change
4. Tap "Update" to save changes
5. Changes are reflected immediately in all views

### Managing Todos
- **Complete**: Tap the checkbox next to a todo
- **Edit**: Tap anywhere on a todo item to edit its details
- **Delete**: Tap the trash icon
- **View Details**: Todos show due date and recurrence info below the text

### Viewing Modes
1. **Today's Todos**: Default view showing only today's items
   - Access: Launches by default, or Menu → Today
2. **All Todos**: View all todos regardless of date
   - Access: Menu → All Todos
3. **Calendar View**: Visual calendar with date-specific todos
   - Access: Menu → Calendar View
   - Tap any date to see todos for that day

### Filter Options
- **Hide Completed**: Toggle to hide/show completed todos
  - Access: Menu → Hide Completed (checkable option)
  - Applies to all view modes (Today, All Todos, Calendar)
  - Useful for focusing on active tasks
  - State persists while app is running

### Adding Widgets

**Todo List Widget:**
1. Long press on home screen
2. Select "Widgets"
3. Find "Todo App" → "Today's Todos" widget
4. Drag to desired location
5. Widget shows today's todos and updates automatically

**Calendar Widget:**
1. Long press on home screen
2. Select "Widgets"
3. Find "Todo App" → "Calendar" widget
4. Drag to desired location
5. Widget shows current month with todo indicators
6. Today's todos appear below the calendar
7. Tap any day to see todos for that date (opens app)

## Permissions

The app requires the following permissions:
- `POST_NOTIFICATIONS`: Show todo notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM`: Schedule exact alarm for due date reminders (Android 12+)
- `USE_EXACT_ALARM`: Use exact alarm API
- `WAKE_LOCK`: Wake device for notifications
- `RECEIVE_BOOT_COMPLETED`: Reschedule notifications after device restart
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: Request exemption from battery optimization

### Setting Up Notifications (Important for Pixel/Android 12+)

To ensure you receive notifications when your phone is idle or screen is off:

1. **Grant Notification Permission** (Android 13+):
   - The app will request this on first launch
   - Or go to: Settings → Apps → Todo App → Notifications → Allow

2. **Enable Exact Alarms** (Android 12+):
   - The app will guide you to: Settings → Apps → Todo App → Alarms & reminders → Allow
   - This ensures notifications appear exactly when todos are due

3. **Disable Battery Optimization** (Critical for background notifications):
   - The app will prompt you to exempt it from battery optimization
   - Or manually: Settings → Apps → Todo App → Battery → Unrestricted
   - **This is the most important step for Pixel phones!**

4. **Additional Pixel-Specific Settings**:
   - Settings → Apps → Todo App → Battery → Battery optimization → Not optimized
   - Settings → Battery → Adaptive preferences → Disable for Todo App
   - Settings → Apps → Special app access → Battery optimization → Todo App → Don't optimize

### Troubleshooting Notifications

If you're not receiving notifications when phone is idle:

1. Check that all three permissions above are granted
2. Verify notification settings: Settings → Notifications → App notifications → Todo App
3. Ensure "Do Not Disturb" mode isn't blocking notifications
4. Restart the app after granting battery optimization exemption
5. Create a test todo due in 2-3 minutes to verify notifications work

## Database Schema

### TodoEntity Table
- `id`: Primary key
- `text`: Todo text content
- `isCompleted`: Completion status
- `dueDateTime`: Due date/time (timestamp)
- `recurrencePattern`: JSON serialized recurrence config
- `createdAt`: Creation timestamp
- `lastModifiedAt`: Last modification timestamp
- `notificationEnabled`: Notification preference
- `parentTodoId`: Link to original todo for recurring instances

### TodoCompletionHistory Table
- `id`: Primary key
- `todoId`: Reference to TodoEntity
- `completedAt`: Completion timestamp
- `dueDateTime`: When it was originally due
- `text`: Snapshot of todo text at completion

## Version

- **Version Code**: 1
- **Version Name**: 1.0

## Future Enhancements

Potential features for future releases:
- Todo categories/tags
- Priority levels
- Search functionality
- Cloud sync
- Subtasks
- Attachments
- Shared todos
- Export/import data

## License

This project is a sample application for demonstration purposes.
