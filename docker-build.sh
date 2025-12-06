#!/bin/bash

# Docker Build Helper Script for Todo App
# This script simplifies building the Android app using Docker

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Docker image
ANDROID_SDK_IMAGE="thyrlian/android-sdk:latest"

# Gradle cache volume
CACHE_VOLUME="gradle-cache"

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to create Gradle cache volume if it doesn't exist
ensure_cache_volume() {
    if ! docker volume inspect $CACHE_VOLUME > /dev/null 2>&1; then
        print_info "Creating Gradle cache volume..."
        docker volume create $CACHE_VOLUME
    fi
}

# Function to run Gradle command in Docker
run_gradle() {
    ensure_cache_volume
    print_info "Running: ./gradlew $@"
    docker run --rm \
        -v "$(pwd)":/app \
        -v $CACHE_VOLUME:/root/.gradle \
        -w /app \
        $ANDROID_SDK_IMAGE \
        ./gradlew "$@"
}

# Show usage
show_usage() {
    cat << EOF
Usage: ./docker-build.sh [command]

Commands:
    debug           Build debug APK
    release         Build release APK
    clean           Clean build directory
    test            Run unit tests
    install         Install debug APK to connected device (requires local ADB)
    tasks           List all available Gradle tasks
    help            Show this help message

Examples:
    ./docker-build.sh debug
    ./docker-build.sh clean
    ./docker-build.sh test

Output locations:
    Debug APK:   app/build/outputs/apk/debug/app-debug.apk
    Release APK: app/build/outputs/apk/release/app-release.apk
EOF
}

# Main script logic
check_docker

case "${1:-help}" in
    debug)
        print_info "Building debug APK..."
        run_gradle assembleDebug
        print_info "Debug APK built successfully!"
        print_info "Location: app/build/outputs/apk/debug/app-debug.apk"
        ;;

    release)
        print_info "Building release APK..."
        run_gradle assembleRelease
        print_info "Release APK built successfully!"
        print_info "Location: app/build/outputs/apk/release/app-release.apk"
        ;;

    clean)
        print_info "Cleaning build directory..."
        run_gradle clean
        print_info "Clean completed!"
        ;;

    test)
        print_info "Running unit tests..."
        run_gradle test
        print_info "Tests completed!"
        print_info "Results: app/build/reports/tests/testDebugUnitTest/index.html"
        ;;

    install)
        if ! command -v adb &> /dev/null; then
            print_error "ADB not found. Please install Android Platform Tools."
            exit 1
        fi

        APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

        if [ ! -f "$APK_PATH" ]; then
            print_warn "Debug APK not found. Building first..."
            run_gradle assembleDebug
        fi

        print_info "Installing debug APK to connected device..."
        adb install -r "$APK_PATH"
        print_info "Installation complete!"
        ;;

    tasks)
        print_info "Available Gradle tasks:"
        run_gradle tasks
        ;;

    help|--help|-h)
        show_usage
        ;;

    *)
        print_error "Unknown command: $1"
        echo ""
        show_usage
        exit 1
        ;;
esac
