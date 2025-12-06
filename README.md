# Todo App

A feature-rich Android todo list application with recurring tasks, notifications, calendar views, and home screen widgets.

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)
![License](https://img.shields.io/badge/License-Sample-lightgrey.svg)

## ✨ Features

- ✅ **Smart Scheduling** - Due dates, times, and recurring tasks
- 📅 **Multiple Views** - Today, All Todos, Monthly Calendar, Weekly Waterfall
- 🔔 **Reliable Notifications** - Never miss a deadline
- 📱 **Home Screen Widgets** - Quick access to todos and calendar
- 💾 **Local Storage** - All data stored securely on device
- 🎨 **Material Design** - Clean, modern interface

[**View all features →**](docs/features.md)

## 🚀 Quick Start

### Using Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/yourusername/todoapp.git
cd todoapp

# Build debug APK
chmod +x docker-build.sh
./docker-build.sh debug

# Install to device (requires ADB)
./docker-build.sh install
```

**No Java or Android SDK installation required!**

[**Full build instructions →**](docs/building.md)

### Using Android Studio

1. Clone the repository
2. Open in Android Studio
3. Let Gradle sync
4. Run on emulator or device

## 📖 Documentation

**📘 [View Full Documentation](https://yourusername.github.io/todoapp/)** (GitHub Pages)

Or browse directly:

**User Guide:**

- [Features](docs/features.md) - Complete feature list
- [Usage Guide](docs/usage.md) - How to use the app
- [Permissions](docs/permissions.md) - Setting up notifications

**Development:**

- [Building](docs/building.md) - Build instructions (Docker & local)
- [Testing](docs/testing.md) - Running tests
- [Architecture](docs/architecture.md) - Technical architecture
- [Database](docs/database.md) - Database schema
- [Widgets](docs/widgets.md) - Home screen widgets
- [Contributing](docs/contributing.md) - How to contribute

## 🔧 Tech Stack

- **Kotlin** - Modern Android development
- **MVVM Architecture** - Clean separation of concerns
- **Room Database** - Local data persistence
- **Coroutines + Flow** - Asynchronous operations
- **Material Design** - Modern UI components
- **WorkManager** - Reliable background tasks

## 📸 Screenshots

[Screenshots will be added]

## 🧪 Testing

```bash
# Run unit tests
./docker-build.sh test

# Run instrumented tests (requires emulator)
./gradlew connectedAndroidTest
```

Tests automatically seed sample data for reliable, consistent results.

[**Testing guide →**](docs/testing.md)

## 🤝 Contributing

We welcome contributions! See [CONTRIBUTING.md](docs/contributing.md) for guidelines.

```bash
# Fork the repo
# Create a feature branch
git checkout -b feature/amazing-feature

# Make your changes and commit
git commit -m "Add amazing feature"

# Push and create a PR
git push origin feature/amazing-feature
```

## 📝 License

This project is a sample application for demonstration purposes.

## 🙏 Acknowledgments

- [Kizitonwose Calendar](https://github.com/kizitonwose/CalendarView) - Calendar view library
- [Material Design](https://material.io/) - Design system
- [Android Jetpack](https://developer.android.com/jetpack) - Modern Android development

---

**Documentation** • [Features](docs/features.md) • [Building](docs/building.md) • [Testing](docs/testing.md) • [Contributing](docs/contributing.md)
