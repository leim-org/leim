# Leim - 简单的实时通信应用

Leim 是一个基于 Android 平台的实时通信应用，支持即时消息、联系人管理和群组聊天功能。

## 功能特性

- 📱 **实时消息**：基于 WebSocket 的即时通信
- 👥 **联系人管理**：添加和管理 Leim 号联系人
- 🔔 **消息通知**：及时的消息推送通知
- 🌙 **深色模式**：支持系统深色模式
- 📝 **Markdown 支持**：消息内容支持 Markdown 格式
- 🔄 **自动启动**：支持开机自启动功能
- 💾 **本地存储**：聊天记录和文件本地保存

## 技术栈

- **开发语言**：Kotlin
- **架构模式**：MVVM + Repository
- **数据库**：Room
- **网络通信**：WebSocket
- **UI 框架**：Material Design 3
- **依赖注入**：手动依赖注入
- **异步处理**：Kotlin Coroutines

## 项目结构

```
app/src/main/java/cn/lemwood/leim/
├── data/
│   ├── database/        # Room 数据库
│   │   ├── entities/    # 数据实体
│   │   └── dao/         # 数据访问对象
│   ├── repository/      # 数据仓库
│   └── websocket/       # WebSocket 客户端
├── ui/
│   ├── activities/      # Activity
│   ├── fragments/       # Fragment
│   ├── adapters/        # RecyclerView 适配器
│   └── viewmodels/      # ViewModel
├── utils/               # 工具类
├── services/            # 后台服务
└── receivers/           # 广播接收器
```

## 主要模块

### 1. 消息模块
- 会话列表展示
- 实时消息接收
- 消息状态管理（已读、已发送、已送达）

### 2. 联系人模块
- 联系人列表管理
- 在线状态显示
- 添加/删除联系人

### 3. 设置模块
- 个人资料管理
- 应用设置（通知、声音、震动）
- 账号管理

### 4. WebSocket 服务
- 后台保持连接
- 心跳检测
- 自动重连机制

## 权限说明

应用需要以下权限：

- **网络权限**：用于 WebSocket 连接
- **存储权限**：保存聊天记录和文件
- **通知权限**：显示消息通知
- **自启动权限**：开机自动启动服务
- **唤醒锁权限**：保持后台连接
- **前台服务权限**：运行后台服务

## 构建说明

本项目使用 GitHub Actions 进行自动构建，不支持本地构建。

### 构建要求
- Android SDK 34
- Kotlin 1.9.0
- Gradle 8.0+

### 构建步骤
1. 推送代码到 GitHub 仓库
2. GitHub Actions 自动触发构建
3. 构建完成后下载 APK 文件

## 开发状态

🚧 **当前状态**：基础框架开发完成，后端服务器正在开发中

### 已完成功能
- ✅ 项目基础架构
- ✅ 数据库设计和实现
- ✅ UI 界面设计
- ✅ WebSocket 客户端框架
- ✅ 权限管理
- ✅ 登录/注销功能

### 待开发功能
- 🔄 后端服务器
- 🔄 真实的消息收发
- 🔄 文件传输功能
- 🔄 群组聊天
- 🔄 消息加密

## 使用说明

### 安装
1. 下载最新版本的 APK 文件
2. 在 Android 设备上安装应用
3. 授予必要的权限

### 登录
当前为演示版本，可以使用任意信息进行登录：
- Leim 号：任意数字
- 密码：任意文本
- 昵称：任意文本

### 功能使用
- **消息**：查看会话列表（当前显示空状态）
- **联系人**：查看和管理联系人（包含模拟数据）
- **设置**：配置应用设置和个人资料

## 贡献指南

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目地址：[GitHub Repository](https://github.com/lemwood/leim)
- 问题反馈：[Issues](https://github.com/lemwood/leim/issues)

---

**注意**：这是一个演示项目，后端服务器正在开发中。当前版本仅用于展示客户端功能和界面设计。