# SSM 项目综合代码审查报告

> 审查对象：图书进销存管理系统（book-ssm + book-ssm-frontend）
> 审查日期：2026-06-03
> 审查依据：JavaEE 课程实验一 ~ 实验十知识点要求

---

## 一、审查总览

### 1.1 项目基本信息

| 项目 | 说明 |
|---|---|
| 项目名称 | 图书进销存管理系统 |
| 后端架构 | Spring 5.3.30 + Spring MVC + MyBatis 3.5.14 |
| 前端架构 | Vue 3.5 + Vite 6 + Element Plus 2.9 + Axios |
| 数据库 | MySQL 8.x + Druid 1.2.20 连接池 |
| 部署方式 | WAR 包 → Tomcat 9 |
| Java 版本 | 1.8（源码目标），IDE 运行 JDK 22 |
| 源码文件数 | 38 个 Java 文件 + 6 个 Mapper XML + 6 个配置文件 |
| 测试文件 | **无（src/test 为空目录）** |

### 1.2 综合评分矩阵

| 实验 | 主题 | 完成度 | 评级 |
|---|---|---|---|
| 实验一 | MyBatis 入门操作 | 全面覆盖 | ✅ 通过 |
| 实验二 | 参数传递及特殊 SQL | 基本覆盖，缺个别项目 | ⚠️ 基本通过 |
| 实验三 | MyBatis 多表查询 | 基本覆盖，缺一对多映射 | ⚠️ 基本通过 |
| 实验四 | MyBatis 动态 SQL | 覆盖基础标签，缺 trim/choose/set | ⚠️ 基本通过 |
| 实验六 | AOP（基于注解） | 仅配置，无切面实现 | ❌ 严重不足 |
| 实验七 | JdbcTemplate 及声明式事务 | 事务完整，JdbcTemplate 缺失 | ❌ 部分缺失 |
| 实验八 | SpringMVC 环境搭建 | 全面覆盖 | ✅ 通过 |
| 实验九 | Ajax 与 RESTful | 功能可用但 RESTful 不规范 | ❌ 部分不足 |
| 实验十 | SSM 整合 | 全面覆盖 | ✅ 通过 |

---

## 二、实验逐项审查

### 2.1 实验一：MyBatis 入门操作 — ✅ 通过

| 检查项 | 状态 | 代码证据 |
|---|---|---|
| Maven依赖（mybatis, mysql-connector） | ✅ | `pom.xml`: mybatis 3.5.14, mysql-connector 8.0.33 |
| 核心配置文件 mybatis-config.xml | ✅ | `src/main/resources/mybatis-config.xml`，含驼峰映射、延迟加载、LOG4J |
| 数据源配置 | ✅ | `applicationContext.xml:29-38`，DruidDataSource |
| Mapper 映射文件 CRUD 标签 | ✅ | 6个XML含 select/insert/update/delete，如 `BookMapper.xml:50-107` |
| SqlSessionFactory/SqlSession | ✅ | Spring整合方式：`applicationContext.xml:41-45` SqlSessionFactoryBean + `48-51` MapperScannerConfigurer |
| 日志配置 | ✅ | `log4j.properties`，mapper 包 DEBUG 级别输出 SQL |
| 属性文件引入 | ✅ | `applicationContext.xml:18` 加载 `jdbc.properties` |

**评价**：MyBatis 基础配置完善。使用 Spring 整合方式管理 SqlSession 比手动 open/commit/close 更规范，事务由 Spring 统一管理。各 Mapper XML 的 namespace 与接口全限定名一一对应。

---

### 2.2 实验二：参数传递及特殊 SQL — ⚠️ 基本通过

| 检查项 | 状态 | 代码证据 |
|---|---|---|
| #{} 预编译占位符 | ✅ | 所有 XML 广泛使用 `#{param}` |
| @Param 注解 | ✅ | 所有 Mapper 接口方法参数均标注，如 `BookMapper.java` |
| 五种参数传递方式 | ✅ | 单参数、@Param多参数、对象参数均有使用 |
| 返回结果封装 | ✅ | 单个实体、List<实体>、long、通过 Result/PageResult 统一包装 |
| 模糊查询（concat） | ✅ | `BookMapper.xml:33` `LIKE CONCAT('%', #{title}, '%')`，安全方式 |
| 自增主键回填 | ✅ | `BookMapper.xml:79` `useGeneratedKeys="true" keyProperty="id"`，所有 insert 均配置 |
| 批量删除（foreach） | ✅ | `CartMapper.xml:80-87` `deleteByIdsAndUser` 使用 `<foreach>` 遍历 ids |
| **${} 字符串拼接** | ❌ | **全项目未使用 `${}`**，无动态表名/动态排序场景演示 |

**问题 2.1**：缺少 `${}` 使用演示。虽然安全，但实验要求理解两者区别及适用场景。

> **修复建议**：在 `BookMapper.xml` 增加一个动态排序查询，对排序字段做白名单校验后使用 `${sortColumn} ${sortDir}`。演示"先校验再拼接"的安全使用模式。

---

### 2.3 实验三：MyBatis 多表查询 — ⚠️ 基本通过

