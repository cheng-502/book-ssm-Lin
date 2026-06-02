 图书进销存管理系统 — 开发者文档

 一、项目概览

图书进销存管理系统，面向中小型书店的 Web 应用，覆盖入库、销售、库存三大环节。采用传统 SSM（Spring 5 + Spring MVC + MyBatis） 三层架构，前端基于 Vue 3 + Element Plus，前后端分离部署。
该项目已经托管到github上：cheng-502/book-ssm-Lin: 一款基于 Spring 5 + Spring MVC + MyBatis（传统 SSM 架构） 的图书进销存管理系统。
https://github.com/cheng-502/book-ssm-Lin
团队通过git实现协作开发

分工：

| 成员 | 角色 | 负责模块 | 具体工作内容 | 涉及文件 |
|---|---|---|---|---|
| 成员一 | 数据库设计与数据层 | 数据库设计 | 1. 需求分析，绘制 E-R 图<br>2. 编写 `book_inventory.sql`（8 张表、索引、测试数据）<br>3. 编写 MyBatis Mapper XML（6 个映射文件，含 resultMap、动态 SQL、多表 LEFT JOIN）<br>4. 编写 Mapper 接口（6 个，含 @Param 注解）<br>5. 编写 `database.md` 数据库设计文档 | `sql/book_inventory.sql`；`resources/mapper/*.xml`；`mapper/*.java`；`database.md` |
| 成员二 | 前端开发与 UI 优化 | 前端页面 | 1. Vue 3 + Vite 项目初始化，配置 Element Plus + Axios + Vue Router<br>2. 开发用户端 3 页（图书浏览/搜索、购物车、我的订单）<br>3. 开发管理端 7 页（首页、分类管理、图书管理、入库、批次、流水、订单管理）<br>4. Axios 封装（拦截器、统一错误处理、URLSearchParams 表单编码）<br>5. 路由守卫（管理员页面权限）<br>6. UI 重设计与优化（协作者分支 `frontend-impeccable-redesign`）<br>7. 编写 `DESIGN.md`、`PRODUCT.md` | `book-ssm-frontend/src/` 全部文件；`book-ssm-frontend/DESIGN.md`；`book-ssm-frontend/PRODUCT.md` |
| 成员三 | 演讲与 API 接口实现 | 演讲与接口 | 1. 设计 REST API 路由表（17 个用户端 + 8 个管理端端点）<br>2. 编写全部 8 个 Controller（Auth、Book、Category、Cart、Order、AdminOrder、Stock、Health）<br>3. 编写统一响应格式 `Result.java` + 分页 `PageResult.java`<br>4. 项目答辩准备：演示流程编排、PPT 制作、现场演示<br>5. 编写 `SSM项目代码审查报告.md` 中的答辩题库<br>6. 项目整体宣讲与技术亮点提炼 | `controller/*.java`；`dto/*.java`；`SSM项目代码审查报告.md` |
| 成员四 | 后端核心架构 | SSM 整合 + Service 层 | 1. SSM 三大配置文件：`applicationContext.xml`、`spring-mvc.xml`、`web.xml`（父子容器）<br>2. `mybatis-config.xml` 全局配置<br>3. 8 个 Entity 实体类设计<br>4. 7 个 Service 接口 + 7 个 ServiceImpl（含核心业务逻辑）<br>5. FIFO 出库实现：悲观锁 + 乐观锁 + `@Transactional` 三层并发控制<br>6. 订单号/批次号唯一性生成（`OrderNoUtil`、`BatchNoUtil`） | `resources/applicationContext.xml`；`resources/spring-mvc.xml`；`webapp/WEB-INF/web.xml`；`service/*.java`；`service/impl/*.java`；`entity/*.java`；`util/OrderNoUtil.java`；`util/BatchNoUtil.java` |
| 成员五 | 后端安全与基础设施 | AOP + 鉴权 + 跨域 | 1. 登录鉴权：`LoginInterceptor` + `SessionUtil`（HttpSession）<br>2. 跨域方案：`CorsFilter`（Servlet Filter，处理 OPTIONS 预检 + Credentials）<br>3. AOP 日志切面：`LogAspect`（@Aspect + @Around，记录 Service 方法耗时）<br>4. 密码加密：`MD5Util`<br>5. JdbcTemplate Bean 配置与演示端点 `/api/health/db`<br>6. 数据库密钥分离：`db-secret.properties` + `.gitignore` | `interceptor/LoginInterceptor.java`；`filter/CorsFilter.java`；`aspect/LogAspect.java`；`util/MD5Util.java`；`util/SessionUtil.java`；`controller/HealthController.java`（db 端点部分） |
| 成员六 | 后端异常处理与文档 | 异常处理 + 文档 + 测试 | 1. 全局异常处理：`GlobalExceptionHandler`（@ControllerAdvice + 统一 JSON 错误响应）<br>2. 自定义业务异常：`BusinessException`<br>3. 编写 `README.md`（根目录 + book-ssm 子目录）<br>4. 编写 `DEVELOPMENT.md`（本文件，含技术架构、核心流程、演示指南）<br>5. 日志配置：`log4j.properties`（mapper 包 DEBUG 级别输出 SQL）<br>6. 数据库测试数据准备与 API 接口验证 | `exception/*.java`；`README.md`；`DEVELOPMENT.md`；`resources/log4j.properties` |

