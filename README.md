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

### Home Screen Widget
- **Quick Access**: View today's todos directly from home screen
- **Check Off Items**: Complete todos without opening the app
- **Auto-Update**: Widget refreshes to show current todos
- **Resizable**: Adjust widget size to fit your home screen layout

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

### Adding Widget
1. Long press on home screen
2. Select "Widgets"
3. Find "Todo App" widget
4. Drag to desired location
5. Widget shows today's todos and updates automatically

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
