# 图书进销存管理系统 — 开发文档

---

## 一、项目背景

图书进销存管理系统是一个面向中小型书店或图书批发商的 Web 应用。核心需求是管理图书的**入库（进货）、上架销售（销）、库存盘点（存）**三大环节，同时支持用户端在线选购、下单，管理端处理订单并发货。

传统图书门店存在以下痛点：
- 库存管理依赖纸质台账或 Excel，容易出错且难以追溯
- 同名图书可能存在不同出版社、不同 ISBN 版本，需精确区分
- 多批次进货成本不同，出库时需按 FIFO（先进先出）核算成本
- 订单状态缺乏系统化管理，发货与库存脱节

本系统采用传统 **SSM（Spring + Spring MVC + MyBatis）架构**，以 WAR 包形式部署到 Tomcat，适用于教学演示和中小型场景。

---

## 二、需求分析

### 2.1 角色定义

| 角色 | 说明 |
|------|------|
| 普通用户（USER） | 浏览图书、加入购物车、下单 |
| 管理员（ADMIN） | 管理图书分类、图书信息、库存入库、处理订单 |

### 2.2 功能需求

**用户端：**
- 注册与登录（Session 机制）
- 分页搜索图书（按书名、作者、出版社、分类、状态筛选）
- 购物车管理（添加、修改数量、删除）
- 从购物车创建订单
- 查看个人订单列表与详情

**管理端：**
- 图书分类 CRUD
- 图书 CRUD（含上下架操作）
- 库存入库（创建独立批次，记录供应商和成本价）
- 批次查询（按图书分页）
- 库存流水查询（按图书、操作类型筛选）
- 订单管理：确认订单、FIFO 发货出库

### 2.3 非功能需求

- Session 登录鉴权，未登录接口返回 401
- 前端跨域支持（携带 Cookie）
- 统一 JSON 响应格式
- 全局异常处理，避免堆栈泄漏
- 数据库事务保证库存扣减原子性

---

## 三、系统功能模块

```
图书进销存管理系统
├── 用户模块
│   ├── 注册（MD5 密码加密）
│   ├── 登录/登出（HttpSession）
│   └── 获取当前用户信息
├── 分类模块
│   └── 管理员 CRUD（删除前校验关联图书）
├── 图书模块
│   ├── 分页搜索（多条件模糊查询）
│   ├── 管理员 CRUD
│   └── 上下架控制
├── 库存模块
│   ├── 入库（生成批次号 + 库存流水）
│   ├── 批次管理（分页查询）
│   └── 流水查询（按操作类型 IN/OUT 筛选）
├── 购物车模块
│   ├── 添加（已存在则累加数量）
│   ├── 修改数量
│   └── 删除
├── 订单模块（用户端）
│   ├── 从购物车创建订单（价格快照 + 清空购物车）
│   └── 查询个人订单（列表 + 详情）
└── 订单模块（管理端）
    ├── 订单列表（按状态、用户名筛选）
    ├── 确认订单（PENDING → CONFIRMED）
    └── 发货出库（CONFIRMED → DELIVERED，FIFO 扣库存）
```

---

## 四、技术架构

### 4.1 架构分层

```
┌─────────────────────────────────────┐
│           浏览器 / Vue 前端          │
├─────────────────────────────────────┤
│  CorsFilter（跨域）                  │
│  CharacterEncodingFilter（编码）     │
├─────────────────────────────────────┤
│  DispatcherServlet                  │
│  ├── LoginInterceptor（登录拦截）     │
│  ├── Controller（REST 接口）         │
│  └── GlobalExceptionHandler（异常）  │
├─────────────────────────────────────┤
│  Service 层（业务逻辑 + 事务）        │
├─────────────────────────────────────┤
│  Mapper 层（MyBatis + SQL XML）     │
├─────────────────────────────────────┤
│  Druid 连接池 → MySQL 8.x           │
└─────────────────────────────────────┘
```

### 4.2 请求处理流程

```
HTTP 请求
  → CorsFilter.doFilter()      # 添加 CORS 头，OPTIONS 直接返回 200
  → CharacterEncodingFilter    # UTF-8 编码
  → DispatcherServlet
    → LoginInterceptor.preHandle()  # 校验 Session 登录状态
    → Controller.method()
      → Service.method()       # @Transactional 事务边界
        → Mapper.method()
          → MyBatis → SQL XML → MySQL
    → GlobalExceptionHandler   # 异常转 JSON
  → HTTP 响应
```

