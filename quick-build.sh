#!/bin/bash

# Leim 快速构建脚本
# 用于快速构建和部署

set -e

# 颜色定义
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 Leim 快速构建开始...${NC}"

# 确保gradlew有执行权限
chmod +x ./gradlew

# 快速构建Debug版本
echo -e "${BLUE}📦 构建Debug APK...${NC}"
./gradlew assembleDebug --quiet

# 检查构建结果
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    APK_SIZE=$(du -h app/build/outputs/apk/debug/app-debug.apk | cut -f1)
    echo -e "${GREEN}✅ 构建成功！${NC}"
    echo -e "${GREEN}📱 APK位置: app/build/outputs/apk/debug/app-debug.apk${NC}"
    echo -e "${GREEN}📏 APK大小: $APK_SIZE${NC}"
else
    echo -e "\033[0;31m❌ 构建失败${NC}"
    exit 1
fi