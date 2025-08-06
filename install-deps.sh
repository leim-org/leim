#!/bin/bash

# Leim Android开发环境安装脚本
# 适用于Ubuntu/Debian系统

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 Leim Android开发环境安装${NC}"
echo "=================================="

# 检查是否为root用户
if [ "$EUID" -eq 0 ]; then
    echo -e "${RED}❌ 请不要使用root用户运行此脚本${NC}"
    exit 1
fi

# 更新包管理器
echo -e "${BLUE}📦 更新包管理器...${NC}"
sudo apt update

# 安装基础工具
echo -e "${BLUE}🔧 安装基础工具...${NC}"
sudo apt install -y curl wget unzip git

# 检查并安装Java 17
echo -e "${BLUE}☕ 检查Java环境...${NC}"
if ! command -v java &> /dev/null || [ "$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)" -lt 17 ]; then
    echo -e "${YELLOW}📥 安装OpenJDK 17...${NC}"
    sudo apt install -y openjdk-17-jdk
    
    # 设置JAVA_HOME
    JAVA_HOME_PATH="/usr/lib/jvm/java-17-openjdk-amd64"
    if [ -d "$JAVA_HOME_PATH" ]; then
        echo "export JAVA_HOME=$JAVA_HOME_PATH" >> ~/.bashrc
        echo "export PATH=\$PATH:\$JAVA_HOME/bin" >> ~/.bashrc
        echo -e "${GREEN}✅ JAVA_HOME已设置到 ~/.bashrc${NC}"
    fi
else
    echo -e "${GREEN}✅ Java 17已安装${NC}"
fi

# 安装Android SDK命令行工具 (可选)
echo -e "${BLUE}📱 是否安装Android SDK命令行工具? (y/N)${NC}"
read -r response
if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
    echo -e "${YELLOW}📥 下载Android SDK命令行工具...${NC}"
    
    # 创建Android SDK目录
    ANDROID_HOME="$HOME/Android/Sdk"
    mkdir -p "$ANDROID_HOME"
    
    # 下载命令行工具
    cd /tmp
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
    unzip -q commandlinetools-linux-9477386_latest.zip
    
    # 移动到正确位置
    mkdir -p "$ANDROID_HOME/cmdline-tools"
    mv cmdline-tools "$ANDROID_HOME/cmdline-tools/latest"
    
    # 设置环境变量
    echo "export ANDROID_HOME=$ANDROID_HOME" >> ~/.bashrc
    echo "export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin" >> ~/.bashrc
    echo "export PATH=\$PATH:\$ANDROID_HOME/platform-tools" >> ~/.bashrc
    
    echo -e "${GREEN}✅ Android SDK命令行工具安装完成${NC}"
    echo -e "${YELLOW}💡 请运行 'source ~/.bashrc' 或重新登录以应用环境变量${NC}"
fi

# 优化Gradle性能
echo -e "${BLUE}⚡ 优化Gradle性能...${NC}"
GRADLE_PROPERTIES="$HOME/.gradle/gradle.properties"
mkdir -p "$HOME/.gradle"

if [ ! -f "$GRADLE_PROPERTIES" ]; then
    cat > "$GRADLE_PROPERTIES" << EOF
# Gradle性能优化配置
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.configureondemand=true
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
org.gradle.caching=true
android.useAndroidX=true
android.enableJetifier=true
EOF
    echo -e "${GREEN}✅ Gradle性能配置已创建${NC}"
else
    echo -e "${YELLOW}⚠️  Gradle配置文件已存在，跳过创建${NC}"
fi

# 设置Git配置 (如果尚未设置)
echo -e "${BLUE}📚 检查Git配置...${NC}"
if [ -z "$(git config --global user.name)" ]; then
    echo -e "${YELLOW}📝 请输入Git用户名:${NC}"
    read -r git_name
    git config --global user.name "$git_name"
fi

if [ -z "$(git config --global user.email)" ]; then
    echo -e "${YELLOW}📧 请输入Git邮箱:${NC}"
    read -r git_email
    git config --global user.email "$git_email"
fi

echo -e "${GREEN}✅ Git配置完成${NC}"

# 完成安装
echo ""
echo "=================================="
echo -e "${GREEN}🎉 安装完成！${NC}"
echo ""
echo -e "${YELLOW}📋 下一步操作:${NC}"
echo "1. 运行 'source ~/.bashrc' 或重新登录"
echo "2. 运行 './check-env.sh' 检查环境"
echo "3. 运行 './build.sh debug' 开始构建"
echo ""
echo -e "${BLUE}💡 构建脚本使用方法:${NC}"
echo "  ./build.sh debug     # 构建Debug版本"
echo "  ./build.sh all       # 完整构建流程"
echo "  ./quick-build.sh     # 快速构建"