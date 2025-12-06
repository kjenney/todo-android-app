# Database Schema

The app uses Room for local data persistence with SQLite.

## TodoEntity Table

Primary table storing all todo items.

| Column | Type | Description |
|--------|------|-------------|
| `id` | Long | Primary key (auto-generated) |
| `text` | String | Todo text content |
| `isCompleted` | Boolean | Completion status |
| `dueDateTime` | Long? | Due date/time timestamp in milliseconds (nullable) |
| `recurrencePattern` | RecurrencePattern | JSON serialized recurrence configuration |
| `createdAt` | Long | Creation timestamp |
| `lastModifiedAt` | Long | Last modification timestamp |
| `notificationEnabled` | Boolean | Whether notifications are enabled for this todo |
| `parentTodoId` | Long? | Link to original todo for recurring instances (nullable) |

### RecurrencePattern Structure

Stored as JSON via Room type converters:

```kotlin
data class RecurrencePattern(
    val type: RecurrenceType,           // NONE, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
    val interval: Int = 1,              // Every N units
    val daysOfWeek: Set<Int> = emptySet(),  // For weekly: 1=Mon, 7=Sun
    val dayOfMonth: Int? = null,        // For monthly: specific day
    val endDate: Long? = null           // When recurrence ends
)
```

## TodoCompletionHistory Table

Tracks completion history for reporting and analytics.

| Column | Type | Description |
|--------|------|-------------|
| `id` | Long | Primary key (auto-generated) |
| `todoId` | Long | Reference to TodoEntity id |
| `completedAt` | Long | Completion timestamp |
| `dueDateTime` | Long? | When it was originally due (nullable) |
| `text` | String | Snapshot of todo text at completion time |

## Queries

### Common DAO Queries

**Get all todos:**
```kotlin
@Query("SELECT * FROM todos ORDER BY dueDateTime ASC, createdAt DESC")
fun getAllTodos(): Flow<List<TodoEntity>>
```

**Get today's todos:**
```kotlin
@Query("SELECT * FROM todos WHERE date(dueDateTime/1000, 'unixepoch', 'localtime') = date('now', 'localtime') ORDER BY dueDateTime ASC")
fun getTodayTodos(): Flow<List<TodoEntity>>
```

**Get todos by specific date:**
```kotlin
@Query("SELECT * FROM todos WHERE date(dueDateTime/1000, 'unixepoch', 'localtime') = date(:date/1000, 'unixepoch', 'localtime') ORDER BY dueDateTime ASC")
fun getTodosByDate(date: Long): Flow<List<TodoEntity>>
```

**Get due todos for notifications:**
```kotlin
@Query("SELECT * FROM todos WHERE dueDateTime <= :currentTime AND notificationEnabled = 1")
suspend fun getDueTodos(currentTime: Long): List<TodoEntity>
```

## Type Converters

Room uses custom type converters for complex types:

### RecurrencePattern Converter
```kotlin
class Converters {
    @TypeConverter
    fun fromRecurrencePattern(value: RecurrencePattern): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toRecurrencePattern(value: String): RecurrencePattern {
        return Gson().fromJson(value, RecurrencePattern::class.java)
    }
}
```

## Database Migrations

Currently using version 1 with no migrations.

Future migrations should:
- Preserve user data
- Handle schema changes gracefully
- Include rollback strategies

## Data Relationships

### Recurring Todos

Recurring todos use a parent-child relationship:

- **Parent Todo**: Original todo with recurrence pattern
- **Child Todos**: Generated occurrences with `parentTodoId` pointing to parent

When a recurring todo is completed:
1. Mark current instance as complete
2. Create next occurrence based on recurrence pattern
3. Link to parent via `parentTodoId`

### Completion History

Each completion creates a history record:
- Tracks when todo was completed
- Preserves snapshot of todo state
- Used for completion statistics

## Performance Considerations

### Indexes
- Primary key index on `id` (automatic)
- Consider adding index on `dueDateTime` for large datasets
- Consider adding index on `parentTodoId` for recurring todos

### Query Optimization
- Use Flow for reactive queries (efficient)
- Limit results when displaying lists
- Use paging for very large datasets (future enhancement)

## Backup and Export

See [TodoExportImportManager](../app/src/main/java/com/example/todoapp/utils/TodoExportImportManager.kt) for:

- Export todos to JSON
- Import todos from JSON
- Backup/restore functionality
