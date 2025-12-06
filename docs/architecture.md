# Architecture

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
│   └── CalendarActivity.kt              # Calendar waterfall view
├── notifications/
│   ├── TodoNotificationWorker.kt        # Notification worker
│   ├── TodoNotificationScheduler.kt     # Notification scheduling
│   ├── TodoActionReceiver.kt            # Handle notification actions
│   └── TodoAlarmReceiver.kt             # Alarm receiver
├── utils/
│   ├── TodoSeeder.kt                    # Test data seeding
│   ├── TodoExportImportManager.kt       # Export/import functionality
│   └── PermissionHelper.kt              # Permission handling
└── widget/
    ├── TodoWidget.kt                    # Todo list widget provider
    ├── TodoWidgetService.kt             # Todo widget data service
    ├── CalendarWidget.kt                # Calendar widget provider
    ├── CalendarWidgetGridService.kt     # Calendar grid service
    └── CalendarWidgetTodoService.kt     # Calendar todo service
```

## MVVM Architecture

### Model Layer (`data/`)
- **TodoEntity**: Data class representing a todo item
- **TodoDao**: Database queries
- **TodoRepository**: Abstracts data sources
- **RecurrencePattern**: Recurrence configuration

### View Layer (`ui/`, `MainActivity.kt`)
- **Activities**: MainActivity, CalendarActivity, AddTodoActivity
- **Adapters**: TodoEntityAdapter for RecyclerView
- **Layouts**: XML layouts for UI

### ViewModel Layer (`ui/TodoViewModel.kt`)
- **TodoViewModel**: Business logic and state management
- **LiveData/Flow**: Reactive data streams
- **Coroutines**: Asynchronous operations

## Data Flow

1. **User Action** → View (Activity/Fragment)
2. **View** → ViewModel (via method calls)
3. **ViewModel** → Repository (data operations)
4. **Repository** → DAO (database queries)
5. **DAO** → Database (Room)
6. **Database** → DAO → Repository (via Flow)
7. **Repository** → ViewModel (LiveData updates)
8. **ViewModel** → View (UI updates via observers)

## Key Design Patterns

### Repository Pattern
Centralizes data access logic and provides clean API to ViewModel.

### Observer Pattern
LiveData/Flow observers update UI automatically when data changes.

### Dependency Injection
Manual DI via ViewModels, Database singleton pattern.

### Adapter Pattern
RecyclerView adapters for efficient list rendering.

## Background Processing

### WorkManager
- Schedules notifications reliably
- Persists across device reboots
- Handles battery optimization

### Coroutines
- Asynchronous database operations
- Non-blocking UI updates
- Structured concurrency

## Version

- **Version Code**: 1
- **Version Name**: 1.0
