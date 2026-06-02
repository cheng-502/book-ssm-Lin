# 图书进销存管理系统 — 数据库设计文档

---

## 一、数据库概述

- **数据库名**：`book_inventory`
- **字符集**：`utf8mb4`
- **引擎**：`InnoDB`
- **数据库版本**：MySQL 8.x

---

## 二、ER 关系

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

---

## 三、表结构定义

### 3.1 user — 用户表

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码（MD5 加密） |
| nickname | VARCHAR(50) | DEFAULT NULL | 昵称 |
| phone | VARCHAR(20) | DEFAULT NULL | 手机号 |
| email | VARCHAR(100) | DEFAULT NULL | 邮箱 |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'USER' | 角色：ADMIN-管理员, USER-普通用户 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：0-禁用, 1-正常 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

### 3.2 category — 图书分类表

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 分类ID |
| name | VARCHAR(50) | NOT NULL, UNIQUE | 分类名称 |
| description | VARCHAR(200) | DEFAULT NULL | 分类描述 |
| sort_order | INT | DEFAULT 0 | 排序号 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 3.3 book — 图书表

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 图书ID |
| title | VARCHAR(200) | NOT NULL | 书名 |
| author | VARCHAR(100) | NOT NULL | 作者 |
| publisher | VARCHAR(100) | NOT NULL | 出版社 |
| isbn | VARCHAR(20) | NOT NULL, UNIQUE | ISBN |
| price | DECIMAL(10,2) | NOT NULL | 售价 |
| cost_price | DECIMAL(10,2) | NOT NULL | 成本价 |
| cover_image | VARCHAR(300) | DEFAULT NULL | 封面图片URL |
| description | TEXT | DEFAULT NULL | 图书描述 |
| category_id | BIGINT | NOT NULL, FK → category.id | 所属分类ID |
| stock | INT | NOT NULL, DEFAULT 0 | 当前库存（冗余字段，通过批次汇总更新） |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：0-下架, 1-在售 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

### 3.4 stock_batch — 库存批次表

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 批次ID |
| book_id | BIGINT | NOT NULL, FK → book.id | 图书ID |
| batch_no | VARCHAR(50) | NOT NULL, UNIQUE | 批次号（格式：RK + yyyyMMddHHmmssSSS + 6位随机数） |
| quantity | INT | NOT NULL | 入库数量 |
| remain_quantity | INT | NOT NULL, DEFAULT 0 | 批次剩余数量 |
| cost_price | DECIMAL(10,2) | NOT NULL | 本次入库成本价 |
| supplier | VARCHAR(100) | DEFAULT NULL | 供应商 |
| operator_id | BIGINT | NOT NULL | 操作人ID |
| remark | VARCHAR(500) | DEFAULT NULL | 备注 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 入库时间 |

### 3.5 stock_record — 库存流水表

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 流水ID |
| book_id | BIGINT | NOT NULL, FK → book.id | 图书ID |
| batch_id | BIGINT | DEFAULT NULL, FK → stock_batch.id | 关联批次ID |
| type | VARCHAR(20) | NOT NULL | 操作类型：IN-入库, OUT-出库, ADJUST-盘点调整 |
| quantity | INT | NOT NULL | 变动数量（入库正数，出库负数） |
| before_stock | INT | NOT NULL, DEFAULT 0 | 变动前库存（批次级） |
| after_stock | INT | NOT NULL, DEFAULT 0 | 变动后库存（批次级） |
| order_id | BIGINT | DEFAULT NULL, FK → orders.id | 关联订单ID（销售出库时关联） |
| operator_id | BIGINT | NOT NULL | 操作人ID |
| remark | VARCHAR(500) | DEFAULT NULL | 备注 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 操作时间 |

### 3.6 cart_item — 购物车表

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 购物车项ID |
| user_id | BIGINT | NOT NULL, FK → user.id | 用户ID |
| book_id | BIGINT | NOT NULL, FK → book.id | 图书ID |
| quantity | INT | NOT NULL, DEFAULT 1 | 数量 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 加入时间 |

- 联合唯一约束：`UNIQUE KEY uk_user_book (user_id, book_id)`