| 检查项 | 状态 | 代码证据 |
|---|---|---|
| 字段名与属性名不一致处理 | ✅ | `mybatis-config.xml` 启用 `mapUnderscoreToCamelCase` |
| resultMap 定义 | ✅ | 6 个 mapper XML 均定义 resultMap，字段映射完整 |
| 多对一映射（LEFT JOIN） | ✅ | `BookMapper.xml:51-54` book LEFT JOIN category；`OrderMapper.xml:73-77` orders LEFT JOIN user |
| SQL 别名方式 | ✅ | `CartMapper.xml:22` `b.title AS book_title` 等 |
| **一对多映射（collection）** | ❌ | **全项目未使用 `<collection>`** |
| **分步查询（select+column）** | ❌ | 无 association/collection 的 select 属性使用 |
| 延迟加载配置 | ✅ | `mybatis-config.xml` 已启用 `lazyLoadingEnabled=true` |

**问题 3.1（关键）**：`Order.java:30` 定义了 `List<OrderItem> items` 属性，但 `OrderMapper.xml` 的 `orderMap` 未使用 `<collection>` 映射。当前通过应用层手动循环填充：`AdminOrderServiceImpl.java:159-163` 和 `OrderServiceImpl.java:155-159` 的 `fillItems()` 方法分别查询。这种方式需要 N+1 次数据库查询。

> **修复建议**：在 `OrderMapper.xml` 的 `orderMap` 中添加：
> ```xml
> <collection property="items" ofType="com.bookssm.entity.OrderItem"
>             column="id" select="findItemsByOrderId" fetchType="lazy"/>
> ```
> 然后删除 Service 层的手动 `fillItems()` 方法。延迟加载已在 `mybatis-config.xml` 开启，会自动生效。

**问题 3.2**：未使用分步查询（`association` + `select`）。

> **修复建议**：可将 BookMapper 的 `bookMap` 中 `categoryName` 的获取改为分步查询方式演示：添加 `<association property="category" column="category_id" select="com.bookssm.mapper.CategoryMapper.findById"/>`。

---

### 2.4 实验四：MyBatis 动态 SQL — ⚠️ 基本通过

| 检查项 | 状态 | 代码证据 |
|---|---|---|
| `<if>` | ✅ | `BookMapper.xml:32-46` 多条件判断；`OrderMapper.xml:90-93` 等 |
| `<where>` | ✅ | `BookMapper.xml:31`；`OrderMapper.xml:110,124`；`StockMapper.xml:117,130,141,157` |
| `<foreach>` | ✅ | `CartMapper.xml:56-58` IN 查询；`:83-85` 批量删除 |
| `<sql>` + `<include>` | ✅ | `BookMapper.xml:24-28` 定义 + `:51,70` 引用；所有 mapper 均使用此模式 |
| **`<trim>`** | ❌ | 无任何使用 |
| **`<choose>`/`<when>`/`<otherwise>`** | ❌ | 无任何使用 |
| **`<set>`** | ❌ | `BookMapper.xml:90-102` update 使用硬编码 `SET`，未使用 `<set>` 动态生成 |

**问题 4.1**：`BookMapper.xml:90-102` 的 update 语句直接硬编码 `SET field1 = #{v1}, field2 = #{v2}, ...`，所有字段都会被更新（包括未修改的）。应使用 `<set>` 标签实现动态更新。

> **修复建议**：将 update 改写为 `<set>` + `<if>` 模式，仅更新非空字段：
> ```xml
> <update id="update">
>     UPDATE book
>     <set>
>         <if test="title != null">title = #{title},</if>
>         <if test="author != null">author = #{author},</if>
>         ...
>     </set>
>     WHERE id = #{id}
> </update>
> ```

**问题 4.2**：缺少 `<trim>` 和 `<choose>/<when>/<otherwise>` 演示。

> **修复建议**：
> - 将 `bookCondition` 的 `<where>` 改写为 `<trim prefix="WHERE" prefixOverrides="AND|OR">` 作为 trim 演示
> - 在订单查询中增加排序逻辑：根据 status 参数选择不同排序字段（PENDING 按创建时间、CONFIRMED 按确认时间、其他按更新时间），使用 `<choose>/<when>/<otherwise>`

---

### 2.5 实验六：AOP（基于注解） — ❌ 严重不足

| 检查项 | 状态 | 代码证据 |
|---|---|---|
| spring-aop 依赖 | ✅ | `pom.xml` 含 `spring-aop 5.3.30` |
| aspectjweaver 依赖 | ✅ | `pom.xml` 含 `aspectjweaver 1.9.20` |
| `<aop:aspectj-autoproxy/>` | ✅ | `applicationContext.xml:62` |
| **@Aspect 切面类** | ❌ | **全项目 38 个 Java 文件中，零个带 @Aspect 注解** |
| **五种通知类型** | ❌ | 无 @Before / @After / @AfterReturning / @AfterThrowing / @Around |
| **@Pointcut** | ❌ | 无切入点定义 |

**问题 5.1（最严重）**：AOP 基础设施完全就绪（依赖 + 配置），但未编写任何切面类。这在答辩中极易被追问。

