#!/bin/bash

# Leim Android Project Build Script
#
# This script provides commands to build, clean, and test the Leim project.
#
# Usage:
#   ./build.sh <command>
#
# Commands:
#   debug       Build the debug version of the application.
#   release     Build the release version of the application.
#   clean       Clean the project.
#   test        Run unit tests.
#   check       Run lint checks.
#

set -e

# Function to print colored messages
print_message() {
    color="$1"
    message="$2"
    case "$color" in
        "green") echo -e "\033[0;32m${message}\033[0m" ;;
        "red") echo -e "\033[0;31m${message}\033[0m" ;;
        "yellow") echo -e "\033[0;33m${message}\033[0m" ;;
        *) echo "${message}" ;;
    esac
}

# Function to check Java version
check_java() {
    print_message "yellow" "Checking Java version..."
    if ! command -v java &> /dev/null; then
        print_message "red" "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    JAVA_VERSION_OUTPUT=$(java -version 2>&1)
    JAVA_VERSION=$(echo "$JAVA_VERSION_OUTPUT" | awk -F '"' '/version/ {print $2}')
    MAJOR_VERSION=$(echo "$JAVA_VERSION" | cut -d. -f1)
    if [ "$MAJOR_VERSION" -lt 17 ]; then
        print_message "red" "Java version is less than 17. Please upgrade to Java 17 or higher."
        print_message "yellow" "Current Java version: $JAVA_VERSION"
        exit 1
    fi
    print_message "green" "Java version check passed: $JAVA_VERSION"
}

# Function to build the debug version
build_debug() {
    print_message "yellow" "Building debug version..."
    check_java
    ./gradlew assembleDebug
    print_message "green" "Debug build completed successfully."
}

# Function to build the release version
build_release() {
    print_message "yellow" "Building release version..."
    check_java
    ./gradlew assembleRelease
    print_message "green" "Release build completed successfully."
}

# Function to clean the project
clean_project() {
    print_message "yellow" "Cleaning project..."
    ./gradlew clean
    print_message "green" "Project cleaned successfully."
}

# Function to run unit tests
run_tests() {
    print_message "yellow" "Running unit tests..."
    check_java
    ./gradlew test
    print_message "green" "Unit tests completed successfully."
}

# Function to run lint checks
run_lint() {
    print_message "yellow" "Running lint checks..."
    check_java
    ./gradlew lint
    print_message "green" "Lint checks completed successfully."
}

# Main script logic
COMMAND="$1"

if [ -z "$COMMAND" ]; then
    print_message "red" "No command specified. Usage: ./build.sh {debug|release|clean|test|check}"
    exit 1
fi

case "$COMMAND" in
    "debug")
        build_debug
        ;;
    "release")
        build_release
        ;;
    "clean")
        clean_project
        ;;
    "test")
        run_tests
        ;;
    "check")
        run_lint
        ;;
    *)
        print_message "red" "Unknown command: $COMMAND"
        exit 1
        ;;
esac