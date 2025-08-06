# 贡献指南

感谢您对 Leim 项目的关注！我们欢迎任何形式的贡献。

## 开发环境

### 必要条件
- Android Studio Arctic Fox 或更高版本
- JDK 17 或更高版本
- Android SDK API 34
- Git

### 项目设置
1. Fork 本仓库
2. 克隆您的 fork：
   ```bash
   git clone https://github.com/your-username/leim.git
   cd leim
   ```
3. 在 Android Studio 中打开项目
4. 等待 Gradle 同步完成

## 开发规范

### 代码风格
- 严格遵循 [Kotlin 官方编码规范](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 4 个空格缩进
- 类名使用 PascalCase
- 函数和变量名使用 camelCase
- 常量使用 UPPER_SNAKE_CASE

### 架构规范
- 使用 MVVM 架构模式
- Repository 模式管理数据源
- 使用 LiveData 进行数据绑定
- 协程处理异步操作

### 提交规范
使用 [Conventional Commits](https://www.conventionalcommits.org/) 格式：

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

类型包括：
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

示例：
```
feat(auth): 添加用户登录功能
fix(websocket): 修复连接断开重连问题
docs(readme): 更新安装说明
```

## 分支策略

- `main`: 主分支，包含稳定的生产代码
- `develop`: 开发分支，包含最新的开发代码
- `feature/*`: 功能分支，从 develop 分支创建
- `hotfix/*`: 热修复分支，从 main 分支创建

## 提交流程

1. 创建功能分支：
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. 进行开发并提交：
   ```bash
   git add .
   git commit -m "feat: 添加新功能描述"
   ```

3. 推送到您的 fork：
   ```bash
   git push origin feature/your-feature-name
   ```

4. 创建 Pull Request

## Pull Request 要求

- 提供清晰的 PR 标题和描述
- 确保所有测试通过
- 代码通过 CI/CD 检查
- 至少一个维护者审核通过

## 问题报告

使用 GitHub Issues 报告问题时，请包含：

- 问题的详细描述
- 复现步骤
- 预期行为
- 实际行为
- 设备信息（Android 版本、设备型号等）
- 相关日志或截图

## 功能请求

提交功能请求时，请说明：

- 功能的详细描述
- 使用场景
- 预期的用户体验
- 可能的实现方案

## 代码审查

我们重视代码质量，所有 PR 都需要经过代码审查：

- 代码逻辑正确性
- 性能考虑
- 安全性检查
- 用户体验
- 代码可维护性

## 测试

- 为新功能编写单元测试
- 确保现有测试通过
- 手动测试关键功能

## 文档

- 更新相关文档
- 添加必要的代码注释
- 更新 README（如需要）

## 社区准则

- 尊重所有贡献者
- 建设性的讨论
- 友好的交流环境
- 遵循开源精神

## 联系方式

如有任何问题，请通过以下方式联系：

- GitHub Issues
- 项目维护者邮箱

感谢您的贡献！