> **修复建议（三选一，推荐全部实现）**：
>
> **方案一：日志切面（最简单，10 分钟）**
> ```java
> package com.bookssm.aspect;
>
> import org.aspectj.lang.ProceedingJoinPoint;
> import org.aspectj.lang.annotation.*;
> import org.slf4j.Logger;
> import org.slf4j.LoggerFactory;
> import org.springframework.stereotype.Component;
>
> @Aspect
> @Component
> public class LogAspect {
>     private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
>
>     @Pointcut("execution(* com.bookssm.service..*.*(..))")
>     public void serviceLayer() {}
>
>     @Around("serviceLayer()")
>     public Object around(ProceedingJoinPoint pjp) throws Throwable {
>         long start = System.currentTimeMillis();
>         String method = pjp.getSignature().toShortString();
>         log.info(">>> {} 开始", method);
>         Object result = pjp.proceed();
>         log.info("<<< {} 结束，耗时 {} ms", method, System.currentTimeMillis() - start);
>         return result;
>     }
> }
> ```
>
> **方案二：权限切面（实用性强，答辩加分）**
> 创建 `@RequireAdmin` 自定义注解 + 切面，用 `@Around` 拦截 Controller 方法，自动校验 Session 中用户的 role。
>
> **方案三：异常监控切面** — 用 `@AfterThrowing` 统一记录异常日志。

---

### 2.6 实验七：JdbcTemplate 及声明式事务 — ❌ 部分缺失

| 检查项 | 状态 | 代码证据 |
|---|---|---|
| DataSource 配置 | ✅ | `applicationContext.xml:29-38` DruidDataSource |
| DataSourceTransactionManager | ✅ | `applicationContext.xml:54-56` |
| `<tx:annotation-driven>` | ✅ | `applicationContext.xml:59` |
| `@Transactional` 使用 | ✅ | 6 个 ServiceImpl 的写操作方法均标注，如 `BookServiceImpl:31` |
| 事务回滚机制 | ✅ | FIFO 出库演示了异常时全回滚（`AdminOrderServiceImpl:86-135`） |
| **JdbcTemplate Bean 配置** | ❌ | **applicationContext.xml 中无 JdbcTemplate bean 定义** |
| **JdbcTemplate 实际使用** | ❌ | **全项目无 JdbcTemplate 导入或调用** |

**问题 6.1**：`spring-jdbc` 依赖已引入（`pom.xml`），但未配置 JdbcTemplate bean 且未在任何位置使用。实验七明确要求 JdbcTemplate CRUD 演示。

> **修复建议**：
> 1. 在 `applicationContext.xml` 添加：`<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate"><property name="dataSource" ref="dataSource"/></bean>`
> 2. 在 `StockServiceImpl`（或其他 Service）中注入 `@Autowired JdbcTemplate jdbcTemplate`
> 3. 使用 JdbcTemplate 执行一条查询演示：`jdbcTemplate.queryForObject("SELECT VERSION()", String.class)` 或 `jdbcTemplate.queryForList("SELECT COUNT(*) FROM book")`

**评价**：声明式事务部分实现优秀——`@Transactional` 覆盖所有写操作，FIFO 出库的悲观锁+乐观锁+事务回滚展示了对事务原子性的深入理解。

---

### 2.7 实验八：SpringMVC 环境搭建 — ✅ 通过

| 检查项 | 状态 | 代码证据 |
|---|---|---|
| Maven Web 工程目录结构 | ✅ | 标准 `src/main/java`、`src/main/resources`、`src/main/webapp/WEB-INF` |
| web.xml DispatcherServlet | ✅ | `web.xml:50-62`，url-pattern `/`，load-on-startup=1 |
| CharacterEncodingFilter | ✅ | `web.xml:27-42`，UTF-8，forceEncoding=true |
| ContextLoaderListener | ✅ | `web.xml:45-47` |
| springmvc.xml 注解驱动 | ✅ | `spring-mvc.xml` `<mvc:annotation-driven>` |
| 静态资源处理 | ✅ | `spring-mvc.xml` `<mvc:default-servlet-handler/>` |
| @Controller / @RestController | ✅ | 8 个 Controller 均用 `@RestController` |
| @RequestMapping | ✅ | 所有 Controller 类级别配置路径前缀 |
| @PathVariable | ✅ | `OrderController.java`、`AdminOrderController.java`、`StockController.java` 共 5 处 |
| @RequestParam | ✅ | 所有请求参数通过 @RequestParam 接收 |
| GET/POST/PUT 方法 | ✅ | `@GetMapping` / `@PostMapping` / `@PutMapping` |
| **@DeleteMapping** | ❌ | **全项目无**，删除操作用 `POST /xxx/delete` |
| Tomcat 部署 | ✅ | WAR 包 → Tomcat 9 webapps，Context Path `/book_ssm_war` |

**问题 7.1**：未使用 `@DeleteMapping`。所有删除操作（图书删除、分类删除、购物车删除）使用 `POST /xxx/delete`，不符合 HTTP 方法语义。

> **修复建议**：将删除接口改为 `@DeleteMapping("/{id}")` + `@PathVariable`，如：
> ```java
> @DeleteMapping("/{id}")
> public Result<?> delete(@PathVariable("id") Long id) { ... }
> ```

**评价**：SpringMVC 整体配置规范。Spring 父子容器隔离正确（父容器排除 Controller，子容器仅扫描 Controller）。拦截器配置合理，CORS Filter 在 DispatcherServlet 之前处理 OPTIONS 预检是一个设计亮点。

---

### 2.8 实验九：Ajax 与 RESTful — ❌ 部分不足