| 维度 | 选型 | 说明 |
|---|---|---|
| 后端框架 | Spring 5.3.30 + Spring MVC + MyBatis 3.5.14 | XML 配置驱动，非 Spring Boot |
| AOP | AspectJ + 自定义 @Aspect 切面 | LogAspect 记录 Service 层方法耗时 |
| 数据库 | MySQL 8.x + Druid 1.2.20 + JdbcTemplate | MyBatis 为 ORM 主体，JdbcTemplate 补充演示 |
| 事务 | DataSourceTransactionManager + @Transactional | 声明式事务，FIFO 出库含悲观锁+乐观锁 |
| 鉴权 | HttpSession + LoginInterceptor | 教学级 Session 鉴权 |
| 跨域 | Servlet Filter（CorsFilter） | 在 DispatcherServlet 前处理 OPTIONS |
| 前端 | Vue 3.5 + Vite 6 + Element Plus 2.9 + Axios | 前后端分离 SPA |
| 部署 | WAR → Tomcat 9 | 端口 8080，Context Path `/book-ssm` |
| 数据库密钥管理 | db-secret.properties 独立文件 | 密钥与配置分离，不入 Git |

---

 二、项目结构

根目录
├── book-ssm/                       后端（SSM + Maven）
│   ├── pom.xml                     依赖管理
│   ├── sql/book_inventory.sql      建库脚本 + 测试数据
│   ├── README.md                   后端详细文档（API 列表）
│   ├── DEVELOPMENT.md              本文件
│   └── src/main/
│       ├── java/com/bookssm/
│       │   ├── controller/         8 个 REST Controller
│       │   ├── service/            7 个接口 + 7 个实现
│       │   ├── mapper/             6 个 MyBatis 接口
│       │   ├── entity/             8 个实体类
│       │   ├── dto/                Result + PageResult
│       │   ├── aspect/             LogAspect（AOP 日志切面）
│       │   ├── exception/          BusinessException + GlobalExceptionHandler
│       │   ├── filter/             CorsFilter
│       │   ├── interceptor/        LoginInterceptor
│       │   └── util/               MD5Util, OrderNoUtil, BatchNoUtil, SessionUtil
│       ├── resources/
│       │   ├── mapper/             6 个 SQL XML 映射文件
│       │   ├── applicationContext.xml   Spring 父容器
│       │   ├── spring-mvc.xml           Spring MVC 子容器
│       │   ├── mybatis-config.xml       MyBatis 全局配置
│       │   ├── jdbc.properties          数据库连接模板
│       │   ├── db-secret.properties     数据库密码（本地，不入 Git）
│       │   └── log4j.properties         日志配置
│       └── webapp/WEB-INF/web.xml       Servlet 配置
│
├── book-ssm-frontend/              前端（Vue 3 + Vite）
│   ├── DESIGN.md                   前端设计文档
│   ├── PRODUCT.md                  产品设计文档
│   └── src/
│       ├── utils/request.js        Axios 封装（拦截器 + URLSearchParams）
│       ├── router/index.js         Vue Router（含导航守卫）
│       └── views/                  用户端 3 页 + 管理端 7 页
│
├── database.md                     数据库设计文档（完整表结构/索引/ER 图）
└── SSM项目代码审查报告.md          实验逐项审查 + 20 道答辩题库
```


 三、技术栈与课程实验对照

 3.1 实验覆盖总览
| 实验 | 课程要求 | 项目实现 | 关键代码位置 |
|---|---|---|---|
| 一：MyBatis 入门 | 核心配置、CRUD映射、日志 | SqlSessionFactoryBean 整合 Spring；6 个 Mapper XML 含完整 CRUD；LOG4J 输出 SQL | `applicationContext.xml:41-51`；`mybatis-config.xml` |
| 二：参数传递 | {} 与 ${}、@Param、模糊查询、主键回填、批量删除 | 全项目 {} 防注入；CONCAT 模糊查询；useGeneratedKeys 回填主键；foreach 批量删除 | `BookMapper.xml:33,79`；`CartMapper.xml:80-87` |
| 三：多表查询 | resultMap、多对一、一对多、延迟加载 | book LEFT JOIN category；orders LEFT JOIN user；mybatis-config 启用延迟加载 | `BookMapper.xml:51-54`；`OrderMapper.xml:73-77` |
| 四：动态 SQL | if/where/foreach/sql/include | 条件分页查询；动态 WHERE；sql 片段复用 | `BookMapper.xml:24-48` |
| 六：AOP | @Aspect、五种通知、切入点表达式 | LogAspect：@Around 记录 Service 层方法调用参数与耗时 | `com.bookssm.aspect.LogAspect` |
| 七：事务+JdbcTemplate | 声明式事务、JdbcTemplate CRUD | @Transactional 覆盖所有写操作；JdbcTemplate 共用 Druid 数据源 | `AdminOrderServiceImpl:63,86-135`；`applicationContext.xml:63-66` |
| 八：SpringMVC | DispatcherServlet、拦截器、@Controller | 父子容器隔离；LoginInterceptor + CorsFilter；@RestController 全 JSON 响应 | `web.xml`；`spring-mvc.xml` |
| 九：Ajax+REST | axios、@RequestBody、JSON交互 | axios 拦截器自动解包 Result；统一 `{code, message, data}` 格式 | `request.js:10-26`；`Result.java` |
| 十：SSM 整合 | 父子容器、三层架构、@Autowired | ContextLoaderListener + DispatcherServlet；Controller→Service→Mapper | `web.xml:45-62`；`applicationContext.xml:21-26` |

1. 架构展示（1 分钟） — 打开 `web.xml` + `applicationContext.xml` + `spring-mvc.xml`，说明父子容器分层。
2. MyBatis 多表 + 动态 SQL（2 分钟） — 打开 `BookMapper.xml`，展示：
- 第 24-28 行：`<sql>` 公共列片段 + LEFT JOIN category
- 第 30-48 行：`<if>` + `<where>` 动态条件
- 第 79 行：`useGeneratedKeys` 主键回填
- 第 33 行：`CONCAT('%', {title}, '%')` 安全模糊查询
3. AOP 日志切面（1 分钟） — 打开 `com.bookssm.aspect.LogAspect`，展示 @Around 环绕通知记录耗时。
4. 声明式事务 + FIFO（3 分钟，重点） — 打开 `AdminOrderServiceImpl.java:86-135`，讲解三层并发控制。
5. 跨域方案（1 分钟） — 打开 `CorsFilter.java`，说明为什么用 Filter 而非 Spring MVC 配置。

 四、核心技术亮点详解

 4.1 FIFO 出库 — 三层并发控制（实验七 / 实验十 体现）

同一图书多批次入库，成本价不同。出库时按入库时间先进先出，从最早批次依次扣减。

代码入口：`AdminOrderServiceImpl.java:86-135` 的 `shipOrderItem()` 方法

三层保护：

| 层级 | 机制 | 代码证据 |
|---|---|---|
| 悲观锁 | `SELECT ... FOR UPDATE` 锁定 book 行和所有可用 batch 行 | `StockMapper.xml:findBookStockForUpdate`、`findAvailableBatchesForUpdate` |
| 乐观锁（CAS） | `UPDATE stock_batch SET remain_quantity=? WHERE id=? AND remain_quantity=?` — 校验 beforeRemain 未被并发修改 | `AdminOrderServiceImpl:111`，affected rows ≠ 1 则回滚 |
| 事务回滚 | `@Transactional` on `ship()` 方法，任何步骤异常则全部撤销 | `AdminOrderServiceImpl:63` |

演示技巧：准备两组数据（两个批次），讲清楚"为什么先扣旧批次"，然后展示 `stock_record` 中的出库流水。

 4.2 Spring 父子容器

ContextLoaderListener（父容器）
  └── applicationContext.xml
        ├── 管理 Service、Mapper、数据源、事务
        └── 排除 @Controller、@ControllerAdvice

DispatcherServlet（子容器）
  └── spring-mvc.xml
        └── 仅扫描 Controller + ExceptionHandler

为什么这样设计？
- 子容器可以访问父容器的 bean（Controller 注入 Service）
- 父容器不能访问子容器的 bean（Service 层看不到 Controller）
- 保证分层隔离，Service 层不依赖 Web 层

 4.3 AOP 日志切面

`com.bookssm.aspect.LogAspect`：
- `@Aspect` + `@Component`，Spring 自动扫描
- `@Pointcut("execution(* com.bookssm.service..*.*(..))")` 切入所有 Service 方法
- `@Around` 环绕通知：记录方法签名、参数数量、执行耗时、异常类型
- 无需修改任何业务代码，横切关注点完全解耦

 4.4 CORS 跨域

为什么用 Servlet Filter 而非 Spring MVC `<mvc:cors>`？

- OPTIONS 预检请求在 Filter 层就返回 200，不经过 DispatcherServlet
- 不会被 LoginInterceptor 拦截（否则 OPTIONS 请求也返回 401）
- `Access-Control-Allow-Origin` 回显具体 Origin（不能用 `*`，因为配置了 `withCredentials: true`）

 4.5 前后端数据交互

```
前端 axios 实例（request.js）
  ├── withCredentials: true       → 携带 Cookie（JSESSIONID）
  ├── URLSearchParams 表单编码    → Content-Type: application/x-www-form-urlencoded
  └── 响应拦截器自动解包          → code == 200 时直接返回 data 字段