### 4.3 关键配置文件

| 文件 | 说明 |
|------|------|
| `web.xml` | Servlet 3.1：CorsFilter → CharacterEncodingFilter → ContextLoaderListener → DispatcherServlet(`/`) |
| `applicationContext.xml` | Spring 主容器：数据源、SqlSessionFactory、Mapper 扫描、事务管理器 |
| `spring-mvc.xml` | Spring MVC 子容器：Controller 扫描、注解驱动、拦截器注册 |
| `mybatis-config.xml` | MyBatis：驼峰命名映射、LOG4J 日志、延迟加载 |
| `jdbc.properties` | 数据库连接信息 |
| `log4j.properties` | 日志级别（mapper 包 DEBUG 输出 SQL） |

---

## 五、数据库设计

### 5.1 ER 关系

```
user ──1:N── orders ──1:N── order_item ──N:1── book ──N:1── category
                │
user ──1:N── cart_item ──N:1── book
                             │
                             book ──1:N── stock_batch
                             book ──1:N── stock_record
                                    stock_record ──N:1── stock_batch
                                    stock_record ──N:1── orders
```

### 5.2 核心表说明

**user** — 用户表。`role` 字段区分 `ADMIN` 与 `USER`。密码使用 MD5 加盐前哈希存储（教学级别，生产环境应使用 BCrypt）。

**book** — 图书表。以 ISBN 为唯一键，支持区分同名不同出版社的图书（如"三体"的重庆出版社版和猫头鹰出版社版）。`stock` 为冗余字段，由入库/出库操作实时更新。

**stock_batch** — 库存批次表。每次入库创建一条独立批次，记录该批次的成本价和剩余数量。FIFO 出库时按 `created_at` 升序扣减。

**stock_record** — 库存流水表。记录每次入库（IN）和出库（OUT）的详细变动。`before_stock` 和 `after_stock` 记录批次层级的前后库存快照。

**orders** — 订单表。状态流转：`PENDING → CONFIRMED → DELIVERED → COMPLETED`，任意状态可转 `CANCELLED`。

**order_item** — 订单明细表。`book_title` 和 `book_price` 为下单时的快照，避免后续图书信息变更影响历史订单。

### 5.3 索引策略

- 所有外键列均建有普通索引（如 `idx_book_id`、`idx_user_id`）
- 唯一索引：`uk_isbn`（book）、`uk_order_no`（orders）、`uk_batch_no`（stock_batch）、`uk_user_book`（cart_item 用户+图书联合唯一）
- 排序/筛选列建有索引：`idx_created_at`、`idx_status`、`idx_type`

---

## 六、核心业务流程

### 6.1 用户注册与登录

```
注册：
1. 校验 username 不为空且未被占用
2. 密码 MD5 哈希
3. 默认角色 USER，状态 1（正常）
4. 插入 user 表

登录：
1. 根据 username 查询 user
2. 比对 MD5 密码
3. 校验 status 为 1（未被禁用）
4. 将 User 对象存入 HttpSession（key: "loginUser"）
```

### 6.2 创建订单

```
1. 校验用户已登录
2. 从购物车查询用户选中的购物车项
3. 逐项校验：
   - 图书存在且状态为在售（status=1）
   - 库存充足（stock >= quantity）
   - 数量有效（> 0）
4. 计算每项小计和订单总金额
5. 生成唯一订单号（ORD + yyyyMMddHHmmss + 6位随机数，DuplicatedKeyException 重试）
6. 插入 orders 表（状态 PENDING）
7. 逐项插入 order_item（含图书名和价格快照）
8. 删除对应购物车项
```

### 6.3 库存入库

```
1. 校验图书存在、数量 > 0、成本价 > 0
2. SELECT book.stock FOR UPDATE（悲观锁）
3. 生成唯一批次号（RK + yyyyMMddHHmmssSSS + 6位随机数，重试防重复）
4. 插入 stock_batch（remain_quantity = quantity）
5. 更新 book.stock（stock = stock + quantity）
6. 插入 stock_record（type=IN, before_stock, after_stock, 关联 batch_id）
```

