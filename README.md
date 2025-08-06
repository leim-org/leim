# Leim - 简单的实时通信应用

## 项目概述

Leim 是一个基于 Android 平台的实时通信应用，类似于 QQ 的基础功能。应用采用现代化的 MVVM 架构，支持 WebSocket 实时通信和 Markdown 消息格式。

## 主要功能

- **消息模块**：首页展示对话列表，支持实时消息收发
- **联系人模块**：管理 Leim 号和群组联系人
- **设置模块**：账号设置和应用基础配置
- **WebSocket 服务**：后台保持连接，确保消息实时性
- **登录检测**：应用启动时自动检查登录状态

## 技术栈

- **开发语言**：Kotlin
- **架构模式**：MVVM + Repository
- **数据库**：Room Database
- **网络通信**：WebSocket (Java-WebSocket)
- **UI 框架**：Material Design Components
- **消息格式**：Markdown 支持 (Markwon)
- **图片加载**：Glide
- **权限管理**：Dexter

## 项目结构

```
app/src/main/java/cn/lemwood/leim/
├── data/                    # 数据层
│   ├── database/           # Room 数据库
│   ├── repository/         # 数据仓库
│   ├── model/             # 数据模型
│   └── websocket/         # WebSocket 客户端
├── ui/                     # UI 层
│   ├── activities/        # Activity
│   ├── fragments/         # Fragment
│   ├── adapters/          # RecyclerView 适配器
│   └── viewmodels/        # ViewModel
├── utils/                  # 工具类
├── services/              # 服务类
└── receivers/             # 广播接收器
```

## 权限要求

- **相册权限**：用于发送图片和文件
- **存储权限**：保存聊天记录和文件
- **通知权限**：接收消息通知
- **自启动权限**：开机自动启动服务

## 开发环境

- **Android Studio**：Arctic Fox 或更高版本
- **Kotlin**：1.9.10
- **Gradle**：8.1.2
- **最低 SDK**：24 (Android 7.0)
- **目标 SDK**：34 (Android 14)

## 构建说明

1. 克隆项目到本地
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步完成
4. 连接 Android 设备或启动模拟器
5. 点击运行按钮构建并安装应用

## 注意事项

- 当前版本使用模拟数据进行功能演示
- 后端服务器正在开发中，WebSocket 连接可能无法正常工作
- 建议在 GitHub Actions 中进行正式构建
- 不要在本地进行生产环境构建

## 版本信息

- **版本号**：1.0
- **版本代码**：1
- **包名**：cn.lemwood.leim

## 开发规范

- 严格遵循 Kotlin 官方编码规范
- 使用 MVVM 架构模式
- 合理使用 LiveData 和 ViewModel
- 保持代码简洁和可维护性
- 添加必要的注释和文档

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证，详情请参阅 LICENSE 文件。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 项目地址：https://github.com/lemwood/leim
- 邮箱：support@lemwood.cn

---

**注意**：这是一个开发中的项目，功能可能不完整或存在 Bug。欢迎提交 Issue 和 Pull Request！