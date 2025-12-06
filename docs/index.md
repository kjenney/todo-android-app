# Todo App Documentation

Welcome to the Todo App documentation! This is a feature-rich Android todo list application with recurring tasks, notifications, calendar views, and home screen widget support.

## Overview

Todo App helps you manage your daily tasks with powerful features like:

- ✅ Smart scheduling with recurring tasks
- 📅 Interactive calendar and weekly waterfall view
- 🔔 Reliable notification system
- 📱 Home screen widgets
- 💾 Local data storage with history tracking
- 🎨 Material Design UI

## Quick Links

- [Features](features.md) - Complete list of features
- [Building](building.md) - How to build the project
- [Testing](testing.md) - Running tests
- [Usage Guide](usage.md) - How to use the app
- [Architecture](architecture.md) - Technical details

## Technology Stack

- **Language**: Kotlin
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Async**: Kotlin Coroutines + Flow

## Getting Started

The fastest way to get started is using Docker:

```bash
# Clone the repository
git clone https://github.com/yourusername/todoapp.git
cd todoapp

# Build with Docker
chmod +x docker-build.sh
./docker-build.sh debug

# Install to device
./docker-build.sh install
```

For detailed instructions, see the [Building](building.md) guide.

## Project Status

- **Version**: 1.0
- **Status**: Active Development
- **License**: Sample Application

## Need Help?

- Check the [User Guide](usage.md) for usage instructions
- See [Permissions](permissions.md) for notification setup
- Review [Contributing](contributing.md) to contribute
