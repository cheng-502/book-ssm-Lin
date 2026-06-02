# 图书进销存管理系统

基于 **Spring 5 + Spring MVC + MyBatis（传统 SSM 架构）** 的图书进销存管理系统，前端使用 Vue 3 + Element Plus。

## 项目结构

```
├── book-ssm/                    # 后端（SSM + Maven + Tomcat 9）
│   ├── sql/                    # 数据库脚本
│   ├── README.md               # 后端详细文档
│   ├── DEVELOPMENT.md          # 开发文档
│   └── src/main/resources/
│       ├── db-secret.properties # 数据库密码（本地，不提交 Git）
│       └── jdbc.properties      # 数据库连接模板
├── book-ssm-frontend/           # 前端（Vue 3 + Vite）
│   ├── DESIGN.md               # 前端设计文档
│   └── PRODUCT.md              # 产品设计文档
├── database.md                  # 数据库设计文档
└── SSM项目代码审查报告.md       # 课程实验审查报告
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring 5.3.30 + Spring MVC + MyBatis 3.5.14 |
| AOP | AspectJ + 自定义日志切面（LogAspect） |
| 数据库 | MySQL 8.x + Druid 1.2.20 + JdbcTemplate |
| 前端 | Vue 3.5 + Vite 6 + Element Plus 2.9 + Axios |
| 部署 | WAR 包 → Tomcat 9 |

## 快速开始

### 1. 导入数据库

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS book_inventory DEFAULT CHARSET utf8mb4;"
mysql -u root -p book_inventory < book-ssm/sql/book_inventory.sql
```

### 2. 配置数据库连接

项目采用**密钥与配置分离**策略：密码不写入 `jdbc.properties`（该文件会提交到 Git），而是放在独立的 **Java Properties 文件** `db-secret.properties` 中（已加入 `.gitignore`，不提交）。

**第一步：创建密钥文件**

在 `book-ssm/src/main/resources/` 下新建 `db-secret.properties`（纯文本文件，格式同 `jdbc.properties`）：

```bash
# 直接在终端执行以下命令即可创建
echo "db.password=你的MySQL密码" > book-ssm/src/main/resources/db-secret.properties
```

文件内容示例：
```properties
db.password=123456
```

> `db-secret.properties` 是 **Java Properties 格式文件**，与 `jdbc.properties` 类型相同，由 Spring 的 `<context:property-placeholder>` 统一加载。

**第二步：理解占位符机制**

`jdbc.properties` 中写的是占位符而非真实密码：
```properties
jdbc.password=${db.password}   # Spring 会自动从 db-secret.properties 中查找 db.password 的值
```

`applicationContext.xml` 配置了两个文件**先后加载**：
```xml
<context:property-placeholder
    location="classpath:db-secret.properties,      <!-- 先加载密钥 -->
              classpath:jdbc.properties"            <!-- 后加载模板 -->
    ignore-unresolvable="true"/>
```

Spring 先读 `db-secret.properties` 得到 `db.password`，再读 `jdbc.properties` 时将 `${db.password}` 替换为真实值，最终 Druid 连接池拿到完整密码。

如果 `db-secret.properties` 不存在或缺少 `db.password` 键，`jdbc.password` 的值会保持为字面量 `${db.password}`，数据库连接将失败。

### 3. 启动后端

```bash
cd book-ssm
mvn clean package -DskipTests
# 将 target/book-ssm.war 部署到 Tomcat 9 webapps 目录
# 访问 http://localhost:8080/book-ssm/api/health
# 测试 JdbcTemplate: GET /api/health/db
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
- **AOP 日志切面** — @Aspect 切面记录 Service 层方法调用参数与耗时
- **JdbcTemplate** — 共用 Druid 数据源，演示 Spring JDBC 基本查询
- **统一异常处理** — GlobalExceptionHandler 捕获所有异常，返回 JSON
- **密钥分离** — 数据库密码通过 `db-secret.properties` 独立管理，不入版本库

## 详细文档

- [后端 README](book-ssm/README.md) — API 接口、数据库设计、完整项目结构
- [开发文档](book-ssm/DEVELOPMENT.md) — 需求分析、技术架构、核心流程、FIFO 详解
- [数据库设计](database.md) — 表结构、ER 关系、索引策略
- [前端设计文档](book-ssm-frontend/DESIGN.md) — 前端架构与设计说明
- [产品设计文档](book-ssm-frontend/PRODUCT.md) — 产品功能与交互说明
- [代码审查报告](SSM项目代码审查报告.md) — 课程实验逐项审查与答辩题库
