# Leim Android 构建指南

本文档介绍如何在Linux环境下构建Leim Android应用。

## 🚀 快速开始

### 1. 环境检查
```bash
# 检查开发环境是否满足要求
./check-env.sh
```

### 2. 安装依赖 (首次使用)
```bash
# 安装Java、Git等必要依赖
./install-deps.sh
```

### 3. 快速构建
```bash
# 快速构建Debug版本
./quick-build.sh
```

## 📋 详细构建选项

### 主构建脚本
```bash
# 显示帮助信息
./build.sh help

# 清理项目
./build.sh clean

# 构建Debug版本
./build.sh debug

# 构建Release版本
./build.sh release

# 运行单元测试
./build.sh test

# 运行代码检查
./build.sh lint

# 完整构建流程 (推荐)
./build.sh all

# 组合使用
./build.sh clean debug test
```

## 🔧 环境要求

### 系统要求
- **操作系统**: Ubuntu 18.04+ / Debian 10+
- **内存**: 最少4GB，推荐8GB+
- **磁盘空间**: 最少10GB可用空间
- **网络**: 稳定的互联网连接

### 软件要求
- **Java**: OpenJDK 17或更高版本
- **Git**: 用于版本控制
- **Gradle**: 通过Gradle Wrapper自动管理

## 📁 构建输出

### Debug版本
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release版本
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

### 测试报告
```
app/build/reports/tests/testDebugUnitTest/index.html
```

### 代码检查报告
```
app/build/reports/lint-results-debug.html
```

## 🛠️ 故障排除

### 常见问题

#### 1. Java版本问题
```bash
# 检查Java版本
java -version

# 如果版本低于17，安装新版本
sudo apt install openjdk-17-jdk

# 设置JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

#### 2. 权限问题
```bash
# 给构建脚本添加执行权限
chmod +x *.sh
chmod +x ./gradlew
```

#### 3. 网络问题
```bash
# 如果下载依赖失败，可以配置代理
export GRADLE_OPTS="-Dhttp.proxyHost=proxy.example.com -Dhttp.proxyPort=8080"
```

#### 4. 内存不足
```bash
# 编辑 ~/.gradle/gradle.properties
echo "org.gradle.jvmargs=-Xmx2g" >> ~/.gradle/gradle.properties
```

### 清理缓存
```bash
# 清理Gradle缓存
./gradlew clean
rm -rf ~/.gradle/caches/

# 清理项目缓存
./gradlew cleanBuildCache
```

## ⚡ 性能优化

### Gradle优化
项目已包含优化的Gradle配置，位于 `~/.gradle/gradle.properties`:

```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.configureondemand=true
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
org.gradle.caching=true
```

### 构建加速技巧
1. **使用Gradle守护进程**: 自动启用
2. **并行构建**: 自动启用
3. **增量构建**: 避免不必要的重新编译
4. **构建缓存**: 复用之前的构建结果

## 🔄 CI/CD集成

### GitHub Actions
项目已配置GitHub Actions自动构建，位于 `.github/workflows/android.yml`

### 本地预检查
在推送代码前，建议运行完整检查:
```bash
./build.sh all
```

## 📞 技术支持

如果遇到构建问题:

1. 首先运行环境检查: `./check-env.sh`
2. 查看构建日志中的错误信息
3. 检查网络连接和代理设置
4. 确保有足够的磁盘空间和内存

## 📝 版本信息

- **Android Gradle Plugin**: 8.7.2
- **Gradle**: 8.7
- **Kotlin**: 1.9.10
- **Target SDK**: 34
- **Min SDK**: 24