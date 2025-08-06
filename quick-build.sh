#!/bin/bash
set -e

echo "Leim Quick Build Starting..."
chmod +x ./gradlew
echo "Building Debug APK..."
./gradlew assembleDebug

if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "Build Success!"
    echo "APK Location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "Build Failed"
    exit 1
fi