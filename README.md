# 图书进销存管理系统

基于 **Spring 5 + Spring MVC + MyBatis（传统 SSM 架构）** 的图书进销存管理系统，前端使用 Vue 3 + Element Plus。

## 项目结构

```
├── book-ssm/                # 后端（SSM + Maven + Tomcat）
│   ├── sql/                 # 数据库脚本
│   ├── README.md            # 后端详细文档
│   └── DEVELOPMENT.md       # 开发文档
└── book-ssm-frontend/       # 前端（Vue 3 + Vite）
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring 5.3.30 + Spring MVC + MyBatis 3.5.14 |
| 数据库 | MySQL 8.x + Druid 1.2.20 |
| 前端 | Vue 3.5 + Vite 6 + Element Plus 2.9 + Axios |
| 部署 | WAR 包 → Tomcat 9 |

## 快速开始

### 1. 导入数据库

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS book_inventory DEFAULT CHARSET utf8mb4;"
mysql -u root -p book_inventory < book-ssm/sql/book_inventory.sql
```

### 2. 配置数据库连接

编辑 `book-ssm/src/main/resources/jdbc.properties`，修改 `jdbc.username` 和 `jdbc.password`。

### 3. 启动后端

```bash
cd book-ssm
mvn clean package -DskipTests
# 将 target/book-ssm.war 部署到 Tomcat webapps 目录
# 访问 http://localhost:8084/book_ssm_war/api/health
```

### 4. 启动前端

```bash
cd book-ssm-frontend
npm install
npm run dev
# 访问 http://localhost:5173
```

### 5. 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 普通用户 | zhangsan | 123456 |

## 功能概览

- **用户端**：注册登录、图书搜索、购物车、下单
- **管理端**：分类/图书 CRUD、库存入库、批次管理、订单发货（FIFO 出库）

## 核心设计

- **Session 登录** — HttpSession + LoginInterceptor 拦截鉴权
- **CORS 跨域** — Servlet Filter 在 DispatcherServlet 前处理，支持 Credentials
- **FIFO 出库** — 悲观锁（FOR UPDATE）+ 乐观锁（remain_quantity 校验）保证并发安全
- **统一异常处理** — GlobalExceptionHandler 捕获所有异常，返回 JSON

## 详细文档

- [后端 README](book-ssm/README.md) — API 接口、数据库设计、完整项目结构
- [开发文档](book-ssm/DEVELOPMENT.md) — 需求分析、技术架构、核心流程、FIFO 详解
