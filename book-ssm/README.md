# 图书进销存管理系统（book-ssm）

基于 **Spring 5 + Spring MVC + MyBatis（传统 SSM 架构）** 的图书进销存管理系统。

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 1.8 | 编译与运行环境 |
| Spring | 5.3.30 | IoC、AOP、事务管理 |
| Spring MVC | 5.3.30 | RESTful API、拦截器 |
| MyBatis | 3.5.14 | ORM、SQL 映射 |
| MySQL | 8.x | 关系型数据库 |
| Druid | 1.2.20 | 数据库连接池 |
| Jackson | 2.15.3 | JSON 序列化 |
| Maven | 3.x | 依赖管理与构建 |
| Tomcat | 9.x | Servlet 容器 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5 | 前端框架 |
| Vite | 6.0 | 构建工具 |
| Vue Router | 4.5 | 前端路由 |
| Element Plus | 2.9 | UI 组件库 |
| Axios | 1.7 | HTTP 客户端 |

## 项目结构

```
book-ssm/
├── pom.xml                                    # Maven 配置
├── README.md                                  # 本文件
├── sql/
│   └── book_inventory.sql                     # 数据库建表 + 测试数据
└── src/
    ├── main/
    │   ├── java/com/bookssm/
    │   │   ├── controller/                    # 控制器（8 个）
    │   │   │   ├── HealthController.java      # 健康检查
    │   │   │   ├── AuthController.java        # 登录/注册
    │   │   │   ├── BookController.java        # 图书管理
    │   │   │   ├── CategoryController.java    # 分类管理
    │   │   │   ├── StockController.java       # 库存管理
    │   │   │   ├── CartController.java        # 购物车
    │   │   │   ├── OrderController.java       # 用户订单
    │   │   │   └── AdminOrderController.java  # 管理员订单
    │   │   ├── service/                       # 服务接口（7 个）
    │   │   │   └── impl/                      # 服务实现（7 个）
    │   │   ├── mapper/                        # MyBatis Mapper 接口（6 个）
    │   │   ├── entity/                        # 实体类（8 个）
    │   │   ├── dto/                           # 数据传输对象
    │   │   │   ├── Result.java                # 统一响应
    │   │   │   └── PageResult.java            # 分页响应
    │   │   ├── interceptor/                   # 拦截器
    │   │   │   └── LoginInterceptor.java      # 登录拦截
    │   │   ├── filter/                        # Servlet 过滤器
    │   │   │   └── CorsFilter.java            # CORS 跨域
    │   │   ├── exception/                     # 异常处理
    │   │   │   ├── BusinessException.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   └── util/                          # 工具类
    │   │       ├── SessionUtil.java           # Session 管理
    │   │       ├── MD5Util.java               # MD5 加密
    │   │       ├── OrderNoUtil.java           # 订单号生成
    │   │       └── BatchNoUtil.java           # 批次号生成
    │   ├── resources/
    │   │   ├── applicationContext.xml         # Spring 主配置
    │   │   ├── spring-mvc.xml                 # Spring MVC 配置
    │   │   ├── mybatis-config.xml             # MyBatis 配置
    │   │   ├── jdbc.properties                # 数据库连接
    │   │   └── log4j.properties               # 日志配置
    │   │   └── mapper/                        # SQL 映射 XML（6 个）
    │   └── webapp/
    │       └── WEB-INF/
    │           └── web.xml                    # Servlet 部署描述符
    └── test/                                  # 单元测试
```

## 快速开始

### 1. 数据库导入

确保 MySQL 8.x 已安装并运行，然后执行：

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS book_inventory DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入表结构和测试数据
mysql -u root -p book_inventory < sql/book_inventory.sql
```

### 2. 修改数据库连接

编辑 `src/main/resources/jdbc.properties`，修改为你的数据库连接信息：

```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/book_inventory?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
jdbc.username=root
jdbc.password=你的密码
```

### 3. 后端启动

```bash
# 进入后端目录
cd book-ssm

# 编译打包
mvn clean package -DskipTests

