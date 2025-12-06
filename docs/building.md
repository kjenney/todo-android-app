# Building the Project

## Option 1: Using Docker (Recommended)

Docker provides a consistent build environment without needing to install Java or Android SDK locally.

### Prerequisites

- Docker installed on your system
- For installing on device: ADB (Android Debug Bridge) installed locally

### Quick Start with Helper Script

The easiest way to build with Docker:

```bash
# Make the script executable (first time only)
chmod +x docker-build.sh

# Build debug APK
./docker-build.sh debug

# Build release APK
./docker-build.sh release

# Clean build
./docker-build.sh clean

# Run unit tests
./docker-build.sh test

# Install to device
./docker-build.sh install

# See all options
./docker-build.sh help
```

The script automatically:

- Creates and uses a Gradle cache volume for faster builds
- Shows colored output for easier reading
- Tells you where to find built APKs
- Handles common errors

### Manual Docker Commands

If you prefer to run Docker commands directly:

**Build debug APK:**

```bash
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest \
  ./gradlew assembleDebug
```

**Build release APK:**

```bash
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest \
  ./gradlew assembleRelease
```

**Clean and build:**

```bash
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest \
  ./gradlew clean assembleDebug
```

**Speed up builds with Gradle cache:**

```bash
# Create a named volume for Gradle cache
docker volume create gradle-cache

# Use it in your builds
docker run --rm \
  -v "$(pwd)":/app \
  -v gradle-cache:/root/.gradle \
  -w /app \
  thyrlian/android-sdk:latest \
  ./gradlew assembleDebug
```

**Find your built APK:**

After building, APKs are located at:

- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

**Install on connected device:**

```bash
# Install debug APK (requires ADB installed locally)
adb install app/build/outputs/apk/debug/app-debug.apk

# Or install release APK
adb install app/build/outputs/apk/release/app-release.apk
```

### Using Docker Compose

Create a `docker-compose.yml` file:

```yaml
version: '3.8'
services:
  android-build:
    image: thyrlian/android-sdk:latest
    volumes:
      - .:/app
      - gradle-cache:/root/.gradle
    working_dir: /app
    command: ./gradlew assembleDebug

volumes:
  gradle-cache:
```

Then run:

```bash
docker-compose run android-build
```

### Quick Reference: Common Docker Build Commands

**Build and install in one step:**

```bash
# Build debug APK
docker run --rm -v "$(pwd)":/app -v gradle-cache:/root/.gradle -w /app \
  thyrlian/android-sdk:latest ./gradlew assembleDebug

# Install to device (requires ADB locally)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Check build version:**

```bash
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest \
  ./gradlew -q printVersionName
```

**List all Gradle tasks:**

```bash
docker run --rm -v "$(pwd)":/app -w /app thyrlian/android-sdk:latest \
  ./gradlew tasks
```

**Build with logs:**

```bash
docker run --rm -v "$(pwd)":/app -v gradle-cache:/root/.gradle -w /app \
  thyrlian/android-sdk:latest ./gradlew assembleDebug --info
```

### Troubleshooting Docker Builds

**Build is slow**
- Use the Gradle cache volume as shown above
- First builds download dependencies (slow)
- Subsequent builds are much faster

**Permission denied**
- On Linux, you may need to add `--user $(id -u):$(id -g)`

**Out of disk space**
- Clean up with `docker system prune -a`

**Gradle daemon issues**
- Add `--no-daemon` flag to gradle commands

## Option 2: Local Installation

If you prefer to install dependencies locally:

### Prerequisites

- Android Studio (Arctic Fox or later recommended)
- JDK 17 or higher
- Android SDK with API 34

### Build Steps

1. Clone the repository

```bash
git clone https://github.com/yourusername/todoapp.git
cd todoapp
```

2. Open the project in Android Studio
   - Let Gradle sync the project dependencies
   - Wait for indexing to complete

3. Build and run
   - Run the app on an emulator or physical device (API 24+)
   - Or use command line:

### Build Commands (Local)

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Clean build
./gradlew clean build
```

## Automated Builds with GitHub Actions

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

- Calendar View 2.4.1 (Kizitonwose)
- Gson 2.10.1 (for Room converters)
