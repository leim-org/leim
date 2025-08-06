#!/bin/bash

# Leim Android项目构建脚本
# 适用于Linux环境 (Ubuntu/Debian)

set -e  # 遇到错误时退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    printf "${BLUE}[INFO]${NC} %s\n" "$1"
}

log_success() {
    printf "${GREEN}[SUCCESS]${NC} %s\n" "$1"
}

log_warning() {
    printf "${YELLOW}[WARNING]${NC} %s\n" "$1"
}

log_error() {
    printf "${RED}[ERROR]${NC} %s\n" "$1"
}

# 检查Java环境 (简化版本)
check_java() {
    log_info "检查Java环境..."
    if command -v java &> /dev/null; then
        JAVA_VERSION_OUTPUT=$(java -version 2>&1 | head -n1)
        log_success "Java环境可用: $JAVA_VERSION_OUTPUT"
    else
        log_warning "未检测到Java，但将继续构建（Gradle会自动处理）"
    fi
}

# 检查Gradle环境
check_gradle() {
    log_info "检查Gradle环境..."
    if [ ! -f "./gradlew" ]; then
        log_error "gradlew文件不存在"
        exit 1
    fi
    
    # 确保gradlew有执行权限
    chmod +x ./gradlew
    log_success "Gradle Wrapper准备就绪"
}

# 清理项目
clean_project() {
    log_info "清理项目..."
    ./gradlew clean
    log_success "项目清理完成"
}

# 构建Debug版本
build_debug() {
    log_info "构建Debug版本..."
    ./gradlew assembleDebug
    
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        log_success "Debug APK构建成功: app/build/outputs/apk/debug/app-debug.apk"
    else
        log_error "Debug APK构建失败"
        exit 1
    fi
}

# 构建Release版本
build_release() {
    log_info "构建Release版本..."
    ./gradlew assembleRelease
    
    if [ -f "app/build/outputs/apk/release/app-release-unsigned.apk" ]; then
        log_success "Release APK构建成功: app/build/outputs/apk/release/app-release-unsigned.apk"
    else
        log_error "Release APK构建失败"
        exit 1
    fi
}

# 运行测试
run_tests() {
    log_info "运行单元测试..."
    ./gradlew test
    log_success "单元测试完成"
}

# 检查代码质量
check_lint() {
    log_info "运行代码检查..."
    ./gradlew lint
    log_success "代码检查完成"
}

# 显示帮助信息
show_help() {
    echo "Leim Android项目构建脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  clean       清理项目"
    echo "  debug       构建Debug版本"
    echo "  release     构建Release版本"
    echo "  test        运行单元测试"
    echo "  lint        运行代码检查"
    echo "  all         执行完整构建流程 (clean + debug + test + lint)"
    echo "  help        显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 debug           # 只构建Debug版本"
    echo "  $0 all             # 完整构建流程"
    echo "  $0 clean debug     # 清理后构建Debug版本"
}

# 完整构建流程
build_all() {
    log_info "开始完整构建流程..."
    check_java
    check_gradle
    clean_project
    build_debug
    run_tests
    check_lint
    log_success "完整构建流程完成！"
}

# 主函数
main() {
    if [ $# -eq 0 ]; then
        show_help
        exit 0
    fi
    
    # 基础环境检查
    check_java  # 仅显示信息，不强制退出
    check_gradle
    
    # 处理参数
    for arg in "$@"; do
        case $arg in
            clean)
                clean_project
                ;;
            debug)
                build_debug
                ;;
            release)
                build_release
                ;;
            test)
                run_tests
                ;;
            lint)
                check_lint
                ;;
            all)
                build_all
                return
                ;;
            help|--help|-h)
                show_help
                exit 0
                ;;
            *)
                log_error "未知参数: $arg"
                show_help
                exit 1
                ;;
        esac
    done
    
    log_success "构建脚本执行完成！"
}

# 执行主函数
main "$@"