# 将 target/book-ssm.war 部署到 Tomcat 的 webapps 目录
# 启动 Tomcat 后访问：http://localhost:8084/book_ssm_war/api/health
```

Tomcat 部署后的 Context Path 默认为 `book_ssm_war`（与 WAR 文件名一致）。

### 4. 前端启动

```bash
# 进入前端目录
cd book-ssm-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端开发服务器默认运行在 `http://localhost:5173`。

### 5. 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 可访问后台管理 |
| 普通用户 | zhangsan | 123456 | 可浏览图书、下单 |

## 功能模块

### 用户端

- **用户认证**：注册、登录、登出、获取当前用户信息
- **图书浏览**：分页搜索、按分类/状态筛选
- **购物车**：添加图书、修改数量、删除
- **我的订单**：创建订单、查看订单列表、查看订单详情

### 管理端

- **分类管理**：新增、修改、删除图书分类
- **图书管理**：新增、修改、删除图书，上下架操作
- **库存管理**：入库（生成批次）、查看批次、查看库存流水
- **订单管理**：查看所有订单、确认订单、发货（FIFO 出库）

## API 接口一览

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 公开接口（无需登录）

| 方法 | URL | 说明 |
|------|-----|------|
| GET | `/api/health` | 健康检查 |
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |

### 需要登录

| 方法 | URL | 说明 |
|------|-----|------|
| POST | `/api/auth/logout` | 登出 |
| GET | `/api/auth/current` | 当前用户信息 |
| GET | `/api/categories` | 分类列表 |
| GET | `/api/books` | 图书搜索/分页 |
| GET | `/api/cart` | 购物车列表 |
| POST | `/api/cart/add` | 加入购物车 |
| POST | `/api/cart/update` | 修改购物车数量 |
| POST | `/api/cart/delete` | 删除购物车项 |
| POST | `/api/orders/create` | 创建订单 |
| GET | `/api/orders/mine` | 我的订单 |
| GET | `/api/orders/{id}` | 订单详情 |

### 管理员接口

| 方法 | URL | 说明 |
|------|-----|------|
| POST | `/api/categories/create` | 新增分类 |
| POST | `/api/categories/update` | 修改分类 |
| POST | `/api/categories/delete` | 删除分类 |
| POST | `/api/books/create` | 新增图书 |
| POST | `/api/books/update` | 修改图书 |
| POST | `/api/books/delete` | 删除图书 |
| POST | `/api/books/on-sale` | 上架 |
| POST | `/api/books/off-sale` | 下架 |
| POST | `/api/stocks/in` | 入库 |
| GET | `/api/stocks/batches` | 批次列表 |
| GET | `/api/stocks/records` | 库存流水 |
| GET | `/api/admin/orders` | 订单管理 |
| PUT | `/api/admin/orders/{id}/confirm` | 确认订单 |
| PUT | `/api/admin/orders/{id}/ship` | 发货（FIFO 出库） |

## 数据库表结构

| 表名 | 说明 |
|------|------|
| user | 用户表（角色：ADMIN/USER） |
| category | 图书分类表 |
| book | 图书表（支持同名不同出版社不同 ISBN） |
| stock_batch | 库存批次表（每次入库独立批次） |
| stock_record | 库存流水表（入库/出库操作记录） |
| cart_item | 购物车表 |
| orders | 订单表（PENDING → CONFIRMED → DELIVERED → COMPLETED） |
| order_item | 订单明细表（价格快照） |

## 核心设计要点

### Session 登录

后端使用 HttpSession 保存登录用户信息。`LoginInterceptor` 拦截所有 `/api/**` 请求（排除登录、注册、健康检查），从 Session 中校验登录状态。未登录返回 401。

### CORS 跨域

`CorsFilter` 作为 Servlet Filter 在 DispatcherServlet 之前处理 OPTIONS 预检请求，允许前端开发服务器（5173/5174/5175 端口）携带 Cookie 跨域请求。支持 `Access-Control-Allow-Credentials: true`，Origin 白名单回显。

### FIFO 出库

发货时按批次入库时间从早到晚扣减库存（先进先出），使用数据库悲观锁（SELECT FOR UPDATE）加乐观锁（remain_quantity WHERE 校验）保证并发安全。

### 统一异常处理

`GlobalExceptionHandler` 捕获所有 `BusinessException` 和未预期异常，返回统一 JSON 格式，避免错误信息泄漏到前端。