| 检查项 | 状态 | 代码证据 |
|---|---|---|
| axios 异步请求 | ✅ | `request.js:4-9` 创建 axios 实例，baseURL + withCredentials |
| 请求/响应拦截器 | ✅ | `request.js:10-26` 统一解包 Result + 错误弹窗 |
| @RestController 返回 JSON | ✅ | 所有 Controller 返回 `Result<T>` JSON |
| 统一响应格式 | ✅ | `Result.java` 统一 `{code, message, data}` 结构 |
| 前后端数据交互 | ✅ | 前端 Element Plus 表单 → URLSearchParams → Controller @RequestParam |
| **@RequestBody 接收 JSON** | ❌ | **全项目无**，所有后端接收参数用 @RequestParam |
| **前端发送 JSON body** | ❌ | 前端 `form()` 函数转为 URLSearchParams（表单格式），无 JSON body 请求 |
| **RESTful URL 规范** | ❌ | 图书 CRUD 使用 `POST /api/books/create` 而非 `POST /api/books`；删除用 `POST /api/books/delete` 而非 `DELETE /api/books/{id}` |

**问题 8.1（关键）**：完全未使用 `@RequestBody`。实验九明确要求对比"普通表单提交"与"JSON 提交（@RequestBody）"的区别，目前只有表单方式，缺少 JSON 方式演示。

> **修复建议**：
> 1. 新建 `CreateOrderRequest` DTO 类，包含 cartItemIds 数组和收货信息
> 2. 在 `OrderController` 新增一个接收 JSON 的创建订单端点：
> ```java
> @PostMapping("/create-json")
> public Result<Order> createFromJson(@RequestBody CreateOrderRequest req, HttpSession session) { ... }
> ```
> 3. 前端新增一个调用，使用 `Content-Type: application/json` 发送：
> ```js
> request.post('/api/orders/create-json', jsonData, {
>     headers: { 'Content-Type': 'application/json' }
> })
> ```

**问题 8.2**：RESTful URL 设计不规范。图书/分类的 CRUD 将操作动词放在 URL 路径中（`/create`、`/update`、`/delete`），应改用 HTTP 方法区分操作。

> **修复建议**：
> | 当前 | 应改为 | HTTP 方法 |
> |---|---|---|
> | `POST /api/books/create` | `POST /api/books` | POST（创建资源） |
> | `POST /api/books/update` | `PUT /api/books/{id}` | PUT（全量更新） |
> | `POST /api/books/delete` | `DELETE /api/books/{id}` | DELETE（删除） |
> | `POST /api/books/on-sale` | `PATCH /api/books/{id}/status` | PATCH（部分更新） |

---

### 2.9 实验十：SSM 整合 — ✅ 通过

| 检查项 | 状态 | 代码证据 |
|---|---|---|
| Spring 父容器（ContextLoaderListener） | ✅ | `web.xml:45-47` 加载 `applicationContext.xml` |
| SpringMVC 子容器（DispatcherServlet） | ✅ | `web.xml:50-58` 加载 `spring-mvc.xml` |
| 父容器排除 Controller | ✅ | `applicationContext.xml:21-26` 排除 @Controller/@ControllerAdvice |
| 子容器仅扫描 Controller | ✅ | `spring-mvc.xml:15-16` 仅扫描 controller 和 exception |
| 三层架构 | ✅ | Controller → Service(接口+实现类) → Mapper(接口+XML) |
| @Autowired 注入 | ✅ | 所有依赖通过 @Autowired 字段注入 |
| 单表完整 CRUD | ✅ | Category、Book 均有完整的前后端 CRUD 页面 |
| 中文乱码处理 | ✅ | CharacterEncodingFilter + jdbc.properties UTF-8 + JSON converter UTF-8 |
| 版本兼容 | ✅ | Spring 5.3.30 + Servlet 3.1 + JDK 8 目标，兼容 Tomcat 9 |

**评价**：SSM 整合是项目最成熟的部分。父子容器的正确配置体现了对 Spring 架构的良好理解。FIFO 出库中事务 + 悲观锁 + 乐观锁的组合使用是本次审查中最出彩的实现。

---

## 三、跨实验共性问题

### 3.1 密码安全（安全）

`UserServiceImpl.java:36` 使用 `MD5Util.md5(password)` 单次 MD5 哈希，**无盐值**。彩虹表可在秒级破解简单密码（如 `123456` → `e10adc3949ba59abbe56e057f20f883e`）。

> **修复**：引入 BCrypt（Spring Security 提供 `BCryptPasswordEncoder`）或在 MD5 基础上加随机盐：`MD5(password + salt)`。

### 3.2 实体类日期类型不一致（代码质量）

`User.java` 使用 `java.time.LocalDateTime`，而 `Book.java`、`Order.java` 等其余 7 个实体使用 `java.util.Date`。

> **修复**：统一为 `LocalDateTime`（不可变、线程安全、精度更高），并确保 MyBatis 能正确处理。

### 3.3 管理员权限检查不完整（安全）

仅 `AdminOrderServiceImpl.java:166-173` 在服务层手动校验管理员角色。`BookController`、`CategoryController`、`StockController` 的写操作**未做任何 ADMIN 角色校验**——任何已登录用户都可以通过直接调用 API 来增删改图书/分类/库存。

