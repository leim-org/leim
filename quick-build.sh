#!/bin/bash

# Leim 快速构建脚本
set -e

echo "🚀 Leim 快速构建开始..."

# 确保gradlew有执行权限
chmod +x ./gradlew

# 快速构建Debug版本
echo "📦 构建Debug APK..."
./gradlew assembleDebug --quiet

# 检查构建结果
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    APK_SIZE=$(du -h app/build/outputs/apk/debug/app-debug.apk | cut -f1)
    echo "✅ 构建成功！"
    echo "📱 APK位置: app/build/outputs/apk/debug/app-debug.apk"
    echo "📏 APK大小: $APK_SIZE"
else
    echo "❌ 构建失败"
    exit 1
fi