### 6.4 订单发货 — FIFO 出库

详见第七章。

---

## 七、FIFO 出库逻辑（重点）

### 7.1 设计动机

同一图书可能分多个批次入库，每次入库的**成本价可能不同**（供应商差异、时间差异）。出库时采用**先进先出（FIFO）**原则：优先扣减入库时间最早的批次，这样成本核算更合理，也能避免老旧批次长期积压。

### 7.2 执行流程

```
发货方法：AdminOrderServiceImpl.ship(operatorId, orderId)

前置条件：
- 操作人为 ADMIN 角色
- 订单状态为 CONFIRMED（已确认）

对订单中每个 order_item 执行：

1. SELECT book.stock FOR UPDATE          ← 悲观锁，防止并发修改库存
   if stock == null → 抛异常 "图书不存在"
   if stock < quantity → 抛异常 "库存不足"

2. 查询可用批次（按 created_at ASC，remain_quantity > 0）
   SELECT * FROM stock_batch
   WHERE book_id = ? AND remain_quantity > 0
   ORDER BY created_at ASC
   FOR UPDATE                            ← 悲观锁，锁定所有可用批次

3. 遍历批次逐一扣减：
   for each batch (按时间从早到晚):
     if needQuantity <= 0: break         ← 已扣够，停止
     beforeRemain = batch.remainQuantity
     if beforeRemain <= 0: continue      ← 跳过空批次

     deductQuantity = min(beforeRemain, needQuantity)
     afterRemain = beforeRemain - deductQuantity

     UPDATE stock_batch                  ← 乐观锁：WHERE remain_quantity = beforeRemain
     SET remain_quantity = afterRemain
     WHERE id = ? AND remain_quantity = beforeRemain
     if affected_rows != 1 → 抛异常 "批次库存扣减失败"

     INSERT INTO stock_record            ← 记录出库流水
       (type=OUT, quantity=-deductQuantity,
        before_stock=beforeRemain, after_stock=afterRemain,
        order_id=orderId, batch_id=batchId)

     needQuantity -= deductQuantity

4. 校验：if needQuantity > 0 → 抛异常 "库存不足"

5. 更新 book.stock = book.stock - quantity  ← 更新冗余库存

6. UPDATE orders SET status='DELIVERED'   ← 更新订单状态
```

### 7.3 并发安全保证

| 机制 | 说明 |
|------|------|
| `SELECT ... FOR UPDATE`（悲观锁） | 锁定 book 行 + 所有可用 batch 行，阻止并发发货同时操作 |
| `UPDATE ... WHERE remain_quantity = ?`（乐观锁） | 二次校验：如果并发发货已在步骤间修改了 remain_quantity，更新失败回滚 |
| `@Transactional` | 整个发货流程在同一事务中，任何失败全部回滚 |

### 7.4 示例

图书"三体（重庆出版社）"库存 100 本，分两批次入库：

| 批次 | 入库时间 | 入库数量 | 剩余 | 成本价 |
|------|----------|----------|------|--------|
| RK001 | 2026-05-01 | 60 | 60 | 15.00 |
| RK002 | 2026-05-10 | 40 | 40 | 18.00 |

用户下单购买 80 本，发货时：
1. 先从 RK001 扣 60 本（剩余 0）— 成本 15.00 × 60
2. 再从 RK002 扣 20 本（剩余 20）— 成本 18.00 × 20

出库后：RK001 remain=0，RK002 remain=20，book.stock=20。

---

## 八、CORS 跨域解决方案

### 8.1 问题描述

Vue 前端运行在 `http://localhost:5173`，SSM 后端部署在 `http://localhost:8084/book_ssm_war`。浏览器同源策略阻止跨域请求，尤其项目使用 Session + Cookie 登录，需要携带凭证。

### 8.2 方案选择

选择 **Servlet Filter** 而非 Spring MVC `<mvc:cors>` 配置，原因：

- Filter 在所有请求到达 DispatcherServlet **之前**执行，OPTIONS 预检请求不会被 LoginInterceptor 拦截
- 更精确控制 Origin 白名单和 Credentials 设置
- 不依赖 Spring MVC 版本差异

### 8.3 实现要点