> **修复**：创建 `@RequireAdmin` 自定义注解 + AOP 切面，统一拦截所有管理端 Controller 方法。或至少在 Controller 方法中添加 `SessionUtil.getLoginUser()` 的角色检查。

### 3.4 单元测试完全缺失（质量保证）

`src/test/java/com/bookssm/` 和 `src/test/resources/` 均为**空目录**。`pom.xml` 中未引入 JUnit 或任何测试框架依赖。

> **修复**：至少添加 Spring 整合测试，对 UserService 的 register/login 和 BookService 的 CRUD 编写测试用例。

### 3.5 异常信息泄露（安全）

`GlobalExceptionHandler.java:29-33` 将根异常类名和消息直接返回给前端（如 `[DataIntegrityViolationException] Duplicate entry 'xxx' for key 'uk_isbn'`），可能暴露数据库结构和字段名。

> **修复**：对非 `BusinessException` 的未知异常，统一返回"系统繁忙，请稍后重试"，详细错误仅记录到日志。

---

## 四、修复优先级路线图

### P0 — 直接影响实验评分，必须修复

| 序号 | 实验 | 修复内容 | 预估工时 |
|---|---|---|---|
| 1 | 实验六 | 创建 `LogAspect` + `@Aspect` 日志切面 | 30 分钟 |
| 2 | 实验七 | 配置 JdbcTemplate Bean + 演示查询 | 20 分钟 |
| 3 | 实验九 | 增加 `@RequestBody` 接收 JSON 演示（新建 DTO + Controller 方法） | 30 分钟 |
| 4 | 实验四 | 补充 `<trim>` / `<choose>` / `<set>` 标签演示 | 30 分钟 |

### P1 — 影响答辩评价，建议修复

| 序号 | 实验 | 修复内容 | 预估工时 |
|---|---|---|---|
| 5 | 实验三 | OrderMapper 增加 `<collection>` 一对多映射 | 20 分钟 |
| 6 | 实验三 | 增加 `association` + `select` 分步查询演示 | 20 分钟 |
| 7 | 实验二 | 增加 `${}` 动态排序演示（含白名单校验） | 20 分钟 |
| 8 | 密码安全 | MD5 改为 BCrypt 加盐 | 30 分钟 |
| 9 | 权限安全 | 创建 `@RequireAdmin` 注解 + AOP 权限切面 | 30 分钟 |
| 10 | 实验九 | 图书/分类 CRUD 改为标准 RESTful URL | 30 分钟 |

### P2 — 锦上添花，时间允许时修复

| 序号 | 修复内容 | 预估工时 |
|---|---|---|
| 11 | 实体类日期类型统一为 LocalDateTime | 30 分钟 |
| 12 | 异常信息脱敏 | 10 分钟 |
| 13 | 添加 Service 层单元测试 | 2 小时 |
| 14 | 分页参数计算提取为工具类 | 20 分钟 |

---

## 五、模拟答辩题库（20 题）

---

### 题 1（实验一）：请描述 MyBatis 的 SqlSessionFactory 在你项目中是如何配置的？

**考察点**：MyBatis 与 Spring 整合方式

**答**：在 `applicationContext.xml:41-45` 使用 `SqlSessionFactoryBean` 注入 dataSource 和全局配置 `mybatis-config.xml`，指定 mapper XML 路径。再通过 `applicationContext.xml:48-51` 的 `MapperScannerConfigurer` 自动扫描 `com.bookssm.mapper` 包下的接口生成代理对象。这与传统手动 `new SqlSessionFactoryBuilder().build()` 不同——Spring 管理了 SqlSession 的完整生命周期，开发者无需手动 open/commit/close。

---

### 题 2（实验一）：你的项目如何输出 MyBatis 执行的 SQL 语句？

**考察点**：日志配置

**答**：通过 `mybatis-config.xml` 设置 `logImpl="LOG4J"`，并在 `log4j.properties` 中将 `com.bookssm.mapper` 包的日志级别设为 DEBUG。这样所有 Mapper 执行的 SQL 语句、参数和结果都会输出到控制台和 `book-ssm.log` 文件。

---

### 题 3（实验二）：#{} 和 ${} 的区别是什么？你在项目中使用的是哪种？为什么？

**考察点**：SQL 注入防护

**答**：`#{}` 是预编译占位符，MyBatis 会将其替换为 `?` 再通过 PreparedStatement 设值，自动处理引号和类型转换，能防止 SQL 注入。`${}` 是字符串直接拼接，不经过预编译，存在注入风险。

我的项目**全部使用 `#{}`**（如 `BookMapper.xml:33` `LIKE CONCAT('%', #{title}, '%')`），因为所有参数都来自用户输入，用 `#{}` 更安全。没有使用 `${}`，这是一个不足——应该在安全校验后的动态排序场景中演示 `${}` 的合法用途。

---

### 题 4（实验二）：你在模糊查询中为什么用 CONCAT 而不是在 Java 代码中拼接百分号？

**考察点**：模糊查询的正确写法

**答**：如果在 Java 代码中拼接好 `%keyword%` 再传给 MyBatis，就必须用 `${}` 而不是 `#{}`（因为 `#{}` 会自动加引号，把 `%keyword%` 当成字符串字面量而非 LIKE 模式）。在 SQL 中使用 `CONCAT('%', #{keyword}, '%')`（`BookMapper.xml:33`）既能用安全的 `#{}`，又能正确构造模糊匹配模式。