后端统一响应格式（Result.java）
  └── { code: 200, message: "success", data: {...} }
```

 4.6 密钥与配置分离

数据库密码存储在 `db-secret.properties`（已加入 `.gitignore`），`jdbc.properties` 中通过 `${db.password}` 占位符引用。`applicationContext.xml` 配置两个 properties 文件先后加载，`ignore-unresolvable="true"` 保证本地开发灵活性。

 4.7 价格快照机制
`order_item` 表的 `book_title` 和 `book_price` 在下单时从 book 表复制，而非外键引用。即使后续图书涨价或改名，历史订单数据完全不受影响。详见 `OrderServiceImpl.java:55-60`。

 五、请求处理全链路
浏览器（localhost:5173）
  │  axios.post('/api/orders/create', form(data))
  │  withCredentials: true → Cookie: JSESSIONID=xxx
  ▼
Tomcat 9（localhost:8080/book-ssm）
  │
  ├── CorsFilter.doFilter()
  │     └── 添加 CORS 头；OPTIONS 直接返回 200
  ├── CharacterEncodingFilter（UTF-8）
  ├── DispatcherServlet
  │     ├── LoginInterceptor.preHandle()
  │     │     └── 从 HttpSession 读取 loginUser，null → 401
  │     ├── Controller.method()
  │     │     └── @RestController → 返回值自动序列化为 JSON
  │     ├── Service.method()        ← @Transactional 事务边界
  │     │     └── LogAspect.around() ← AOP 记录耗时
  │     ├── Mapper.method()
  │     │     └── MyBatis → Mapper XML → SQL → MySQL
  │     └── GlobalExceptionHandler  ← 异常转 {code, message}
  │
  ▼  JSON 响应 → 前端 axios 拦截器 → 自动解包 body.data
```