### 3.7 orders — 订单表

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 订单ID |
| order_no | VARCHAR(32) | NOT NULL, UNIQUE | 订单号（格式：ORD + yyyyMMddHHmmss + 6位随机数） |
| user_id | BIGINT | NOT NULL, FK → user.id | 用户ID |
| total_amount | DECIMAL(12,2) | NOT NULL, DEFAULT 0.00 | 订单总金额 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | 订单状态 |
| receiver_name | VARCHAR(50) | DEFAULT NULL | 收货人姓名 |
| receiver_phone | VARCHAR(20) | DEFAULT NULL | 收货人电话 |
| receiver_address | VARCHAR(300) | DEFAULT NULL | 收货地址 |
| paid_at | DATETIME | DEFAULT NULL | 支付时间 |
| confirmed_at | DATETIME | DEFAULT NULL | 确认时间 |
| delivered_at | DATETIME | DEFAULT NULL | 发货时间 |
| completed_at | DATETIME | DEFAULT NULL | 完成时间 |
| cancelled_at | DATETIME | DEFAULT NULL | 取消时间 |
| remark | VARCHAR(500) | DEFAULT NULL | 备注 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**订单状态流转**：`PENDING → CONFIRMED → DELIVERED → COMPLETED`，任意状态可转 `CANCELLED`

### 3.8 order_item — 订单明细表

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | 明细ID |
| order_id | BIGINT | NOT NULL, FK → orders.id | 订单ID |
| book_id | BIGINT | NOT NULL, FK → book.id | 图书ID |
| book_title | VARCHAR(200) | NOT NULL | 下单时图书名称（快照） |
| book_price | DECIMAL(10,2) | NOT NULL | 下单时单价（快照） |
| quantity | INT | NOT NULL | 购买数量 |
| subtotal | DECIMAL(12,2) | NOT NULL | 小计（book_price × quantity） |

---

## 四、索引策略

### 主键索引
所有表的 `id` 均为 PRIMARY KEY。

### 唯一索引

| 表 | 唯一键 | 列 |
|---|---|---|
| user | uk_username | username |
| category | uk_name | name |
| book | uk_isbn | isbn |
| stock_batch | uk_batch_no | batch_no |
| cart_item | uk_user_book | (user_id, book_id) |
| orders | uk_order_no | order_no |

### 普通索引

| 表 | 索引名 | 列 | 用途 |
|---|---|---|---|
| book | idx_category_id | category_id | 按分类查询图书 |
| book | idx_status | status | 按上架状态筛选 |
| stock_batch | idx_book_id | book_id | 按图书查批次 |
| stock_batch | idx_created_at | created_at | FIFO 出库按时间排序 |
| stock_record | idx_book_id | book_id | 按图书查流水 |
| stock_record | idx_batch_id | batch_id | 按批次查流水 |
| stock_record | idx_type | type | 按 IN/OUT 筛选 |
| stock_record | idx_created_at | created_at | 按时间查流水 |
| cart_item | idx_user_id | user_id | 按用户查购物车 |
| orders | idx_user_id | user_id | 按用户查订单 |
| orders | idx_status | status | 按状态筛选订单 |
| orders | idx_created_at | created_at | 按时间排序 |
| order_item | idx_order_id | order_id | 按订单查明细 |
| order_item | idx_book_id | book_id | 按图书查明细 |

---

## 五、核心设计说明

### 5.1 库存设计

- **book.stock** 是冗余汇总字段，每次入库/出库同步更新，避免频繁 SUM 聚合
- **stock_batch** 每批次独立记录成本价、剩余数量及供应商，支持 FIFO 出库
- **stock_record** 记录每次库存变动的完整快照（变动前/后数量），用于审计追溯

### 5.2 FIFO 出库机制

出库时按 `stock_batch.created_at ASC` 查询可用批次（`remain_quantity > 0`），从最早批次依次扣减。并发安全由双层锁保证：

| 机制 | 说明 |
|---|---|
| `SELECT ... FOR UPDATE`（悲观锁） | 锁定 book 行 + 所有可用 batch 行 |
| `UPDATE ... WHERE remain_quantity = ?`（乐观锁） | 二次校验，并发冲突时回滚 |
| `@Transactional` | 整个发货流程在同一事务中 |

### 5.3 价格快照

`order_item.book_title` 和 `order_item.book_price` 存储下单时的书名和价格。即使后续图书信息变更（如涨价、改名），历史订单数据不受影响。

### 5.4 唯一标识生成

- **订单号**：`ORD + yyyyMMddHHmmss + 6位随机数`，遇 DuplicatedKeyException 重试
- **批次号**：`RK + yyyyMMddHHmmssSSS + 6位随机数`，遇 DuplicatedKeyException 重试

---

## 六、建表脚本位置

完整 SQL 脚本：`book-ssm/sql/book_inventory.sql`

---

## 七、测试数据

| 表 | 初始数据量 |
|---|---|
| user | 2 条（admin / zhangsan） |
| category | 4 条（计算机科学、文学小说、经济管理、科普读物） |
| book | 8 条（含同名不同出版社的"三体"和"红楼梦"） |
| stock_batch | 8 条（每本书一个初始批次） |
| stock_record | 8 条（入库流水记录） |

测试账号密码均为 `123456`（MD5: `e10adc3949ba59abbe56e057f20f883e`）。