---

### 题 5（实验二）：批量删除购物车项是如何实现的？

**考察点**：foreach 标签

**答**：在 `CartMapper.xml:80-87`，使用 `<foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>` 将 List 参数展开为 `IN (?, ?, ?)` 形式。Mapper 接口方法 `deleteByIdsAndUser` 使用 `@Param("ids")` 指定集合参数名，与 XML 中的 `collection="ids"` 对应。

---

### 题 6（实验三）：项目中图书查询如何带出分类名称？用了什么方式？

**考察点**：多表查询

**答**：在 `BookMapper.xml:51-54` 使用 LEFT JOIN 关联 category 表，`b.category_id = c.id`，将 `c.name AS category_name` 加入查询列。通过 `bookMap` resultMap 中的 `<result property="categoryName" column="category_name"/>` 将别名映射到 Book 实体的 `categoryName` 字段。

---

### 题 7（实验三）：Order 类中有 `List<OrderItem> items` 属性，你是如何加载的？有什么更好的做法？

**考察点**：一对多映射

**答**：当前在 Service 层手动加载——`AdminOrderServiceImpl.java:159-163` 和 `OrderServiceImpl.java:155-159` 的 `fillItems()` 方法遍历订单列表，逐个调用 `orderMapper.findItemsByOrderId()` 填充。这导致 N+1 查询问题。

更好的做法是在 `OrderMapper.xml` 的 `orderMap` 中添加 `<collection property="items" ofType="com.bookssm.entity.OrderItem" column="id" select="findItemsByOrderId" fetchType="lazy"/>`，利用 MyBatis 的延迟加载自动填充，减少应用层代码，性能也更好（延迟加载按需触发）。

---

### 题 8（实验四）：请列举你项目中使用的动态 SQL 标签，并说明各自的用途。

**考察点**：动态 SQL 掌握情况

**答**：
- `<if>`：`BookMapper.xml:32-46`，根据参数是否为空动态拼接 WHERE 条件
- `<where>`：`BookMapper.xml:31`，自动去除首个 AND/OR 前缀，无匹配条件时不生成 WHERE
- `<foreach>`：`CartMapper.xml:56-58,83-85`，遍历集合生成 IN 条件和批量删除
- `<sql>` + `<include>`：`BookMapper.xml:24-28` 定义公共列，`:51,70` 复用，避免重复

**未使用的**：`<trim>`、`<choose>/<when>/<otherwise>`、`<set>`，这是需要补充的地方。

---

### 题 9（实验四）：`<where>` 标签的作用是什么？如果不用它会有什么问题？

**考察点**：where 标签机制

**答**：`<where>` 会自动处理两个问题：① 当内部至少有一个条件成立时，插入 `WHERE` 关键字；② 自动移除首个多余的 `AND` 或 `OR`。如果不用 `<where>` 而直接写 `WHERE 1=1`，虽然也能工作，但会产生冗余 SQL 片段，且不够优雅。

---

### 题 10（实验六）：什么是 AOP？请解释连接点、切入点、通知、切面的概念。

**考察点**：AOP 核心概念

**答**：
- **连接点（JoinPoint）**：程序执行的某个点，如方法调用。Spring AOP 中连接点就是方法执行。
- **切入点（Pointcut）**：匹配连接点的表达式，如 `execution(* com.bookssm.service.*.*(..))` 匹配 service 包下所有方法。
- **通知（Advice）**：在切入点执行的代码。五种类型：@Before（前）、@After（后）、@AfterReturning（返回后）、@AfterThrowing（异常后）、@Around（环绕，最强）。
- **切面（Aspect）**：切入点 + 通知的组合，是一个标注了 `@Aspect` 的类。

---

### 题 11（实验六）：你的项目中 AOP 是如何使用的？如果老师说"我没看到你的切面代码"你怎么回答？

**考察点**：AOP 的实际应用（答辩压力题）

**答**：坦率承认当前项目中 AOP 基础设施已就绪——`applicationContext.xml:62` 配置了 `<aop:aspectj-autoproxy/>`，`pom.xml` 引入了 spring-aop 和 aspectjweaver 依赖，但尚未编写自定义切面类。**如果能修复，我会回答**：已创建 `LogAspect` 类（`com.bookssm.aspect.LogAspect`），用 `@Around` 环绕通知记录所有 Service 层方法的调用日志和耗时，演示了 AOP 横切关注点分离的核心价值。此外，计划用 AOP 实现管理员权限统一校验（`@RequireAdmin` 注解 + 切面），替代目前仅在 `AdminOrderServiceImpl` 中手写的 `requireAdmin()` 方法。

---

### 题 12（实验七）：项目中事务是如何管理的？`@Transactional` 放在哪一层？默认传播行为是什么？

**考察点**：声明式事务

**答**：使用 Spring 声明式事务。`applicationContext.xml:54-56` 配置 `DataSourceTransactionManager`，`:59` 通过 `<tx:annotation-driven>` 启用注解事务。`@Transactional` 放在 **Service 层**的写操作方法上（如 `BookServiceImpl:31` create、`AdminOrderServiceImpl:63` ship）。默认传播行为是 `REQUIRED`——如果当前存在事务则加入，不存在则创建新事务。默认隔离级别是 `DEFAULT`，使用数据库默认级别（MySQL 为 REPEATABLE_READ）。