```java
// CorsFilter.doFilter() 核心逻辑
String origin = request.getHeader("Origin");
if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
    response.setHeader("Access-Control-Allow-Origin", origin);       // 回显具体 origin
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
}

if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
    response.setStatus(200);
    return;  // 不继续传递，避免后续拦截器处理 OPTIONS
}
```

**关键细节**：当 `Access-Control-Allow-Credentials: true` 时，`Access-Control-Allow-Origin` **不能**为 `*`，必须根据请求的 `Origin` 头回显具体值。这是浏览器 W3C CORS 规范的硬性要求。

---

## 九、系统测试

### 9.1 测试环境

| 组件 | 地址 | 说明 |
|------|------|------|
| 后端 API | `http://localhost:8084/book_ssm_war` | Tomcat 9 |
| 前端 | `http://localhost:5173` | Vite 开发服务器 |
| 数据库 | `localhost:3306/book_inventory` | MySQL 8.x |

### 9.2 测试用例（部分）

**健康检查：**
```
GET http://localhost:8084/book_ssm_war/api/health
→ 200 {"code":200,"message":"success","data":{"status":"UP",...}}
```

**用户注册：**
```
POST /api/auth/register
Body: username=testuser&password=123456
→ 200 {"code":200,"message":"注册成功","data":null}
```

**用户登录：**
```
POST /api/auth/login
Body: username=admin&password=123456
→ 200 {"code":200,"message":"登录成功","data":{"id":1,"username":"admin",...}}
Set-Cookie: JSESSIONID=xxx
```

**CORS 预检：**
```
OPTIONS /api/categories
Origin: http://localhost:5173
→ 200
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Credentials: true
```

**登录拦截：**
```
GET /api/categories (不携带 Cookie)
→ 401 {"code":401,"message":"请先登录","data":null}
```

**图书搜索：**
```
GET /api/books?title=Java&page=1&pageSize=10
→ 200 {"code":200,"data":{"total":2,"list":[...],...}}
```

**创建订单：**
```
POST /api/orders/create
cartItemIds=1,2&receiverName=张三&receiverPhone=13800000001&receiverAddress=北京市
→ 200 返回订单详情，购物车项被清空
```

**FIFO 发货：**
```
PUT /api/admin/orders/{id}/ship
→ 200 订单状态变为 DELIVERED
验证：stock_batch.remain_quantity 按时间顺序递减
验证：stock_record 插入 OUT 记录
验证：book.stock 同步递减
```

### 9.3 已知限制

1. **权限粒度**：图书/分类/库存的 CRUD 接口未在代码层面强制校验 ADMIN 角色（仅依赖 LoginInterceptor 校验登录）。管理员接口通过前端路由守卫限制访问，但后端存在提升空间。
2. **密码安全**：使用单次 MD5 哈希，生产环境应升级为 BCrypt 或 Argon2。
3. **无支付流程**：订单无在线支付环节，确认和发货均由管理员手动操作。
4. **无退货/退款**：不支持订单取消后的库存回退。
5. **无图片上传**：`cover_image` 字段仅支持 URL 字符串输入。
6. **单机部署**：Session 存储于 Tomcat 内存，不支持集群。生产环境可改为 Redis 共享 Session 或 JWT。

---

## 十、总结

本项目完整实现了一个基于传统 SSM 架构的图书进销存管理系统，覆盖了用户端选购下单和后台进销存管理的核心流程。

**架构亮点：**
- 严格的三层分离：Controller → Service → Mapper
- XML 配置驱动的 Spring/MyBatis 整合（非 Spring Boot 自动配置）
- Servlet Filter 解决 CORS 跨域（含 Credentials 支持）
- HandlerInterceptor 实现 Session 登录鉴权

**业务亮点：**
- **FIFO 出库**：悲观锁 + 乐观锁双重并发控制，批次级库存追踪
- **订单价格快照**：下单时锁定书名和价格，避免后续变更影响历史订单
- **批次号/订单号唯一性**：带时间戳前缀 + 随机后缀 + 重试机制
- **库存冗余**：`book.stock` 作为汇总字段，与批次扣减同步更新，查询性能优于 SUM 聚合

**技术栈选择理由：**
选择传统 SSM（非 Spring Boot）的目的是展示经典 Java Web 开发模式：XML 配置、WAR 包部署、Servlet Filter/Interceptor 的层次差异。这种架构在教学场景和遗留系统维护中仍有广泛参考价值。
