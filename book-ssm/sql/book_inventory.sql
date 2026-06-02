-- ============================================================
-- 图书进销存管理系统 - 数据库初始化脚本
-- Version: 1.0
-- MySQL 8.x
-- ============================================================

USE book_inventory;

-- ============================================================
-- 1. 用户表
-- ============================================================
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS stock_record;
DROP TABLE IF EXISTS stock_batch;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL                COMMENT '用户名',
    password    VARCHAR(100) NOT NULL                COMMENT '密码（MD5加密）',
    nickname    VARCHAR(50)  DEFAULT NULL            COMMENT '昵称',
    phone       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
    email       VARCHAR(100) DEFAULT NULL            COMMENT '邮箱',
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员, USER-普通用户',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0-禁用, 1-正常',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- ============================================================
-- 2. 图书分类表
-- ============================================================
CREATE TABLE category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    name        VARCHAR(50)  NOT NULL                COMMENT '分类名称',
    description VARCHAR(200) DEFAULT NULL            COMMENT '分类描述',
    sort_order  INT          DEFAULT 0               COMMENT '排序号',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';


-- ============================================================
-- 3. 图书表
--    支持区分同名不同作者、不同出版社、不同ISBN
--    ISBN 全局唯一
--    (title, author, publisher) 联合唯一作为辅助校验
-- ============================================================
CREATE TABLE book (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '图书ID',
    title       VARCHAR(200)  NOT NULL                COMMENT '书名',
    author      VARCHAR(100)  NOT NULL                COMMENT '作者',
    publisher   VARCHAR(100)  NOT NULL                COMMENT '出版社',
    isbn        VARCHAR(20)   NOT NULL                COMMENT 'ISBN',
    price       DECIMAL(10,2) NOT NULL                COMMENT '售价',
    cost_price  DECIMAL(10,2) NOT NULL                COMMENT '成本价',
    cover_image VARCHAR(300)  DEFAULT NULL            COMMENT '封面图片URL',
    description TEXT          DEFAULT NULL            COMMENT '图书描述',
    category_id BIGINT        NOT NULL                COMMENT '所属分类ID',
    stock       INT           NOT NULL DEFAULT 0      COMMENT '当前库存（冗余，通过批次汇总更新）',
    status      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态：0-下架, 1-在售',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_isbn (isbn),
    KEY idx_category_id (category_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';


-- ============================================================
-- 4. 库存批次表
--    每次入库形成独立批次，记录供应商和进价
-- ============================================================
CREATE TABLE stock_batch (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '批次ID',
    book_id     BIGINT        NOT NULL                COMMENT '图书ID',
    batch_no    VARCHAR(50)   NOT NULL                COMMENT '批次号',
    quantity    INT           NOT NULL                COMMENT '入库数量',
    remain_quantity INT       NOT NULL DEFAULT 0      COMMENT '批次剩余数量',
    cost_price  DECIMAL(10,2) NOT NULL                COMMENT '本次入库成本价',
    supplier    VARCHAR(100)  DEFAULT NULL            COMMENT '供应商',
    operator_id BIGINT        NOT NULL                COMMENT '操作人ID',
    remark      VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_batch_no (batch_no),
    KEY idx_book_id (book_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存批次表';


-- ============================================================
-- 5. 库存流水表
--    记录每一次入库和出库操作（入库/销售出库/退货/盘点调整等）
-- ============================================================
CREATE TABLE stock_record (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    book_id      BIGINT        NOT NULL                COMMENT '图书ID',
    batch_id     BIGINT        DEFAULT NULL            COMMENT '关联批次ID（出库时关联具体批次）',
    type         VARCHAR(20)   NOT NULL                COMMENT '操作类型：IN-入库, OUT-出库, ADJUST-盘点调整',
    quantity     INT           NOT NULL                COMMENT '变动数量（入库正数，出库负数）',
    before_stock INT           NOT NULL DEFAULT 0      COMMENT '变动前库存',
    after_stock  INT           NOT NULL DEFAULT 0      COMMENT '变动后库存',
    order_id     BIGINT        DEFAULT NULL            COMMENT '关联订单ID（销售出库时关联）',
    operator_id  BIGINT        NOT NULL                COMMENT '操作人ID',
    remark       VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_book_id (book_id),
    KEY idx_batch_id (batch_id),
    KEY idx_type (type),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';


-- ============================================================
-- 6. 购物车表
-- ============================================================
CREATE TABLE cart_item (
    id         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
    user_id    BIGINT   NOT NULL                COMMENT '用户ID',
    book_id    BIGINT   NOT NULL                COMMENT '图书ID',
    quantity   INT      NOT NULL DEFAULT 1      COMMENT '数量',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_book (user_id, book_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';


-- ============================================================
-- 7. 订单表
--    状态流转：PENDING -> PAID -> DELIVERED -> COMPLETED
--             PENDING -> CANCELLED
--             PAID    -> CANCELLED（需退款）
-- ============================================================
CREATE TABLE orders (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no         VARCHAR(32)   NOT NULL                COMMENT '订单号',
    user_id          BIGINT        NOT NULL                COMMENT '用户ID',
    total_amount     DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '订单总金额',
    status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '订单状态：PENDING-待支付, PAID-已支付, DELIVERED-已发货, COMPLETED-已完成, CANCELLED-已取消',
    receiver_name    VARCHAR(50)   DEFAULT NULL            COMMENT '收货人姓名',
    receiver_phone   VARCHAR(20)   DEFAULT NULL            COMMENT '收货人电话',
    receiver_address VARCHAR(300)  DEFAULT NULL            COMMENT '收货地址',
    paid_at          DATETIME      DEFAULT NULL            COMMENT '支付时间',
    confirmed_at     DATETIME      DEFAULT NULL            COMMENT '确认时间',
    delivered_at     DATETIME      DEFAULT NULL            COMMENT '发货时间',
    completed_at     DATETIME      DEFAULT NULL            COMMENT '完成时间',
    cancelled_at     DATETIME      DEFAULT NULL            COMMENT '取消时间',
    remark           VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';


-- ============================================================
-- 8. 订单明细表
-- ============================================================
CREATE TABLE order_item (
    id         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    order_id   BIGINT        NOT NULL                COMMENT '订单ID',
    book_id    BIGINT        NOT NULL                COMMENT '图书ID',
    book_title VARCHAR(200)  NOT NULL                COMMENT '图书名称（快照）',
    book_price DECIMAL(10,2) NOT NULL                COMMENT '下单时单价（快照）',
    quantity   INT           NOT NULL                COMMENT '购买数量',
    subtotal   DECIMAL(12,2) NOT NULL                COMMENT '小计',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_book_id (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';


-- ============================================================
-- 测试数据
-- ============================================================

-- 测试用户（密码为 123456 的 MD5 值：e10adc3949ba59abbe56e057f20f883e）
-- 管理员 admin / 123456
INSERT INTO user (username, password, nickname, phone, role) VALUES
('admin',  'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800000001', 'ADMIN'),
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三',      '13800000002', 'USER');

-- 图书分类
INSERT INTO category (name, description, sort_order) VALUES
('计算机科学', '计算机编程、算法、架构等', 1),
('文学小说',   '中外文学、小说、散文等',   2),
('经济管理',   '经济学、管理学、商业等',   3),
('科普读物',   '科普、百科、自然科学等',   4);

-- 测试图书
INSERT INTO book (title, author, publisher, isbn, price, cost_price, category_id, stock, description) VALUES
('Java编程思想',       'Bruce Eckel',    '机械工业出版社', '9787111213826', 108.00, 70.00, 1, 100, 'Java经典入门书籍'),
('深入理解Java虚拟机', '周志明',         '机械工业出版社', '9787111421900', 79.00,  50.00, 1, 80,  'JVM深度解析'),
('三体',               '刘慈欣',         '重庆出版社',     '9787536692930', 23.00,  15.00, 2, 200, '科幻巨作，雨果奖获奖作品'),
('三体',               '刘慈欣',         '猫头鹰出版社',   '9789863590934', 35.00,  22.00, 2, 50,  '三体-台湾繁体版'),
('红楼梦',             '曹雪芹',         '人民文学出版社', '9787020002207', 59.70,  40.00, 2, 60,  '中国古典四大名著之一'),
('红楼梦',             '曹雪芹/高鹗',   '中华书局',       '9787101045635', 48.00,  32.00, 2, 40,  '红楼梦-中华书局版本'),
('经济学原理',         '曼昆',           '北京大学出版社', '9787301259781', 88.00,  60.00, 3, 90,  '经济学入门经典教材'),
('时间简史',           '霍金',           '湖南科学技术出版社', '9787535732309', 45.00, 28.00, 4, 70, '霍金科普经典');

-- 库存批次（模拟历史入库）
INSERT INTO stock_batch (book_id, batch_no, quantity, remain_quantity, cost_price, supplier, operator_id, remark) VALUES
(1, 'RK20260501-001', 100, 100, 70.00, '机械工业出版社直供', 1, '首批入库'),
(2, 'RK20260501-002', 80,  80,  50.00, '机械工业出版社直供', 1, '首批入库'),
(3, 'RK20260501-003', 200, 200, 15.00, '重庆出版社直供',     1, '首批入库'),
(4, 'RK20260501-004', 50,  50,  22.00, '进口书商',           1, '繁体版入库'),
(5, 'RK20260501-005', 60,  60,  40.00, '人民文学出版社直供', 1, '首批入库'),
(6, 'RK20260501-006', 40,  40,  32.00, '中华书局直供',       1, '首批入库'),
(7, 'RK20260501-007', 90,  90,  60.00, '北京大学出版社直供', 1, '首批入库'),
(8, 'RK20260501-008', 70,  70,  28.00, '湖南科技出版社直供', 1, '首批入库');

-- 库存流水（入库记录）
INSERT INTO stock_record (book_id, batch_id, type, quantity, before_stock, after_stock, operator_id, remark) VALUES
(1, 1, 'IN', 100, 0,   100, 1, '首批入库'),
(2, 2, 'IN', 80,  0,   80,  1, '首批入库'),
(3, 3, 'IN', 200, 0,   200, 1, '首批入库'),
(4, 4, 'IN', 50,  0,   50,  1, '首批入库'),
(5, 5, 'IN', 60,  0,   60,  1, '首批入库'),
(6, 6, 'IN', 40,  0,   40,  1, '首批入库'),
(7, 7, 'IN', 90,  0,   90,  1, '首批入库'),
(8, 8, 'IN', 70,  0,   70,  1, '首批入库');