---

### 题 13（实验七）：你在项目中使用了 JdbcTemplate 吗？和 MyBatis 有什么区别？

**考察点**：JdbcTemplate vs MyBatis

**答**：当前项目**仅配置了依赖但未使用 JdbcTemplate**——`pom.xml` 有 `spring-jdbc` 依赖，但 `applicationContext.xml` 没有配置 JdbcTemplate bean。JdbcTemplate 是 Spring 对 JDBC 的轻量封装，适合简单查询和批量操作，不需要 XML 映射文件，但需要手动处理 ResultSet→对象的映射。MyBatis 提供更强大的 SQL 管理能力（XML 映射、动态 SQL、resultMap），适合复杂业务。两者可以互补使用——MyBatis 处理复杂业务查询，JdbcTemplate 处理简单统计查询。

---

### 题 14（实验八）：请描述一个 HTTP 请求从进入 Tomcat 到返回 JSON 响应的完整流程。

**考察点**：SpringMVC 请求生命周期

**答**：
1. Tomcat 接收 HTTP 请求
2. `CorsFilter`（`web.xml:17-24`）先处理跨域头，OPTIONS 请求直接返回 200
3. `CharacterEncodingFilter`（`web.xml:27-42`）设置 UTF-8 编码
4. `DispatcherServlet`（`web.xml:50-62`）接收请求，根据 `@RequestMapping` 找到对应 Controller 方法
5. `LoginInterceptor`（`spring-mvc.xml:36-44`）校验 Session 登录状态，未登录返回 401
6. Controller 方法执行业务逻辑，调用 Service → Mapper → MyBatis → MySQL
7. `@RestController` 使返回值通过 `MappingJackson2HttpMessageConverter` 序列化为 JSON
8. 如有异常，`GlobalExceptionHandler`（`@ControllerAdvice`）拦截并返回统一错误 JSON
9. JSON 通过 HTTP 响应返回给前端

---

### 题 15（实验八）：为什么你的项目没有配置视图解析器？

**考察点**：前后端分离架构

**答**：因为项目采用**前后端分离**架构——后端只提供 REST API 返回 JSON，不渲染 JSP/HTML 页面。前端是独立的 Vue 3 项目（`book-ssm-frontend`），通过 axios 调用后端 API。所以不需要 `InternalResourceViewResolver`，只需要 `MappingJackson2HttpMessageConverter` 做 JSON 序列化（`spring-mvc.xml:22-28`）。

---

### 题 16（实验九）：前后端数据交互使用什么格式？为什么用 URLSearchParams 而不是 JSON？

**考察点**：前后端数据交互方式

**答**：当前使用 **表单格式**（`application/x-www-form-urlencoded`）。前端 `request.js:28-36` 的 `form()` 函数将 JS 对象转为 `URLSearchParams`。后端通过 `@RequestParam` 逐个接收参数。这种方式的优点是简单直观，不需要额外的 DTO 类。但缺点是复杂嵌套对象（如订单含多个明细行）难以表达。

**不足**：没有演示 JSON 格式（`@RequestBody`）。计划增加一个创建订单的 JSON 接口，前端发送 `{cartItemIds: [...], receiverName: "..."}`，后端用 `@RequestBody CreateOrderRequest` 接收。

---

### 题 17（实验九）：你的项目符合 RESTful 风格吗？有哪些可以改进的地方？

**考察点**：RESTful API 设计规范

**答**：**不完全符合**。做得好的地方：查询使用 GET（`GET /api/books`），统一 JSON 响应格式，使用正确的 HTTP 状态语义（200 成功、400/401/403/404/500 错误）。

需要改进的地方：
1. **URL 应面向资源而非操作**：当前 `POST /api/books/create`、`POST /api/books/delete` 将操作动词放在 URL 中，应为 `POST /api/books`（创建）、`DELETE /api/books/{id}`（删除）
2. **应使用更多 HTTP 方法**：当前仅有 GET 和 POST（及少量 PUT），应增加 PUT（全量更新）和 DELETE
3. **未使用 @RequestBody**：RESTful API 通常使用 JSON body 传递数据

---

### 题 18（实验十）：Spring 的父子容器是什么？在你的项目中如何体现？

**考察点**：SSM 容器架构（高频答辩题）

**答**：Spring 支持父子两个 IoC 容器。**父容器**（Root WebApplicationContext）由 `ContextLoaderListener`（`web.xml:45-47`）启动，加载 `applicationContext.xml`，管理 Service、Mapper、数据源、事务等全局组件。**子容器**由 `DispatcherServlet`（`web.xml:50-58`）启动，加载 `spring-mvc.xml`，仅管理 Controller 和异常处理器。

关键隔离规则：父容器**排除** Controller（`applicationContext.xml:21-26`），子容器**仅扫描** Controller（`spring-mvc.xml:15-16`）。子容器可以访问父容器的 bean（Controller 通过 `@Autowired` 注入 Service），但父容器不能访问子容器的 bean。这种隔离防止 Service 层依赖 Web 层，保证架构清晰。

---

### 题 19（综合）：如果老师问"你的 FIFO 出库是如何保证并发安全的？"

**考察点**：事务 + 锁机制（展示深度的加分题）

**答**：FIFO 出库在 `AdminOrderServiceImpl.java:86-135` 的 `shipOrderItem()` 方法中实现，使用**三层保护**：

