# Home Screen Widgets

The app provides two home screen widgets for quick access to your todos.

## Todo List Widget

View and interact with today's todos directly from your home screen.

### Features

- **Quick Access**: View today's todos without opening the app
- **Check Off Items**: Mark todos complete with a single tap
- **Auto-Update**: Widget refreshes automatically when todos change
- **Resizable**: Adjust widget size to fit your home screen layout
- **Empty State**: Shows helpful message when no todos exist

### Adding the Widget

1. Long press on your home screen
2. Select "Widgets" from the menu
3. Find "Todo App" → "Today's Todos" widget
4. Drag the widget to your desired location
5. Resize as needed by dragging the corners

### How It Works

The widget:
- Displays todos due today
- Updates when you complete, add, or edit todos
- Uses a ListView for efficient scrolling
- Shows checkboxes for quick completion
- Taps on todo text open the app for editing

### Implementation Details

**Provider:** `TodoWidget.kt`
**Service:** `TodoWidgetService.kt`
**Layout:** `widget_todo_list.xml`, `widget_todo_item.xml`

## Calendar Widget

Monthly calendar view with todo indicators and today's todo list.

### Features

- **Monthly View**: See the entire current month at a glance
- **Visual Indicators**: Days with todos show a small dot indicator
- **Today Highlight**: Current day highlighted in purple/blue
- **Quick Todo List**: Shows up to 5 of today's todos below the calendar
- **Tap to Open**: Tap the header to open the calendar view in the app
- **Compact Design**: Fits nicely on home screen while showing useful info
- **Day Selection**: Tap any day to see todos for that date (opens app)

### Adding the Widget

1. Long press on your home screen
2. Select "Widgets" from the menu
3. Find "Todo App" → "Calendar" widget
4. Drag the widget to your desired location
5. The widget is fixed-size and optimized for readability

### How It Works

The widget:
- Shows current month's calendar grid
- Indicates which days have todos with dots
- Lists today's todos below the calendar
- Updates automatically when todos change
- Opens the calendar view when tapped

### Implementation Details

**Provider:** `CalendarWidget.kt`
**Grid Service:** `CalendarWidgetGridService.kt`
**Todo Service:** `CalendarWidgetTodoService.kt`
**Layout:** `widget_calendar.xml`, `widget_calendar_day.xml`, `widget_calendar_todo_item.xml`

## Widget Updates

### Automatic Updates

Widgets update automatically when:
- A todo is added, edited, or deleted
- A todo is marked complete/incomplete
- The date changes (for "today's" todos)

### Manual Updates

Widgets can be manually refreshed by:
- Removing and re-adding the widget
- Restarting the device
- Opening the app (triggers update)

### Update Frequency

- **On Data Change**: Immediate update via AppWidgetManager
- **Periodic**: System handles periodic updates
- **On Boot**: Widgets restore on device restart

## Best Practices

### Todo List Widget

- **Size**: Works best in 2x2 or larger
- **Placement**: Put on main home screen for quick access
- **Usage**: Great for daily task management

### Calendar Widget

- **Size**: Fixed size optimized for 4x3 grid
- **Placement**: Secondary screen or tablet
- **Usage**: Great for weekly/monthly planning

## Limitations

### Todo List Widget

- Shows today's todos only
- Limited to screen size (scrollable)
- No inline editing (opens app)
- Checkbox action only for completion

### Calendar Widget

- Shows current month only
- Maximum 5 todos displayed
- No month navigation on widget
- Fixed size layout

## Troubleshooting

### Widget Not Updating

1. Open the app to trigger an update
2. Remove and re-add the widget
3. Check app permissions
4. Restart device

### Widget Shows No Data

1. Ensure you have todos created
2. Check that todos have due dates
3. For calendar widget, ensure todos are in current month
4. Open app to verify data exists

### Widget Looks Broken

1. Remove widget
2. Clear app cache (Settings → Apps → Todo App → Storage → Clear Cache)
3. Re-add widget
4. If problem persists, reinstall app

## Development Notes

### Widget Communication

Widgets communicate with the app via:
- **Intent Filters**: For update broadcasts
- **PendingIntents**: For user interactions
- **RemoteViews**: For UI updates

### Data Access

Widgets access data via:
- **TodoDatabase**: Direct Room database access
- **Coroutines**: Asynchronous data loading
- **RemoteViewsService**: For list data

### Testing Widgets

To test widgets:
1. Use "Seed Sample Todos" from app menu
2. Add widget to home screen
3. Verify data appears correctly
4. Test interactions (checkboxes, taps)
5. Test updates by modifying todos in app
