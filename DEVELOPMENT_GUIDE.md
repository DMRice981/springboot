# Aran Shop 当前版本开发指南

本指南以当前代码库为准，覆盖 JDK 25 编译策略、后端 Spring Boot、前端 Vue 3、数据库初始化、WebSocket 实时聊天和常见问题排查。

---

## 1. 项目概览

Aran Shop 是一个前后端分离电商系统，包含三类角色：

- 用户：注册登录、浏览商品、购物车、下单、订单、地址、售后、评价、聊天
- 商家：注册登录、商品管理、订单处理、售后处理、聊天
- 管理员：用户管理、分类管理、商品管理、轮播图管理、订单管理、聊天管理

当前重点能力：

- JDK 25 后端构建
- Spring Boot WebSocket 实时聊天
- Vue 3 三端聊天页面
- HTTP 参数自动过滤
- Element Plus 图标规范化

---

## 2. 技术栈

### 2.1 后端

| 技术 | 当前版本/说明 |
| --- | --- |
| Spring Boot | 4.0.6 |
| Java | JDK 25 |
| Maven | 3.9.6 / Maven Wrapper |
| MyBatis-Plus | 3.5.15 (spring-boot4-starter) |
| MySQL | 8.0+ |
| Lombok | 1.18.46 |
| WebSocket | spring-boot-starter-websocket |
| Jackson | 3.x (包名 tools.jackson) |
| Dotenv | dotenv-java 3.0.0 |

### 2.2 前端

| 技术 | 当前版本/说明 |
| --- | --- |
| Vue | Vue 3 |
| Vite | Vite 8 |
| Element Plus | UI 组件库 |
| Vue Router | 路由与守卫 |
| WebSocket | 浏览器原生 WebSocket |
| Node.js | 20.19+ |

---

## 3. JDK 25 与 Spring Boot 4 配置