---

 六、数据库设计摘要
 

8 张 InnoDB 表
 

 七、快速演示流程（5 分钟）

| 步骤 | 操作 | 展示要点 |
|---|---|---|
| 1 | 访问 `/api/health/db` | 展示 JdbcTemplate 查询 MySQL 版本号 |
| 2 | 用 admin 登录 | Session 鉴权，查看 Set-Cookie 响应头 |
| 3 | 搜索图书 `GET /api/books?title=三体` | 同名不同出版社的同名图书；动态 SQL 查询 |
| 4 | 管理员入库 `POST /api/stocks/in` | 生成批次号 + stock_record 流水记录 |
| 5 | 用户下单 `POST /api/orders/create` | 价格快照 + 购物车清空 |
| 6 | 管理员发货 `PUT /api/admin/orders/{id}/ship` | FIFO 出库——查看 stock_batch.remain_quantity 递减顺序 |
| 7 | 打开 Tomcat 日志 | 展示 LogAspect 输出：`>>> BookServiceImpl.list(6 args) 开始 ... 结束，耗时 23 ms` |
| 8 | 访问受保护接口（不带 Cookie） | 展示 LoginInterceptor 返回 401 |

 八、测试账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | admin | 123456 |
| 普通用户 | zhangsan | 123456 |

