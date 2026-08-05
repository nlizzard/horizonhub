# HorizonHub - 前后端分离论坛项目

[![License](https://img.shields.io/badge/License-MIT%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.12-green.svg)](https://spring.io/projects/spring-boot)

HorizonHub 是一个基于 Java 21 和 Spring Boot 3 的论坛系统。当前仓库为后端 API 仓库，包含用户侧接口、管理侧接口和公共业务模块；前端页面工程已独立维护在 [horizonhub-front](https://github.com/nlizzard/horizonhub-front)。

## 项目说明

- 当前仓库：`https://github.com/nlizzard/horizonhub`
- 前端仓库：`https://github.com/nlizzard/horizonhub-front`（现在已经移动到当前仓库的horizonhub-front目录下了，未来不单独维护前端仓库）
- 当前仓库只包含后端代码，不包含前端页面工程
- `horizonhub-web` 和 `horizonhub-admin` 是后端服务模块，不是前端项目

## 当前技术栈

| 分类 | 技术 | 说明 |
| --- | --- | --- |
| 语言与构建 | Java 21、Maven | 多模块 Maven 工程 |
| 核心框架 | Spring Boot 3.5.12、Spring MVC、Spring AOP | 接口开发、拦截、启动管理 |
| 数据访问 | MyBatis 3.0.5、MySQL、Druid 1.2.27 | Mapper + XML，数据库连接池 |
| 缓存 | Spring Data Redis | 系统配置缓存、操作频率控制 |
| 通信与工具 | OkHttp 4.12.0、Jackson 2.18.6、Hutool 5.8.43、Apache Commons | HTTP 调用、JSON、常用工具 |
| 邮件与日志 | Spring Boot Mail、Logback 1.5.25 | 邮箱验证码、日志输出 |

## 当前实现能力

### 用户侧 API（`horizonhub-web`）

- 图形验证码、邮箱验证码、注册、登录、退出、找回密码
- 板块树加载与发帖板块选择
- 文章列表、文章详情、关键字搜索
- 发帖与编辑，支持富文本和 Markdown 两种编辑模式
- 封面图、正文图片、附件上传与下载
- 文章点赞、评论点赞
- 评论列表、回复评论、评论图片上传
- 用户资料修改、头像上传
- 我的文章、积分流水、消息中心
- 附件积分下载与下载通知

### 管理侧 API（`horizonhub-admin`）

- 管理员验证码登录
- 用户列表查询、启用/禁用、发送站内消息、积分发放
- 文章列表、删除、审核、置顶、调整所属板块
- 评论列表、评论审核、评论删除
- 板块树管理、板块新增/编辑/删除、排序调整
- 系统设置读取与保存
- 管理侧文件访问接口

### 公共能力（`horizonhub-common`）

- 实体、DTO、VO、Query、Mapper、Service 的公共实现
- `@GlobalInterceptor` + `@VerifyParam` 参数校验与统一拦截
- 基于 Session 的登录态处理
- Redis 缓存系统配置，并在服务启动时预热
- 基于 Redis Lua 的操作频率限制
- 用户积分、消息通知、审核状态流转
- 本地文件存储与图片/附件访问封装

## 工程结构

```text
horizonhub/
├── horizonhub-common/    # 公共模块：entity、service、mapper、utils、config
├── horizonhub-web/       # 用户侧后端 API
├── horizonhub-admin/     # 管理侧后端 API
├── sql/                  # 数据库脚本
├── doc/                  # 过程文档/阶段记录
└── pom.xml               # Maven 父工程
```

## 运行环境

- JDK 21+
- Maven 3.9+
- MySQL 8.x
- Redis 6.x 或以上

## 快速开始

### 1. 克隆仓库

```bash
git clone https://github.com/nlizzard/horizonhub.git
cd horizonhub
```

### 2. 创建数据库并导入脚本

应用配置默认连接的数据库名是 `horizonhub`，请先创建数据库，再导入 [sql/horizonhub.sql](sql/horizonhub.sql)。

```sql
CREATE DATABASE horizonhub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```bash
mysql -u root -p horizonhub < sql/horizonhub.sql
```

### 3. 修改配置

需要至少检查以下两个配置文件：

- `horizonhub-web/src/main/resources/application.yml`
- `horizonhub-admin/src/main/resources/application.yml`

重点配置项如下：

```yaml
server:
  port: 8080 # 管理端默认是 8081
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/horizonhub?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password

  data:
    redis:
      host: 127.0.0.1
      port: 6379

projectFolder: D:/your/path/horizonhub

send:
  mail:
    open: false
```

### 4. 构建项目

```bash
mvn clean install
```

### 5. 启动服务

```bash
mvn -pl horizonhub-web -am spring-boot:run
```

```bash
mvn -pl horizonhub-admin -am spring-boot:run
```

### 6. 默认访问地址

- 用户侧 API：`http://localhost:8080/api`
- 管理侧 API：`http://localhost:8081/api`

## 配置说明

### Redis

- Redis 不是预留配置，当前实现已经实际使用
- 系统启动时会将系统设置写入 Redis
- 用户发帖、评论、点赞、上传等频率控制依赖 Redis

### 邮件验证码

- `send.mail.open=false` 时，邮箱验证码不会真正发出，而是写入日志，适合本地开发
- `send.mail.open=true` 时，需要补全 `spring.mail.*` 配置

### 文件目录

项目通过 `projectFolder` 指定文件根目录，运行时会在该目录下使用这些路径：

- `file/avatar`：头像
- `file/images`：文章图片、评论图片、封面图
- `file/attachment`：文章附件
- `file/temp`：临时上传文件

### 管理员账号

管理端登录账号来自 `horizonhub-admin/src/main/resources/application.yml` 中的以下配置：

```yaml
admin:
  adminName: admin
  password: 123456
```

## 开发说明

- 当前认证方式是 Session，不是 JWT
- 当前仓库没有内置前端页面，前后端联调请配合 `horizonhub-front` 仓库
- 用户侧与管理侧服务都默认挂载在 `/api` 上下文路径下
- `doc/` 目录保存的是阶段性开发文档，不是接口文档
- 仓库当前未提供完整自动化测试用例，联调时建议优先验证登录、发帖、评论、审核、附件下载等核心流程

## 关联仓库

- 后端仓库：`https://github.com/nlizzard/horizonhub`
- 前端仓库：`https://github.com/nlizzard/horizonhub-front`

## 贡献

欢迎通过 Issue 或 Pull Request 参与改进。

```bash
git checkout -b feature/your-feature
git commit -m "feat: your feature"
git push origin feature/your-feature
```

## 许可证

本项目采用 [MIT License](LICENSE)。