后端已经完全升级到 JDK 25 和 Spring Boot 4.0.6。核心配置位于 [pom.xml](file:///C:/Users/Lenovo/Desktop/cxode/springboot/pom.xml)。

### 3.1 JDK 25 关键策略

- `java.version=25`
- `maven.compiler.release=25`
- `lombok.version=1.18.46`
- 使用 `maven-compiler-plugin=3.13.0`
- 显式配置 Lombok annotation processor
- 使用 `maven-enforcer-plugin=3.5.0` 强制 JDK 25

这样可以避免：

- 本机默认 JDK 与项目目标版本不一致
- Lombok getter/setter 生成失败
- JDK 25 下旧版 Lombok 报 `TypeTag :: UNKNOWN`
- 编译成功依赖某个临时终端环境变量

### 3.2 Spring Boot 4 与 Jackson 3.x

Spring Boot 4 升级了 Jackson 至 3.x，包名从 `com.fasterxml.jackson` 改为 `tools.jackson`。

修改的文件：

- [JacksonConfig.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/config/JacksonConfig.java)
- [ChatWebSocketHandler.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/websocket/ChatWebSocketHandler.java)
- [LoginInterceptor.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/interceptor/LoginInterceptor.java)

关键变更：

```java
// 旧版 (Jackson 2.x)
import com.fasterxml.jackson.databind.ObjectMapper;

// 新版 (Jackson 3.x)
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

// ObjectMapper 构建方式变更
ObjectMapper mapper = JsonMapper.builder()
        .addModule(module)
        .build();
```

### 3.3 MyBatis-Plus 适配

MyBatis-Plus 3.5.15 使用 `mybatis-plus-spring-boot4-starter`：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot4-starter</artifactId>
    <version>3.5.15</version>
</dependency>
```

分页插件已由自动配置处理，[MybatisPlusConfig.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/config/MybatisPlusConfig.java) 简化为仅配置拦截器。

### 3.4 验证 Java 版本

```powershell
java -version
```

期望看到：

```text
java version "25.0.1"
```

如果 Maven 使用的不是 JDK 25，会直接失败并提示：

```text
当前项目已升级到 Java 25，请使用 JDK 25 运行 Maven。
```

---

## 4. 环境准备

### 4.1 必备软件

- JDK 25
- Node.js 20.19+
- MySQL 8.0+
- Maven 3.9.6 或项目 Maven Wrapper
- IntelliJ IDEA / VS Code

### 4.2 后端环境变量

后端配置文件：

- [springboot/.env](file:///C:/Users/Lenovo/Desktop/cxode/springboot/.env)
- [springboot/.env.example](file:///C:/Users/Lenovo/Desktop/cxode/springboot/.env.example)

示例：

```env
SERVER_PORT=8081
DB_HOST=localhost
DB_PORT=3306
DB_NAME=shop_mall
DB_USERNAME=root
DB_PASSWORD=你的数据库密码
DB_USE_UNICODE=true
DB_CHARACTER_ENCODING=utf8
DB_SERVER_TIMEZONE=GMT%2B8
DB_USE_SSL=false
```

### 4.3 前端环境变量

前端配置文件：

- [shop-aran/.env](file:///C:/Users/Lenovo/Desktop/cxode/shop-aran/.env)
- [shop-aran/.env.example](file:///C:/Users/Lenovo/Desktop/cxode/shop-aran/.env.example)

示例：

```env
VITE_API_BASE_URL=http://localhost:8081
VITE_API_PREFIX=/api
VITE_REQUEST_TIMEOUT=5000
```

---

## 5. 常用命令

### 5.1 后端干净编译

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd C:\Users\Lenovo\Desktop\cxode\springboot
& 'C:\Users\Lenovo\.m2\wrapper\dists\apache-maven-3.9.6-bin\3311e1d4\apache-maven-3.9.6\bin\mvn.cmd' clean compile -DskipTests
```

成功标志：

```text
Compiling 95 source files with javac [debug parameters release 25]
BUILD SUCCESS
```

### 5.2 后端启动

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd C:\Users\Lenovo\Desktop\cxode\springboot
& 'C:\Users\Lenovo\.m2\wrapper\dists\apache-maven-3.9.6-bin\3311e1d4\apache-maven-3.9.6\bin\mvn.cmd' spring-boot:run
```

### 5.3 前端安装依赖

```powershell
cd C:\Users\Lenovo\Desktop\cxode\shop-aran
npm install
```

### 5.4 前端开发运行

```powershell
npm run dev
```

### 5.5 前端生产构建

```powershell
npm run build
```

成功标志：

```text
✓ built
```

---

## 6. 数据库

数据库脚本统一使用 [database_init.sql](file:///C:/Users/Lenovo/Desktop/cxode/database_init.sql)。

全量初始化：

```powershell
cd C:\Users\Lenovo\Desktop\cxode
mysql -u root -p < database_init.sql
```

核心表包括：

- `user`
- `seller`
- `admin`
- `category`
- `goods`
- `goods_img`
- `cart`
- `order_info`
- `order_item`
- `user_address`
- `banner`
- `goods_comment`
- `after_sale`
- `chat_conversation`
- `chat_message`

聊天功能新增表：

| 表 | 说明 |
| --- | --- |
| `chat_conversation` | 两个角色之间的会话 |
| `chat_message` | 聊天消息记录 |

如果已有数据库，只需补充聊天表，可从 [database_init.sql](file:///C:/Users/Lenovo/Desktop/cxode/database_init.sql) 中复制 `chat_conversation` 和 `chat_message` 两段 SQL 执行。

---

## 7. 后端结构

路径：[springboot/src/main/java/com/mybatisplus](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus)

| 包 | 作用 |
| --- | --- |
| `common` | `Result`、常量等公共对象 |
| `config` | CORS、Jackson、MyBatis-Plus、WebSocket、MVC 配置 |
| `controller` | REST API 控制器 |
| `dto` | 请求和响应 DTO |
| `entity` | MyBatis-Plus 实体 |
| `interceptor` | 登录拦截器 |
| `mapper` | 数据访问接口 |
| `service` | 服务接口 |
| `service/impl` | 服务实现 |
| `websocket` | WebSocket 消息处理 |

### 7.1 聊天后端文件

| 文件 | 作用 |
| --- | --- |
| [WebSocketConfig.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/config/WebSocketConfig.java) | 注册 `/ws/chat` |
| [ChatWebSocketHandler.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/websocket/ChatWebSocketHandler.java) | 管理连接、在线状态和实时消息 |
| [ChatController.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/ChatController.java) | 聊天 REST 接口 |
| [ChatService.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/service/ChatService.java) | 聊天服务接口 |
| [ChatServiceImpl.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/service/impl/ChatServiceImpl.java) | 聊天业务实现 |
| [ChatConversation.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/entity/ChatConversation.java) | 会话实体 |
| [ChatMessage.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/entity/ChatMessage.java) | 消息实体 |

---

## 8. 前端结构

路径：[shop-aran/src](file:///C:/Users/Lenovo/Desktop/cxode/shop-aran/src)

| 目录 | 作用 |
| --- | --- |
| `components` | 可复用组件，目前包含 `ChatPanel.vue` |
| `plugins` | 插件系统：认证、请求、WebSocket、Element Plus、进度条 |
| `router` | 路由定义和守卫 |
| `utils` | 格式化、防抖、验证工具 |
| `views` | 用户、商家、管理员页面 |

### 8.1 聊天前端文件

| 文件 | 作用 |
| --- | --- |
| [websocket.js](file:///C:/Users/Lenovo/Desktop/cxode/shop-aran/src/plugins/websocket.js) | WebSocket 客户端插件 |
| [ChatPanel.vue](file:///C:/Users/Lenovo/Desktop/cxode/shop-aran/src/components/ChatPanel.vue) | 三端复用聊天面板 |
| [Chat.vue](file:///C:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Chat.vue) | 用户聊天页面 |
| [SellerChat.vue](file:///C:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerChat.vue) | 商家聊天页面 |
| [AdminChat.vue](file:///C:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/AdminChat.vue) | 管理员聊天页面 |

---

## 9. 实时聊天功能

当前为第一阶段：实时文本聊天核心。

已支持：

- 用户、商家、管理员任意两方互聊
- WebSocket 即时推送
- 会话列表
- 历史消息
- 未读数
- 在线状态
- 选择联系人弹窗
- 商品详情页联系商家
- 用户、商家、管理员三端入口

暂未实现：

- 图片消息
- 商品卡片消息
- 订单卡片消息
- 群聊
- 撤回消息

### 9.1 WebSocket 地址

```text
ws://localhost:8081/ws/chat?userType=USER&userId=1
ws://localhost:8081/ws/chat?userType=SELLER&userId=1
ws://localhost:8081/ws/chat?userType=ADMIN&userId=1
```

### 9.2 聊天 REST 接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/chat/conversation` | 创建或获取会话 |
| GET | `/chat/conversations` | 查询会话列表 |
| GET | `/chat/messages` | 查询历史消息 |
| PUT | `/chat/read/{conversationId}` | 标记已读 |
| GET | `/chat/unread-count` | 查询未读数 |
| GET | `/chat/contacts` | 查询联系人 |

### 9.3 前端聊天入口

| 入口 | 说明 |
| --- | --- |
| `/chat` | 用户消息中心 |
| `/seller/chat` | 商家消息中心 |
| `/admin/chat` | 管理员消息管理 |
| 用户中心 | 我的消息 |
| 商家后台 | 消息中心 |
| 管理后台 | 消息管理 |
| 商品详情页 | 联系商家 |

---

## 10. 开发规范

### 10.1 后端规范

- Controller 使用 `@RestController` 和 `@RequiredArgsConstructor`
- 响应统一使用 `Result<T>`
- 分页响应统一使用 `PageResult<T>`
- 实体使用 MyBatis-Plus 注解
- 时间字段统一由 Jackson 格式化为 `yyyy-MM-dd HH:mm:ss`
- 新增接口应优先复用已有 Service，不在 Controller 中堆业务逻辑

### 10.2 前端规范

- 页面使用 Vue 3 `<script setup>`
- HTTP 请求统一使用 `inject('http')`
- 聊天连接统一使用 `inject('chatSocket')`
- 登录态统一使用 `inject('auth')`
- Element Plus 图标统一使用别名导入

图标推荐写法：

```javascript
import { Search as IconSearch } from '@element-plus/icons-vue'
```

模板使用：

```vue
<el-icon><component :is="IconSearch" /></el-icon>
```

不要使用不存在的图标名，例如：

- `Shield`
- `Truck`

可替换为：

- `Medal`
- `Van`

### 10.3 HTTP 参数规范

筛选项的“全部”值使用空字符串：

```javascript
const filterStatus = ref('')
```

请求参数中使用：

```javascript
status: filterStatus.value !== '' ? filterStatus.value : undefined
```

`http.get()` 会自动过滤：

- `null`
- `undefined`
- `''`

避免后端出现：

```text
Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'
```

---

## 11. 测试和验证

### 11.1 后端编译

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd C:\Users\Lenovo\Desktop\cxode\springboot
& 'C:\Users\Lenovo\.m2\wrapper\dists\apache-maven-3.9.6-bin\3311e1d4\apache-maven-3.9.6\bin\mvn.cmd' clean compile -DskipTests
```

当前已验证通过。

### 11.2 前端构建

```powershell
cd C:\Users\Lenovo\Desktop\cxode\shop-aran
npm run build
```

当前已验证通过。

### 11.3 聊天功能手动验证

1. 启动后端
2. 启动前端
3. 准备至少两个登录身份，例如用户和商家
4. 用户打开 `/chat` 或商品详情页点击“联系商家”
5. 商家打开 `/seller/chat`
6. 双方发送文本消息
7. 验证消息是否实时出现
8. 刷新页面后验证历史消息是否仍存在

---

## 12. 常见问题排查

### 12.1 getter/setter 找不到

原因：Lombok 注解处理器未生效。

当前解决方案：

- JDK 25
- Lombok 1.18.46
- Maven Compiler Plugin 显式 annotation processor
- Maven Enforcer 强制 JDK 25

重新执行：

```powershell
mvn clean compile -DskipTests
```

### 12.2 TypeTag :: UNKNOWN

原因：旧版 Lombok 不兼容 JDK 25。

解决：确认 [pom.xml](file:///C:/Users/Lenovo/Desktop/cxode/springboot/pom.xml) 中是：

```xml
<lombok.version>1.18.46</lombok.version>
```

### 12.3 Access denied for user root

原因：数据库用户名或密码错误。

解决：修改 [springboot/.env](file:///C:/Users/Lenovo/Desktop/cxode/springboot/.env)：

```env
DB_USERNAME=root
DB_PASSWORD=你的数据库密码
```

### 12.4 聊天接口 404 或 WebSocket 连接失败

检查：

1. 后端是否启动成功
2. `spring-boot-starter-websocket` 是否在 [pom.xml](file:///C:/Users/Lenovo/Desktop/cxode/springboot/pom.xml)
3. 前端 `VITE_API_BASE_URL` 是否是 `http://localhost:8081`
4. WebSocket 地址是否为 `/ws/chat`
5. 当前角色是否已登录并有有效 ID

### 12.5 聊天消息无法保存

检查数据库是否存在：

- `chat_conversation`
- `chat_message`

如果没有，从 [database_init.sql](file:///C:/Users/Lenovo/Desktop/cxode/database_init.sql) 执行对应建表 SQL。

---

## 13. 发布前检查清单

- [ ] JDK 25 可用
- [ ] `java -version` 显示 25
- [ ] MySQL 已启动
- [ ] `.env` 数据库账号密码正确
- [ ] `database_init.sql` 已执行
- [ ] `chat_conversation` 表存在
- [ ] `chat_message` 表存在
- [ ] 后端 `clean compile -DskipTests` 通过
- [ ] 前端 `npm run build` 通过
- [ ] 用户端 `/chat` 可访问
- [ ] 商家端 `/seller/chat` 可访问
- [ ] 管理端 `/admin/chat` 可访问
- [ ] 商品详情页“联系商家”可跳转

---

## 14. 后续开发路线

推荐按阶段继续扩展聊天功能：

1. 图片消息
2. 商品卡片消息
3. 订单卡片消息
4. 消息发送失败重试
5. 已读回执细化
6. 管理员介入客服会话
7. 聊天记录搜索
8. 消息撤回和删除
