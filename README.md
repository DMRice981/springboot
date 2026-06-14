# Aran Shop 后端与全栈运行说明

Aran Shop 是一个基于 Spring Boot、Vue 3、Element Plus、MySQL 的全栈电商系统，包含用户、商家、管理员三类角色。当前版本已新增 WebSocket 实时聊天第一阶段能力，支持三类角色之间实时文本聊天。

---

## 1. 当前技术栈

| 类型 | 技术 |
| --- | --- |
| 后端框架 | Spring Boot 4.0.6 |
| Java 版本 | JDK 25 |
| 构建工具 | Maven 3.9.6 / Maven Wrapper |
| ORM | MyBatis-Plus 3.5.15 |
| 数据库 | MySQL 8.0+ |
| 实时通信 | Spring WebSocket |
| 简化实体代码 | Lombok 1.18.46 |
| JSON 处理 | Jackson 3.x (tools.jackson) |
| 前端框架 | Vue 3 + Vite |
| UI 组件库 | Element Plus |
| Node 版本 | Node.js 20.19+ |

---

## 2. JDK 25 与 Spring Boot 4 编译策略

后端项目已经明确升级到 JDK 25 和 Spring Boot 4.0.6。

### 2.1 JDK 25 配置

[pom.xml](file:///C:/Users/Lenovo/Desktop/cxode/springboot/pom.xml) 中已经配置：

- `java.version=25`
- `maven.compiler.release=25`
- `lombok.version=1.18.46`
- `maven-compiler-plugin=3.13.0`
- 显式 `annotationProcessorPaths` 指向 Lombok
- `maven-enforcer-plugin=3.5.0` 强制 Maven 只能使用 JDK 25

### 2.2 Spring Boot 4 与 Jackson 3.x

Spring Boot 4 升级了 Jackson 至 3.x，包名从 `com.fasterxml.jackson` 改为 `tools.jackson`。已修改以下文件：

- [JacksonConfig.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/config/JacksonConfig.java)
- [ChatWebSocketHandler.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/websocket/ChatWebSocketHandler.java)
- [LoginInterceptor.java](file:///C:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/interceptor/LoginInterceptor.java)

### 2.3 MyBatis-Plus 适配

使用 `mybatis-plus-spring-boot4-starter` 适配 Spring Boot 4：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot4-starter</artifactId>
    <version>3.5.15</version>
</dependency>
```

### 2.4 问题预防

这样可以避免以下问题再次出现：

- Maven 使用 JDK 25，但项目仍按旧版本编译目标编译
- Lombok 注解处理器未生效
- 实体类 getter/setter 找不到
- `TypeTag :: UNKNOWN` 编译异常
- Spring Boot 3.x 与 JDK 25 的 `Incompatible class format` 错误

如果使用非 JDK 25 运行 Maven，构建会直接失败并提示：

```text
当前项目已升级到 Java 25，请使用 JDK 25 运行 Maven。
```

---

## 3. 推荐运行命令

### 3.1 后端编译

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd C:\Users\Lenovo\Desktop\cxode\springboot
& 'C:\Users\Lenovo\.m2\wrapper\dists\apache-maven-3.9.6-bin\3311e1d4\apache-maven-3.9.6\bin\mvn.cmd' clean compile -DskipTests
```

预期结果：

```text
Compiling 95 source files with javac [debug parameters release 25]
BUILD SUCCESS
```

### 3.2 后端启动

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd C:\Users\Lenovo\Desktop\cxode\springboot
& 'C:\Users\Lenovo\.m2\wrapper\dists\apache-maven-3.9.6-bin\3311e1d4\apache-maven-3.9.6\bin\mvn.cmd' spring-boot:run
```

后端默认地址：

```text
http://localhost:8081
```

### 3.3 前端启动

```powershell
cd C:\Users\Lenovo\Desktop\cxode\shop-aran
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

### 3.4 前端构建

```powershell
cd C:\Users\Lenovo\Desktop\cxode\shop-aran
npm run build
```

当前验证结果：

```text
✓ built in 1.99s
```

---

## 4. 环境变量

后端读取 [springboot/.env](file:///C:/Users/Lenovo/Desktop/cxode/springboot/.env) 或 [springboot/.env.example](file:///C:/Users/Lenovo/Desktop/cxode/springboot/.env.example) 中的数据库配置。

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

如果运行测试或启动后端时报错：

```text
Access denied for user 'root'@'localhost'
```

说明本机 MySQL 账号或密码与 `.env` 不一致，需要修改 `DB_USERNAME` / `DB_PASSWORD`。

---

## 5. 数据库初始化

数据库脚本统一使用根目录下的 [database_init.sql](file:///C:/Users/Lenovo/Desktop/cxode/database_init.sql)。

全量初始化：

```powershell
cd C:\Users\Lenovo\Desktop\cxode
mysql -u root -p < database_init.sql
```

如果已有数据库，只需要补充聊天功能表，可以只执行以下两张表：

```sql
CREATE TABLE IF NOT EXISTS chat_conversation (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user_a_type VARCHAR(20) NOT NULL COMMENT '参与方A类型：USER/SELLER/ADMIN',
    user_a_id INT NOT NULL COMMENT '参与方A ID',
    user_b_type VARCHAR(20) NOT NULL COMMENT '参与方B类型：USER/SELLER/ADMIN',
    user_b_id INT NOT NULL COMMENT '参与方B ID',
    last_message VARCHAR(500) COMMENT '最后一条消息',
    last_message_time DATETIME COMMENT '最后消息时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_chat_pair (user_a_type, user_a_id, user_b_type, user_b_id),
    INDEX idx_user_a (user_a_type, user_a_id),
    INDEX idx_user_b (user_b_type, user_b_id),
    INDEX idx_last_message_time (last_message_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

CREATE TABLE IF NOT EXISTS chat_message (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    conversation_id INT NOT NULL COMMENT '会话ID',
    sender_type VARCHAR(20) NOT NULL COMMENT '发送者类型：USER/SELLER/ADMIN',
    sender_id INT NOT NULL COMMENT '发送者ID',
    receiver_type VARCHAR(20) NOT NULL COMMENT '接收者类型：USER/SELLER/ADMIN',
    receiver_id INT NOT NULL COMMENT '接收者ID',
    message_type VARCHAR(20) DEFAULT 'TEXT' COMMENT '消息类型：TEXT',
    content TEXT NOT NULL COMMENT '消息内容',
    is_read INT DEFAULT 0 COMMENT '是否已读：0未读 1已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_receiver (receiver_type, receiver_id, is_read),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';
```

---

## 6. 后端模块结构

核心包路径：

```text
springboot/src/main/java/com/mybatisplus
```

主要模块：

| 路径 | 说明 |
| --- | --- |
| `config` | CORS、Jackson、MyBatis-Plus、WebSocket、MVC 配置 |
| `controller` | REST 接口控制器 |
| `entity` | 数据库实体 |
| `mapper` | MyBatis-Plus Mapper |
| `service` | 业务接口 |
| `service/impl` | 业务实现 |
| `dto` | 请求与响应 DTO |
| `websocket` | WebSocket 消息处理器 |
| `interceptor` | 登录拦截器 |
| `common` | 统一响应与常量 |

聊天相关后端文件：

| 文件 | 作用 |
| --- | --- |
| `config/WebSocketConfig.java` | 注册 `/ws/chat` WebSocket 地址 |
| `websocket/ChatWebSocketHandler.java` | 管理连接、在线状态、实时消息推送 |
| `controller/ChatController.java` | 聊天 REST 接口 |
| `entity/ChatConversation.java` | 会话实体 |
| `entity/ChatMessage.java` | 消息实体 |
| `mapper/ChatConversationMapper.java` | 会话表访问 |
| `mapper/ChatMessageMapper.java` | 消息表访问 |
| `service/ChatService.java` | 聊天业务接口 |
| `service/impl/ChatServiceImpl.java` | 聊天业务实现 |
| `dto/ChatMessageDTO.java` | WebSocket 消息 DTO |
| `dto/ChatConversationDTO.java` | 会话列表 DTO |
| `dto/ChatContactDTO.java` | 联系人 DTO |
| `dto/ChatConversationRequest.java` | 创建会话请求 |

---

## 7. 实时聊天功能

当前聊天功能为第一阶段：真正实时文本聊天。

已支持：

- 用户、商家、管理员三类角色
- 任意两方互聊
- WebSocket 即时推送
- 会话列表
- 历史消息
- 未读数
- 在线状态
- 选择联系人
- 商品详情页联系商家
- 用户中心我的消息
- 商家后台消息中心
- 管理后台消息管理

暂未包含：

- 图片消息
- 商品卡片消息
- 订单卡片消息
- 群聊
- 消息撤回

### 7.1 WebSocket 地址

```text
ws://localhost:8081/ws/chat?userType=USER&userId=1
ws://localhost:8081/ws/chat?userType=SELLER&userId=1
ws://localhost:8081/ws/chat?userType=ADMIN&userId=1
```

### 7.2 WebSocket 发送消息格式

```json
{
  "type": "CHAT_MESSAGE",
  "conversationId": 1,
  "receiverType": "SELLER",
  "receiverId": 1,
  "messageType": "TEXT",
  "content": "你好，请问这个商品还有库存吗？"
}
```

### 7.3 后端推送消息格式

```json
{
  "type": "CHAT_MESSAGE",
  "data": {
    "id": 100,
    "conversationId": 1,
    "senderType": "USER",
    "senderId": 1,
    "receiverType": "SELLER",
    "receiverId": 1,
    "messageType": "TEXT",
    "content": "你好，请问这个商品还有库存吗？",
    "isRead": 0,
    "createTime": "2026-06-14 10:00:00"
  }
}
```

### 7.4 聊天 REST 接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/chat/conversation` | 创建或获取会话 |
| GET | `/chat/conversations` | 获取当前角色会话列表 |
| GET | `/chat/messages` | 获取历史消息 |
| PUT | `/chat/read/{conversationId}` | 标记会话已读 |
| GET | `/chat/unread-count` | 获取未读消息数 |
| GET | `/chat/contacts` | 获取可聊天联系人 |

---

## 8. 常见问题

### 8.1 getter/setter 找不到

原因通常是 Lombok 注解处理器没有生效。当前项目已通过以下配置解决：

- Lombok 升级到 `1.18.46`
- `maven-compiler-plugin` 显式配置 `annotationProcessorPaths`
- Maven Enforcer 强制使用 JDK 25

请使用 JDK 25 运行：

```powershell
java -version
```

必须看到类似：

```text
java version "25.0.1"
```

### 8.2 TypeTag :: UNKNOWN

这是旧版 Lombok 与 JDK 25 javac 内部 API 不兼容导致的错误。当前已通过 Lombok `1.18.46` 修复。

### 8.3 前端能打开但聊天失败

检查：

1. 后端是否已启动在 `8081`
2. 前端 `.env` 中 `VITE_API_BASE_URL` 是否为 `http://localhost:8081`
3. 数据库是否已经创建 `chat_conversation` 和 `chat_message`
4. 当前用户/商家/管理员是否已经登录

### 8.4 测试连接数据库失败

如果看到：

```text
Access denied for user 'root'@'localhost'
```

修改 [springboot/.env](file:///C:/Users/Lenovo/Desktop/cxode/springboot/.env) 中的数据库用户名和密码。

---

## 9. 当前验证状态

| 验证项 | 命令 | 状态 |
| --- | --- | --- |
| 后端 JDK 25 干净编译 | `mvn clean compile -DskipTests` | 通过 |
| 前端生产构建 | `npm run build` | 通过 |
| 聊天服务测试 | `mvn test -Dtest=ChatServiceBehaviorTest` | 受本机 MySQL 账号密码影响 |

---

## 10. 下一阶段计划

聊天功能后续可继续扩展：

1. 图片消息
2. 商品卡片消息
3. 订单卡片消息
4. 消息失败重试
5. 已读回执细化
6. 最近联系人排序优化
7. 管理员介入客服会话
