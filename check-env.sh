#!/bin/bash

# Leim 环境检查脚本
# 检查Linux环境是否满足Android开发要求

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🔍 Leim Android开发环境检查${NC}"
echo "=================================="

# 检查操作系统
echo -e "${BLUE}📋 系统信息:${NC}"
echo "操作系统: $(uname -s)"
echo "内核版本: $(uname -r)"
echo "架构: $(uname -m)"
echo ""

# 检查Java
echo -e "${BLUE}☕ Java环境检查:${NC}"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n1)
    echo -e "${GREEN}✅ Java已安装: $JAVA_VERSION${NC}"
    
    # 检查Java版本
    JAVA_MAJOR=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_MAJOR" -ge 17 ]; then
        echo -e "${GREEN}✅ Java版本满足要求 (需要JDK 17+)${NC}"
    else
        echo -e "${RED}❌ Java版本过低，需要JDK 17或更高版本${NC}"
    fi
else
    echo -e "${RED}❌ Java未安装${NC}"
    echo -e "${YELLOW}💡 安装建议: sudo apt update && sudo apt install openjdk-17-jdk${NC}"
fi
echo ""

# 检查JAVA_HOME
echo -e "${BLUE}🏠 JAVA_HOME检查:${NC}"
if [ -n "$JAVA_HOME" ]; then
    echo -e "${GREEN}✅ JAVA_HOME已设置: $JAVA_HOME${NC}"
else
    echo -e "${YELLOW}⚠️  JAVA_HOME未设置${NC}"
    echo -e "${YELLOW}💡 建议设置: export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64${NC}"
fi
echo ""

# 检查Git
echo -e "${BLUE}📚 Git检查:${NC}"
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version)
    echo -e "${GREEN}✅ Git已安装: $GIT_VERSION${NC}"
else
    echo -e "${RED}❌ Git未安装${NC}"
    echo -e "${YELLOW}💡 安装建议: sudo apt install git${NC}"
fi
echo ""

# 检查Gradle Wrapper
echo -e "${BLUE}🔧 Gradle Wrapper检查:${NC}"
if [ -f "./gradlew" ]; then
    echo -e "${GREEN}✅ gradlew文件存在${NC}"
    
    # 检查执行权限
    if [ -x "./gradlew" ]; then
        echo -e "${GREEN}✅ gradlew有执行权限${NC}"
    else
        echo -e "${YELLOW}⚠️  gradlew缺少执行权限${NC}"
        echo -e "${YELLOW}💡 修复建议: chmod +x ./gradlew${NC}"
    fi
    
    # 检查Gradle版本
    if ./gradlew --version &> /dev/null; then
        GRADLE_VERSION=$(./gradlew --version | grep "Gradle" | head -n1)
        echo -e "${GREEN}✅ $GRADLE_VERSION${NC}"
    else
        echo -e "${RED}❌ Gradle Wrapper执行失败${NC}"
    fi
else
    echo -e "${RED}❌ gradlew文件不存在${NC}"
fi
echo ""

# 检查内存
echo -e "${BLUE}💾 系统资源检查:${NC}"
TOTAL_MEM=$(free -h | awk '/^Mem:/ {print $2}')
AVAILABLE_MEM=$(free -h | awk '/^Mem:/ {print $7}')
echo "总内存: $TOTAL_MEM"
echo "可用内存: $AVAILABLE_MEM"

# 检查磁盘空间
DISK_USAGE=$(df -h . | awk 'NR==2 {print $4}')
echo "可用磁盘空间: $DISK_USAGE"
echo ""

# 检查网络连接
echo -e "${BLUE}🌐 网络连接检查:${NC}"
if ping -c 1 google.com &> /dev/null; then
    echo -e "${GREEN}✅ 网络连接正常${NC}"
else
    echo -e "${RED}❌ 网络连接异常${NC}"
    echo -e "${YELLOW}💡 Android构建需要下载依赖，请检查网络连接${NC}"
fi
echo ""

# 总结
echo "=================================="
echo -e "${BLUE}📊 环境检查完成${NC}"
echo ""
echo -e "${YELLOW}💡 使用构建脚本:${NC}"
echo "  ./build.sh debug     # 构建Debug版本"
echo "  ./build.sh all       # 完整构建流程"
echo "  ./quick-build.sh     # 快速构建"