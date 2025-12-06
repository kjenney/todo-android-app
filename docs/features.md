# Features

## Core Todo Management

### Basic Operations
- **Checkbox Completion**: Check off todos to mark them as complete/incomplete
- **Strikethrough Effect**: Completed todos are visually distinguished with strikethrough text
- **Edit Todos**: Tap any todo to edit all its properties (text, due date, recurrence, notifications)
- **Delete Functionality**: Remove todos with a simple tap on the delete button
- **Floating Action Button**: Quick access to add new todos

## Smart Scheduling

### Due Dates and Times
- **Due Dates and Times**: Set specific due dates and times for todos
- **Date/Time Pickers**: Intuitive date and time selection dialogs
- **Custom Times**: Schedule todos at any time (e.g., 1:32 PM every Thursday)

## Recurring Tasks

### Recurrence Types
- **Hourly**: Every N hours
- **Daily**: Every N days
- **Weekly**: With specific days of the week selection
- **Monthly**: Every N months
- **Yearly**: Every N years

### Recurrence Features
- **Custom Intervals**: Set any interval (e.g., every 2 hours, every 3 days)
- **Days of Week**: For weekly recurrence, select specific days (Mon, Tue, Wed, etc.)
- **Automatic Generation**: When a recurring todo is completed, the next occurrence is automatically created

## Notifications

- **Due Date Reminders**: Receive notifications when todos are due
- **Enable/Disable**: Toggle notifications per todo
- **WorkManager Integration**: Reliable notification scheduling even when app is closed
- **Complete from Notification**: Mark todos complete directly from the notification

## Local Storage & History

- **Room Database**: All todos stored locally with SQLite
- **Completion History**: Track when todos were completed
- **Historical Data**: View past completion records with date and time
- **Persistent Data**: Todos survive app restarts
- **Export/Import**: Back up and restore your todos

## View Modes

### Today View (Default)
Shows only todos due today for focused task management.

### All Todos View
Display all todos regardless of due date.

### Date-Specific View
Filter todos by selecting a specific date in the calendar.

## Calendar Views

### Monthly Calendar
- **Interactive Calendar**: Visual calendar interface with month navigation
- **Date Selection**: Tap any date to view todos for that day
- **Past, Current & Future**: View todos across all time periods
- **Visual Feedback**: See which dates have todos

### Weekly Waterfall View
- **7-Day Columns**: View the entire week at a glance
- **Todos Cascade Down**: Each day shows todos flowing down by time
- **Week Navigation**: Navigate between weeks
- **Color-Coded Status**:
  - Green: Completed
  - Blue: Pending
  - Red: Overdue
- **Todo Counts**: See completion stats for each day
- **Today Highlight**: Current day visually distinguished

## Home Screen Widgets

### Todo List Widget
- **Quick Access**: View today's todos directly from home screen
- **Check Off Items**: Complete todos without opening the app
- **Auto-Update**: Widget refreshes to show current todos
- **Resizable**: Adjust widget size to fit your home screen layout

### Calendar Widget
- **Monthly View**: See the current month at a glance
- **Visual Indicators**: Days with todos show a small dot indicator
- **Today Highlight**: Current day highlighted in purple
- **Quick Todo List**: Shows up to 5 of today's todos below the calendar
- **Tap to Open**: Tap header to open calendar view in app
- **Compact Design**: Fits nicely on home screen while showing useful info

## Filter Options

- **Hide Completed**: Toggle to hide/show completed todos
- **Applies to All Views**: Works in Today, All Todos, and Calendar views
- **Useful for Focus**: Hide completed tasks to focus on active work

## Future Enhancements

Potential features for future releases:

- Todo categories/tags
- Priority levels
- Search functionality
- Cloud sync
- Subtasks
- Attachments
- Shared todos
- Dark mode
- More widget customization