1. **悲观锁（`SELECT ... FOR UPDATE`）**：`StockMapper.xml` 的 `findBookStockForUpdate` 和 `findAvailableBatchesForUpdate` 对库存行加排他锁，阻止并发事务同时读取和修改
2. **乐观锁（CAS）**：`StockMapper.xml` 的 `updateBatchRemain` 使用 `WHERE remain_quantity = #{beforeRemain}`，只有当库存未被其他事务修改时才更新成功。`AdminOrderServiceImpl:111` 检查 affected rows，不为 1 则抛异常回滚
3. **事务回滚（`@Transactional`）**：整个 `ship()` 方法在一个事务中，`AdminOrderServiceImpl:63`，任何步骤失败都会回滚所有库存扣减和订单状态变更

---

### 题 20（综合）：你认为项目中还有哪些安全隐患？

**考察点**：安全意识

**答**：
1. **密码仅 MD5 无盐**（`UserServiceImpl.java:36`）：应升级为 BCrypt
2. **管理员权限检查不完整**：仅 `AdminOrderServiceImpl` 校验了 ADMIN 角色，`BookController`、`CategoryController`、`StockController` 的增删改操作未做权限校验——任何登录用户都能通过 API 直接操作
3. **异常信息泄露**（`GlobalExceptionHandler.java:29-33`）：将数据库异常类名和消息直接返回前端，可能暴露表结构
4. **无参数校验框架**：所有参数校验通过 if/else 手动实现，建议引入 Hibernate Validator（`@NotNull`、`@Size` 等注解）
5. **Session 固定攻击风险**：登录后未刷新 Session ID

---

## 六、备考重点与复习建议

### 6.1 按实验的必读文件和复习要点

| 实验 | 必读文件 | 复习要点 |
|---|---|---|
| 实验一 | `mybatis-config.xml`、`applicationContext.xml:41-51` | SqlSessionFactoryBean 与手动 SqlSession 的区别；MapperScannerConfigurer 的作用 |
| 实验二 | `BookMapper.xml:32-33,79`、`CartMapper.xml:56-58,83-85` | #{} vs ${} 原理；CONCAT 防注入；useGeneratedKeys 用法；foreach 的 collection 属性 |
| 实验三 | `BookMapper.xml:51-54`、`OrderMapper.xml:6-25` | LEFT JOIN + resultMap；驼峰映射；collection/association 语法；延迟加载配置 |
| 实验四 | `BookMapper.xml:30-48,90-102`、`OrderMapper.xml:110-134` | if/where/foreach/sql/include 语法；set 标签防止全字段更新；trim 与 where 的等价关系 |
| 实验六 | `applicationContext.xml:62` | 五种通知的执行顺序；@Around vs @Before+@After；切入点表达式语法 |
| 实验七 | `applicationContext.xml:54-59`、`AdminOrderServiceImpl.java:63,86-135` | @Transactional 传播行为和隔离级别；事务回滚条件；DataSourceTransactionManager 原理 |
| 实验八 | `web.xml`、`spring-mvc.xml` | DispatcherServlet 请求流程；父子容器隔离原因；拦截器 vs 过滤器 |
| 实验九 | `request.js`、`BookController.java` | @RequestBody vs @RequestParam；RESTful 六大约束；axios 拦截器链 |
| 实验十 | `web.xml` + `applicationContext.xml` + `spring-mvc.xml` | 父子容器 bean 隔离；为什么要排除 Controller；@Autowired 按类型注入原理 |

### 6.2 答辩高频追问 Top 5

1. **"你配置文件中的 Bean 是交给 Spring 管理还是 SpringMVC 管理？两者有什么区别？"** → 用父子容器回答
2. **"AOP 的底层实现是什么？"** → JDK 动态代理（接口）和 CGLIB（无接口的类）；Spring 默认使用 JDK 动态代理，`<aop:aspectj-autoproxy/>` 可设置 `proxy-target-class="true"` 强制 CGLIB
3. **"事务什么时候会回滚？checked exception 会回滚吗？"** → 默认只对 RuntimeException 和 Error 回滚；checked exception 不自动回滚，需用 `@Transactional(rollbackFor = Exception.class)` 指定
4. **"你如何解决前后端跨域问题？为什么不能用 `*` 通配符？"** → W3C CORS 规范：当 `Access-Control-Allow-Credentials: true` 时 `Access-Control-Allow-Origin` 不能为 `*`，必须回显具体 Origin
5. **"MyBatis 一级缓存和二级缓存的区别？"** → 一级缓存是 SqlSession 级别，默认开启；二级缓存是 namespace 级别，需显式配置 `<cache/>`

### 6.3 答辩策略建议

1. **坦诚不足但要展示理解**：对 AOP/JdbcTemplate/@RequestBody 缺失，不要说"没做"，要说"基础设施已就绪，具体实现计划是……"
2. **用 FIFO 出库作为亮点**：悲观锁+乐观锁+事务的并发控制设计是答辩的加分项，主动展示
3. **父子容器必考**：这是 SSM 整合最核心的概念，务必讲清楚"父管 Service/Mapper，子管 Controller，子可访父，父不可访子"
4. **带上代码文件**：答辩时打开 IDE，直接展示关键代码段比口头描述有力得多
