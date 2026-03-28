# HorizonHub - 在线论坛交流平台

[![License](https://img.shields.io/badge/License-MIT%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green.svg)](https://spring.io/projects/spring-boot)

HorizonHub 是一个基于 Spring Boot 3.x 构建的现代化在线论坛交流平台，提供完整的社区功能，包括用户管理、文章发布、评论互动、版块管理、积分系统等核心能力。

## 📋 目录

- [项目简介](#项目简介)
- [技术架构](#技术架构)
- [核心功能](#核心功能)
- [技术栈](#技术栈)
- [模块结构](#模块结构)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [项目特色](#项目特色)
- [开发计划](#开发计划)
- [许可证](#许可证)

## 项目简介

HorizonHub 是一个企业级的在线论坛系统，采用前后端分离架构，支持用户注册登录、文章发布、评论互动、版块管理、积分系统、消息通知等功能。项目设计注重代码质量、可维护性和扩展性，适合作为学习参考或二次开发使用。

### 主要特性

- 🎯 **前后端分离**：清晰的模块划分，支持独立部署
- 🔐 **完善的权限体系**：基于会话的权限控制，支持管理员与普通用户角色
- 📝 **富文本编辑**：支持 Markdown 格式文章发布与预览
- 💬 **多级评论系统**：支持无限层级评论与回复
- 🎁 **积分激励体系**：完整的积分获取、消费与流水记录
- 📧 **邮件通知**：支持注册验证、找回密码等邮件通知功能
- 🔍 **全文搜索**：支持文章标题、内容的全文搜索
- 📊 **数据统计**：用户行为统计、文章热度排行等
- 🛡️ **内容审核**：文章、评论支持审核机制
- 🎨 **版块管理**：树形版块结构，支持多级分类

## 技术架构

### 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                      前端应用层                          │
│                  (Vue.js / React)                       │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      后端服务层                          │
├─────────────────────────────────────────────────────────┤
│  horizonhub-web   │   用户端接口服务                     │
│  horizonhub-admin │   管理端接口服务                     │
│  horizonhub-common│   公共服务与工具层                   │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      数据持久层                          │
│              MySQL + MyBatis + Druid                    │
└─────────────────────────────────────────────────────────┘
```

### 技术选型原则

- **稳定性优先**：选择成熟稳定的技术栈，降低系统风险
- **性能至上**：采用高性能组件，优化数据库访问与文件处理
- **可维护性**：清晰的分层架构，统一的编码规范
- **可扩展性**：预留扩展点，支持功能快速迭代

## 核心功能

### 用户端功能 (horizonhub-web)

- **用户系统**
    - 用户注册（邮箱验证码）
    - 用户登录（图形验证码）
    - 密码找回（邮件重置）
    - 个人资料管理
    - 头像上传
    - 积分流水查询

- **文章系统**
    - 文章发布（支持 Markdown 编辑器）
    - 文章编辑与删除
    - 附件上传与下载
    - 文章搜索
    - 文章详情浏览
    - 阅读计数

- **互动系统**
    - 多级评论
    - 文章点赞
    - 评论点赞
    - 收藏功能

- **消息系统**
    - 系统消息通知
    - 评论回复通知
    - 点赞通知
    - 消息已读/未读管理

### 管理端功能 (horizonhub-admin)

- **用户管理**
    - 用户列表查询
    - 用户状态管理（启用/禁用）
    - 发送系统消息
    - 积分奖励

- **内容管理**
    - 文章列表查询
    - 文章审核
    - 文章删除
    - 文章置顶
    - 版块调整

- **评论管理**
    - 评论列表查询
    - 评论审核
    - 评论删除

- **版块管理**
    - 版块树管理
    - 版块新增/编辑
    - 版块删除
    - 版块排序

- **系统设置**
    - 审核配置
    - 评论配置
    - 发帖配置
    - 点赞配置
    - 注册配置
    - 邮件配置

## 技术栈

### 后端核心技术

#### 基础框架

| 技术          | 版本    | 说明                 |
|-------------|-------|--------------------|
| Java        | 21    | 开发语言，使用最新 LTS 版本   |
| Spring Boot | 3.5.7 | 应用框架，提供快速开发能力      |
| Spring MVC  | 6.x   | Web 框架，处理 HTTP 请求  |
| Spring AOP  | 6.x   | 面向切面编程，实现统一拦截与参数校验 |

#### 数据持久层

| 技术                          | 版本     | 说明                       |
|-----------------------------|--------|--------------------------|
| MyBatis                     | 3.0.5  | ORM 框架，灵活的 SQL 映射        |
| MyBatis Spring Boot Starter | 3.0.5  | MyBatis 与 Spring Boot 集成 |
| MySQL                       | 8.3.0  | 关系型数据库                   |
| Druid                       | 1.2.27 | 数据库连接池，提供监控能力            |

#### 工具库

| 技术                   | 版本     | 说明                  |
|----------------------|--------|---------------------|
| Hutool               | 5.8.43 | Java 工具库，提供加密、日期等工具 |
| Apache Commons Lang3 | 3.14.0 | 字符串与对象工具            |
| Apache Commons Codec | 1.16.1 | 编码解码工具              |
| Apache Commons IO    | 2.14.0 | 文件 IO 工具            |
| Jackson              | 2.18.6 | JSON 序列化与反序列化       |
| OkHttp               | 4.12.0 | HTTP 客户端，用于跨模块通信    |

#### 日志与邮件

| 技术               | 版本     | 说明     |
|------------------|--------|--------|
| Logback          | 1.5.25 | 日志框架   |
| Spring Boot Mail | 3.5.12 | 邮件发送支持 |

#### 容器与构建

| 技术            | 版本      | 说明         |
|---------------|---------|------------|
| Apache Tomcat | 10.1.52 | Servlet 容器 |
| Maven         | 3.x     | 项目构建与依赖管理  |

### 后端架构特色

#### 1. 统一拦截机制

通过 `@GlobalInterceptor` 注解实现统一的登录校验、参数校验与频率限制：

```java

@RequestMapping("/updateUserStatus")
@GlobalInterceptor(checkParams = true)
public ResponseVO<Void> updateUserStatus(
        @VerifyParam(required = true) Integer status,
        @VerifyParam(required = true) String userId) {
    // 业务逻辑
}
```

#### 2. 参数校验框架

基于 `@VerifyParam` 注解的参数校验框架，支持：

- 必填校验（`required`）
- 长度校验（`min`、`max`）
- 正则校验（`regex`）
- 嵌套对象字段校验

#### 3. 切面编程实践

使用 Spring AOP 实现：

- 登录态校验
- 参数合法性校验
- 操作频率限制
- 异常统一处理

#### 4. 分层设计

```
Controller 层：
  - 参数接收与校验
  - 会话用户提取
  - 业务编排
  - 响应包装

Service 层：
  - 核心业务逻辑
  - 事务控制
  - 缓存管理
  - 状态流转

Mapper 层：
  - 数据库访问
  - 动态 SQL 构建
  - 分页查询
```

#### 5. 配置化驱动

通过 `application.yml` 集中管理配置项：

- 数据库连接配置
- 文件存储路径
- 邮件服务配置
- 管理员账号配置
- 内部接口密钥
- 开发模式开关

#### 6. 文件处理策略

- 头像存储：`{projectFolder}/avatar/{userId}.jpg`
- 文章图片：`{projectFolder}/image/{yyyyMM}/{imageName}`
- 文章附件：`{projectFolder}/file/{yyyyMM}/{fileName}`
- 支持浏览器类型识别与中文文件名处理

#### 7. 缓存设计

基于 `ConcurrentHashMap` 实现内存缓存：

- 系统配置缓存
- 用户会话缓存
- 频率限制计数

后续支持迁移到 Redis，实现分布式缓存。

#### 8. 跨模块通信

管理端与用户端通过内部 API 通信：

- 基于签名的安全验证（`appKey` + `timestamp` + `sign`）
- 时间窗校验（10 秒内有效）
- 配置实时同步

## 模块结构

```
horizonhub/
├── horizonhub-common/          # 公共模块
│   ├── entity/                 # 实体类、DTO、VO
│   ├── service/                # 通用服务
│   ├── mapper/                 # MyBatis Mapper
│   ├── utils/                  # 工具类
│   ├── annotation/             # 自定义注解
│   ├── enums/                  # 枚举类
│   └── exception/              # 异常定义
│
├── horizonhub-web/             # 用户端模块
│   ├── controller/             # 用户端接口
│   ├── aspect/                 # 切面
│   ├── interceptor/            # 拦截器
│   └── config/                 # 配置类
│
├── horizonhub-admin/           # 管理端模块
│   ├── controller/             # 管理端接口
│   ├── aspect/                 # 切面
│   ├── interceptor/            # 拦截器
│   └── config/                 # 配置类
│
├── sql/                        # 数据库脚本
├── doc/                        # 项目文档
└── pom.xml                     # Maven 父工程配置
```

### 模块说明

#### horizonhub-common（公共模块）

提供所有模块共享的基础能力：

- **实体定义**：数据库实体、查询对象、值对象
- **服务接口**：用户、文章、评论、版块等核心服务
- **数据访问**：MyBatis Mapper 接口与 XML
- **工具类**：文件处理、JSON 转换、缓存管理、邮件发送
- **注解定义**：参数校验、全局拦截等自定义注解
- **枚举类**：状态码、用户状态、审核状态等
- **异常体系**：业务异常、系统异常定义

#### horizonhub-web（用户端模块）

面向论坛用户的业务接口：

- **账号管理**：注册、登录、找回密码、退出
- **用户中心**：个人资料、积分记录、我的文章、消息管理
- **文章功能**：发布、编辑、删除、搜索、详情、附件下载
- **互动功能**：评论、点赞、收藏
- **文件服务**：头像、图片、附件访问
- **统一拦截**：登录校验、参数校验、频率限制

#### horizonhub-admin（管理端模块）

面向管理员的运营管理接口：

- **用户管理**：用户列表、状态管理、消息推送
- **内容管理**：文章审核、删除、置顶、版块调整
- **评论管理**：评论审核、删除
- **版块管理**：版块树、新增、编辑、删除、排序
- **系统设置**：审核、评论、发帖、点赞、注册、邮件配置
- **文件服务**：头像、图片、附件访问
- **统一拦截**：管理员登录校验、参数校验

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- IDE（推荐 IntelliJ IDEA）

### 安装步骤

1. **克隆项目**

```bash
git clone https://github.com/yourusername/horizonhub.git
cd horizonhub
```

2. **创建数据库**

```bash
mysql -u root -p
CREATE DATABASE horizonhub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **导入数据库脚本**

```bash
mysql -u root -p horizonhub < sql/init.sql
```

4. **配置应用**

编辑 `horizonhub-web/src/main/resources/application.yml` 和 `horizonhub-admin/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/horizonhub?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

5. **编译项目**

```bash
mvn clean install
```

6. **启动服务**

```bash
# 启动用户端
cd horizonhub-web
mvn spring-boot:run

# 启动管理端
cd horizonhub-admin
mvn spring-boot:run
```

7. **访问应用**

- 用户端：http://localhost:8081
- 管理端：http://localhost:8082

## 配置说明

### 核心配置项

#### 数据库配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/horizonhub
    username: root
    password: yourpassword
    type: com.alibaba.druid.pool.DruidDataSource
```

#### 文件存储配置

```yaml
project:
  folder: /path/to/storage # 文件存储根目录
```

#### 邮件配置

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 465
    username: your_email@qq.com
    password: your_auth_code
    protocol: smtp
    default-encoding: UTF-8
```

#### 管理员配置

```yaml
admin:
  account: admin
  password: admin123
```

#### 开发模式配置

```yaml
isDev:
  open: true # 开发环境自动登录
  test-user-email: test@example.com
```

### 打包步骤

```bash
# 打包用户端
cd horizonhub-web
mvn clean package

# 启动管理端
cd horizonhub-admin
mvn clean package
```

## 项目特色

### 1. 工程化实践

- **统一响应体**：所有接口使用 `ResponseVO<T>` 包装，保持契约一致
- **统一异常处理**：全局异常处理器，规范错误码与错误信息
- **统一参数校验**：基于注解的声明式参数校验框架
- **统一日志规范**：按模块、级别分离日志输出

### 2. 安全机制

- **会话管理**：基于 Session 的用户身份管理
- **签名验证**：内部接口使用签名机制保证安全
- **时间窗校验**：防止重放攻击
- **频率限制**：发帖、评论、点赞等操作频率限制
- **内容审核**：文章、评论支持审核机制

### 3. 性能优化

- **分页查询**：统一分页框架，避免全表扫描
- **缓存机制**：系统配置、用户会话内存缓存
- **连接池**：Druid 连接池，提供高性能数据库访问
- **文件流式传输**：大文件下载采用流式传输，降低内存占用

### 4. 可维护性

- **清晰分层**：Controller-Service-Mapper 三层架构
- **职责单一**：每层职责明确，降低耦合
- **代码规范**：统一编码风格与注释规范
- **文档完善**：详细的接口文档与技术实现报告

### 5. 可扩展性

- **模块化设计**：公共模块、用户端、管理端独立部署
- **配置化驱动**：核心参数配置化，支持快速调整
- **预留扩展点**：服务层接口设计，支持功能扩展
- **数据库设计**：预留字段，支持功能迭代

## 开发计划

### 已完成功能 ✅

- [x] 用户注册、登录、找回密码
- [x] 个人资料管理与头像上传
- [x] 文章发布、编辑、删除、搜索
- [x] 多级评论与回复
- [x] 点赞功能（文章、评论）
- [x] 积分系统与流水记录
- [x] 消息通知系统
- [x] 版块管理（树形结构）
- [x] 管理端用户管理
- [x] 管理端内容审核
- [x] 管理端系统配置
- [x] 内部接口跨模块通信

### 开发中功能 🚧

- [ ] Redis 缓存集成
- [ ] 文章标签系统
- [ ] 用户关注与粉丝
- [ ] 热门文章排行
- [ ] 数据统计与报表

### 计划功能 📅

- [ ] 前端页面开发（Vue.js）
- [ ] RESTful API 规范升级
- [ ] JWT 认证机制
- [ ] WebSocket 实时消息推送
- [ ] Elasticsearch 全文搜索
- [ ] OSS 对象存储集成
- [ ] Docker 容器化部署
- [ ] API 接口文档（Swagger）
- [ ] 单元测试与集成测试
- [ ] CI/CD 自动化部署

## 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助改进项目！

### 贡献步骤

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 保持代码整洁与注释完整
- 单元测试覆盖核心逻辑
- 提交信息清晰明确

## 许可证

本项目采用 MIT License 开源协议，详见 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目地址：https://github.com/yourusername/horizonhub
- 问题反馈：https://github.com/yourusername/horizonhub/issues

## 致谢

感谢所有开源技术的贡献者，本项目使用了以下优秀的开源项目：

- Spring Boot
- MyBatis
- Druid
- Hutool
- Apache Commons
- OkHttp

---

⭐ 如果这个项目对你有帮助，欢迎 Star 支持！
