# Aran Shop 项目完整开发指南

## 📋 目录

1. [项目概述](#项目概述)
2. [环境准备](#环境准备)
3. [数据库配置](#数据库配置)
4. [后端开发](#后端开发)
5. [前端开发](#前端开发)
6. [功能详解](#功能详解)
7. [开发流程](#开发流程)
8. [测试部署](#测试部署)
9. [常用命令](#常用命令)
10. [问题排查](#问题排查)
11. [开发最佳实践](#开发最佳实践)
12. [项目文件速查表](#项目文件速查表)
13. [检查清单](#检查清单)

---

## 🏗️ 一、项目概述

### 1.1 项目介绍

Aran Shop 是一个基于 Spring Boot + Vue 3 的全栈电商系统，包含三个角色：
- **用户**：浏览商品、下单购物、申请售后
- **商家**：管理商品、处理订单发货、处理售后
- **管理员**：管理平台商品、用户、订单等

### 1.2 技术栈

#### 后端技术栈
- Spring Boot 3.2.5
- MyBatis Plus 3.5.6
- MySQL 8.0+
- Lombok
- Dotenv（环境变量管理）
- Jackson（统一时间格式）

#### 前端技术栈
- Vue 3
- Vite
- Element Plus
- Vue Router
- Axios

### 1.3 项目结构

```
cxode/
├── springboot/              # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/mybatisplus/
│   │   │   │       ├── config/           # 配置类
│   │   │   │       │   ├── CorsConfig.java
│   │   │   │       │   ├── DotenvConfig.java
│   │   │   │       │   ├── JacksonConfig.java
│   │   │   │       │   ├── GlobalExceptionHandler.java
│   │   │   │       │   └── WebMvcConfig.java
│   │   │   │       ├── controller/       # 控制器（构造器注入）
│   │   │   │       ├── entity/           # 实体类
│   │   │   │       ├── mapper/           # 数据访问层
│   │   │   │       ├── service/          # 服务层
│   │   │   │       │   └── impl/         # 服务实现
│   │   │   │       ├── dto/              # 数据传输对象
│   │   │   │       ├── interceptor/      # 拦截器
│   │   │   │       │   └── LoginInterceptor.java
│   │   │   │       └── SpringbootApplication.java
│   │   │   └── resources/
│   │   │       └── application.yml       # 配置文件
│   │   └── test/
│   ├── .env                      # 环境变量（不提交）
│   ├── .env.example              # 环境变量模板
│   └── pom.xml

├── shop-aran/                # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── assets/            # 资源文件
│   │   ├── plugins/           # 插件系统
│   │   │   ├── index.js
│   │   │   ├── progress.js    # 进度条插件
│   │   │   ├── element.js     # Element Plus 增强
│   │   │   ├── auth.js        # 认证插件
│   │   │   └── request.js     # HTTP 请求插件
│   │   ├── router/            # 路由配置（含守卫）
│   │   ├── utils/             # 工具类
│   │   │   ├── format.js      # 格式化工具
│   │   │   ├── debounce.js    # 防抖节流
│   │   │   └── valid.js       # 验证工具
│   │   ├── views/             # 页面
│   │   │   ├── admin/         # 管理员页面
│   │   │   └── seller/        # 商家页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── public/
│   ├── .env                      # 环境变量（不提交）
│   ├── .env.example              # 环境变量模板
│   ├── vite.config.js
│   └── package.json

└── database_init.sql           # 数据库初始化脚本（唯一）
```

### 1.4 最新更新动态

| 更新内容 | 说明 | 状态 |
|---------|------|------|
| 商家订单管理功能 | 新增 SellerOrder.vue，商家可查看和处理订单 | ✅ 已完成 |
| 管理员商品管理显示商家 | 商品列表显示商家店铺名称和商家用户名 | ✅ 已完成 |
| 订单状态流转优化 | 重新定义订单状态：0待支付→1待发货→2已发货→3已完成→4已取消 | ✅ 已完成 |
| 前端请求方式统一 | 所有页面统一使用 http 插件替代直接 fetch 调用 | ✅ 已完成 |
| 构造器注入 | 所有 Controller 从 `@Autowired` 改为 `@RequiredArgsConstructor` | ✅ 已完成 |
| 常量类 | 创建 Constants.java 统一管理状态码、错误码等 | ✅ 已完成 |
| 时间格式统一 | 创建 JacksonConfig.java，统一时间格式为 `yyyy-MM-dd HH:mm:ss` | ✅ 已完成 |
| 全局异常处理 | 创建 GlobalExceptionHandler.java 统一处理异常 | ✅ 已完成 |
| 登录拦截器 | 创建 LoginInterceptor.java 保护后端接口 | ✅ 已完成 |
| 数据库文件清理 | 删除冗余的 init-db.sql，保留 database_init.sql | ✅ 已完成 |
| 分类功能完善 | 支持树形分类结构（pid 字段） | ✅ 已完成 |
| 售后功能完善 | 完整的售后申请和处理流程 | ✅ 已完成 |
| 商品下架功能 | 下架商品禁用购买按钮 | ✅ 已完成 |
| 商家商品隔离 | 商家只能查看和管理自己的商品 | ✅ 已完成 |
| 前端插件系统 | 添加进度条、认证、请求等插件 | ✅ 已完成 |
| 路由进度条 | 添加页面切换进度条动画 | ✅ 已完成 |
| 代码格式化 | 标准化代码风格 | ✅ 已完成 |

---

## 🏗️ 二、环境准备

### 2.1 必备工具安装

#### JDK 17+
- 下载地址：https://adoptium.net/
- 配置环境变量 `JAVA_HOME`
- 验证：`java -version`

#### Node.js 20.19+
- 下载地址：https://nodejs.org/
- 验证：`node -v` 和 `npm -v`

#### Maven 3.6+
- 项目已包含 Maven Wrapper，无需额外安装

#### MySQL 8.0+
- 下载地址：https://dev.mysql.com/downloads/mysql/
- 配置 root 用户密码

#### IDE
- IntelliJ IDEA（后端）
- VS Code（前端）

---

## 🗄️ 三、数据库配置

### 3.1 数据库初始化

**重要**：项目仅保留一个数据库初始化文件 [database_init.sql](file:///c:/Users/Lenovo/Desktop/cxode/database_init.sql)，已删除冗余的 `init-db.sql`。

```bash
cd cxode
mysql -u root -p < database_init.sql
```

### 3.2 数据库表结构设计

#### 用户表 (user)

```sql
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    status INT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    is_delete INT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';
```

#### 商家表 (seller)

```sql
CREATE TABLE seller (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '商家ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    shop_name VARCHAR(100) COMMENT '店铺名称',
    phone VARCHAR(20) COMMENT '手机号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '商家表';
```

#### 管理员表 (admin)

```sql
CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    admin_name VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    status INT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '管理员表';
```

#### 商品分类表 (category) - 支持树形结构

```sql
CREATE TABLE category (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    pid INT DEFAULT 0 COMMENT '父级分类ID：0表示顶级分类',
    sort INT DEFAULT 0 COMMENT '排序值',
    is_delete INT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '商品分类表';
```

#### 商品表 (goods)

```sql
CREATE TABLE goods (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    category_id INT COMMENT '分类ID',
    goods_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    goods_img VARCHAR(255) COMMENT '商品主图',
    price DECIMAL(10,2) COMMENT '售价',
    market_price DECIMAL(10,2) COMMENT '市场价',
    stock INT DEFAULT 0 COMMENT '库存',
    sales INT DEFAULT 0 COMMENT '销量',
    goods_desc TEXT COMMENT '商品描述',
    status INT DEFAULT 1 COMMENT '状态：1上架 0下架',
    is_delete INT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    seller_id INT COMMENT '商家ID'
) COMMENT '商品表';
```

#### 商品图片表 (goods_img)

```sql
CREATE TABLE goods_img (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
    goods_id INT COMMENT '商品ID',
    img_url VARCHAR(255) COMMENT '图片URL',
    sort INT DEFAULT 0 COMMENT '排序值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '商品图片表';
```

#### 购物车表 (cart)

```sql
CREATE TABLE cart (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID',
    user_id INT COMMENT '用户ID',
    goods_id INT COMMENT '商品ID',
    num INT DEFAULT 1 COMMENT '购买数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '购物车表';
```

#### 订单表 (`order`)

```sql
CREATE TABLE `order` (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) UNIQUE COMMENT '订单号',
    user_id INT COMMENT '用户ID',
    address_id INT COMMENT '收货地址ID',
    total_price DECIMAL(10,2) COMMENT '总价',
    pay_price DECIMAL(10,2) COMMENT '实付金额',
    pay_status INT DEFAULT 0 COMMENT '支付状态：0未支付 1已支付',
    order_status INT DEFAULT 0 COMMENT '订单状态：0待支付 1待发货 2已发货 3已完成 4已取消',
    pay_time DATETIME COMMENT '支付时间',
    send_time DATETIME COMMENT '发货时间',
    confirm_time DATETIME COMMENT '确认收货时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '订单表';
```

**订单状态说明**：
- `0`: 待支付 - 用户创建订单，等待支付
- `1`: 待发货 - 用户已支付，等待商家发货
- `2`: 已发货 - 商家已发货，商品配送中
- `3`: 已完成 - 用户确认收货，订单完成
- `4`: 已取消 - 订单被取消

#### 订单项表 (order_item)

```sql
CREATE TABLE order_item (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单项ID',
    order_no VARCHAR(50) COMMENT '订单号',
    goods_id INT COMMENT '商品ID',
    goods_name VARCHAR(100) COMMENT '商品名称',
    goods_img VARCHAR(255) COMMENT '商品图片',
    price DECIMAL(10,2) COMMENT '商品单价',
    num INT COMMENT '购买数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '订单项表';
```

#### 用户地址表 (user_address)

```sql
CREATE TABLE user_address (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
    user_id INT COMMENT '用户ID',
    name VARCHAR(50) COMMENT '收货人姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    detail VARCHAR(255) COMMENT '详细地址',
    is_default INT DEFAULT 0 COMMENT '是否默认：0否 1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '用户地址表';
```

#### 商品评论表 (goods_comment)

```sql
CREATE TABLE goods_comment (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    goods_id INT COMMENT '商品ID',
    user_id INT COMMENT '用户ID',
    order_no VARCHAR(50) COMMENT '订单号',
    score INT COMMENT '评分：1-5星',
    content TEXT COMMENT '评论内容',
    comment_img VARCHAR(255) COMMENT '评论图片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '商品评论表';
```

#### 轮播图表 (banner)

```sql
CREATE TABLE banner (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '轮播图ID',
    img_url VARCHAR(255) COMMENT '图片URL',
    link_url VARCHAR(255) COMMENT '跳转链接',
    sort INT DEFAULT 0 COMMENT '排序值',
    status INT DEFAULT 1 COMMENT '状态：1显示 0隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '轮播图表';
```

#### 售后表 (after_sale)

```sql
CREATE TABLE after_sale (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '售后ID',
    order_id INT COMMENT '订单ID',
    user_id INT COMMENT '用户ID',
    seller_id INT COMMENT '商家ID',
    goods_id INT COMMENT '商品ID',
    reason TEXT COMMENT '售后原因',
    status INT DEFAULT 0 COMMENT '处理状态：0待处理 1已处理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '售后表';
```

### 3.3 数据库表关系图

```
┌─────────────┐
│    user     │───────┐
└─────────────┘       │
                      │ 1:N
                      ▼
┌─────────────┐  1:N ┌──────────────┐
│ user_address│──────│    order     │
└─────────────┘      └──────────────┘
                            │ 1:N
                            ▼
                     ┌──────────────┐
                     │  order_item  │
                     └──────────────┘
                            │ N:1
                            ▼
                     ┌─────────────┐     ┌─────────────┐
                     │    goods    │◄────│   category  │
                     └─────────────┘     └─────────────┘
                            │ 1:N
                            │ N:1
                            ▼
                     ┌─────────────┐
                     │  goods_img   │
                     └─────────────┘

┌─────────────┐
│   seller    │───────┐
└─────────────┘       │ 1:N
                      ▼
              ┌─────────────┐
              │    goods    │
              └─────────────┘

┌─────────────┐
│   admin     │
└─────────────┘

┌─────────────┐      ┌─────────────┐
│goods_comment│◄─────│    goods    │
└─────────────┘      └─────────────┘

┌─────────────┐      ┌─────────────┐
│  after_sale │◄─────│    order    │
└─────────────┘      └─────────────┘
```

### 3.4 测试数据

数据库初始化脚本已包含测试数据：
- 管理员：admin / 123456
- 商家：seller1 / 123456
- 用户：user1 / 123456
- 分类：电子产品、服装、食品、家居（支持树形结构）
- 商品：4个测试商品（关联商家）
- 轮播图：2张测试轮播图
- 收货地址：1条测试地址

---

## 🚀 四、后端开发

### 4.1 项目结构说明

```
springboot/src/main/java/com/mybatisplus/
├── config/
│   ├── CorsConfig.java              # 跨域配置
│   ├── DotenvConfig.java            # 环境变量配置
│   ├── JacksonConfig.java           # 统一时间格式配置
│   ├── GlobalExceptionHandler.java  # 全局异常处理
│   ├── WebMvcConfig.java            # Web MVC配置（含拦截器注册）
│   └── Constants.java               # 常量类
├── controller/                      # 控制器层（构造器注入）
├── entity/                          # 实体类
├── mapper/                          # 数据访问层
├── service/                         # 服务层
│   └── impl/                        # 服务实现
├── dto/                             # 数据传输对象
│   ├── OrderDTO.java
│   ├── AfterSaleDTO.java
│   └── GoodsWithSellerVO.java       # 商品视图对象（包含商家信息）
├── interceptor/                     # 拦截器
│   └── LoginInterceptor.java        # 登录拦截器
└── SpringbootApplication.java       # 启动类
```

### 4.2 配置环境变量

#### 步骤 1: 复制配置文件

```bash
cd springboot
copy .env.example .env
```

#### 步骤 2: 修改 .env 配置

编辑 `springboot/.env` 文件：

```env
# 服务器配置
SERVER_PORT=8081

# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=shop_mall
DB_USERNAME=root
DB_PASSWORD=你的MySQL密码

# 数据库连接配置
DB_USE_UNICODE=true
DB_CHARACTER_ENCODING=utf8
DB_SERVER_TIMEZONE=GMT%2B8
DB_USE_SSL=false
```

### 4.3 后端架构改进

#### 构造器注入（推荐）

所有 Controller 已从 `@Autowired` 字段注入重构为构造函数注入：

```java
@RestController
@RequestMapping("/after-sale")
@RequiredArgsConstructor
public class AfterSaleController {
    private final AfterSaleService afterSaleService;
    private final GoodsService goodsService;
    // ...
}
```

#### 统一时间格式

通过 `JacksonConfig.java` 统一配置时间格式为 `yyyy-MM-dd HH:mm:ss`，所有实体类时间字段添加 `@JsonFormat` 注解：

```java
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private LocalDateTime createTime;
```

#### 常量管理

使用 `Constants.java` 统一管理状态码、错误码等常量，消除硬编码：

```java
public class Constants {
    // 通用状态
    public static class Status {
        public static final Integer NOT_DELETED = 0;
        public static final Integer DELETED = 1;
    }
    
    // 商品状态
    public static class GoodsStatus {
        public static final Integer ON_SHELF = 1;
        public static final Integer OFF_SHELF = 0;
    }
    
    // 订单状态
    public static class OrderStatus {
        public static final Integer PENDING = 0;           // 待支付
        public static final Integer PAID = 1;              // 已支付，待发货
        public static final Integer SHIPPED = 2;           // 已发货，配送中
        public static final Integer COMPLETED = 3;         // 已完成
        public static final Integer CANCELLED = 4;          // 已取消
    }
}
```

#### 全局异常处理

通过 `GlobalExceptionHandler.java` 统一处理各类异常：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(...) { ... }
    
    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handleNullPointerException(...) { ... }
    // ...
}
```

#### 登录拦截器

通过 `LoginInterceptor.java` 保护后端接口：

- 公开接口：登录、注册、商品列表、商品详情、轮播图、分类列表、商家信息
- 需要登录的接口：其他所有接口

### 4.4 后端启动

#### 方式一：Maven 命令

```bash
cd springboot
.\mvnw.cmd spring-boot:run
```

#### 方式二：IDEA 运行

1. 打开 IDEA 导入 `springboot` 目录
2. 等待 Maven 依赖下载
3. 运行 `SpringbootApplication.java`
4. 访问 http://localhost:8081 测试

### 4.5 验证后端

测试接口：http://localhost:8081/api/goods/list

---

## 🎨 五、前端开发

### 5.1 项目结构说明

```
shop-aran/src/
├── api/               # API 接口
│   ├── admin.js
│   ├── banner.js
│   ├── cart.js
│   ├── category.js
│   ├── goods.js
│   ├── goodsComment.js
│   ├── goodsImg.js
│   ├── order.js
│   ├── orderItem.js
│   ├── user.js
│   └── userAddress.js
├── assets/            # 资源文件
├── plugins/           # 插件系统
│   ├── index.js       # 插件入口
│   ├── progress.js    # 进度条插件
│   ├── element.js     # Element Plus 增强
│   ├── auth.js        # 认证插件
│   └── request.js     # HTTP 请求插件
├── router/            # 路由配置
│   └── index.js       # 路由配置（含守卫和进度条）
├── utils/             # 工具类
│   ├── format.js      # 格式化工具
│   ├── debounce.js    # 防抖节流
│   └── valid.js       # 验证工具
├── views/             # 页面
│   ├── admin/         # 管理员页面
│   │   ├── AdminIndex.vue
│   │   ├── AdminLogin.vue
│   │   ├── BannerManage.vue
│   │   ├── CategoryManage.vue   # 分类管理（支持树形结构）
│   │   ├── GoodsManage.vue       # 商品管理（含商家信息显示）
│   │   ├── OrderManage.vue
│   │   └── UserManage.vue
│   ├── seller/        # 商家页面
│   │   ├── SellerAfterSale.vue   # 售后处理
│   │   ├── SellerGoods.vue       # 商品管理（仅显示自己的商品）
│   │   ├── SellerIndex.vue       # 商家中心首页
│   │   ├── SellerLogin.vue
│   │   ├── SellerOrder.vue       # 订单管理（含发货功能）🆕
│   │   └── SellerRegister.vue
│   ├── Address.vue
│   ├── AfterSale.vue            # 用户售后申请
│   ├── Cart.vue
│   ├── Checkout.vue
│   ├── Comment.vue
│   ├── GoodsDetail.vue          # 商品详情（含下架状态）
│   ├── Index.vue                # 首页（含分类导航）
│   ├── Login.vue
│   ├── Order.vue                # 订单管理
│   ├── Register.vue
│   └── User.vue
├── App.vue            # 根组件（含全局样式）
└── main.js            # 入口文件
```

### 5.2 配置环境变量

#### 步骤 1: 复制配置文件

```bash
cd shop-aran
copy .env.example .env
```

#### 步骤 2: 修改 .env 配置

编辑 `shop-aran/.env` 文件：

```env
# API 配置
VITE_API_BASE_URL=http://localhost:8081
VITE_API_PREFIX=/api

# 请求超时配置（毫秒）
VITE_REQUEST_TIMEOUT=5000
```

### 5.3 前端插件系统

#### 插件列表

| 插件 | 说明 | 文件 |
|------|------|------|
| progress | 页面加载进度条 | [progress.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/plugins/progress.js) |
| element | Element Plus 增强（消息提示、确认框、通知） | [element.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/plugins/element.js) |
| auth | 认证管理（用户/管理员/商家） | [auth.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/plugins/auth.js) |
| request | HTTP 请求封装 | [request.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/plugins/request.js) |

#### 插件使用示例

```vue
<script setup>
import { inject } from 'vue'

const msg = inject('msg')
const http = inject('http')
const auth = inject('auth')

// 消息提示
msg.success('操作成功')
msg.error('操作失败')

// HTTP 请求（统一使用 http 插件）🆕
const result = await http.get('/goods/list')
const data = await http.post('/cart/add', { userId: 1, goodsId: 1, num: 1 })
await http.put('/goods/update', formData)
await http.delete('/goods/delete/1')

// 认证管理
auth.setUser(user)
auth.getUser()
auth.logout()
</script>
```

### 5.4 工具函数

#### 格式化工具

```javascript
import { formatPrice, formatTime, formatTimeAgo, formatSales } from '@/utils/format'

formatPrice(99.9)      // "¥99.90"
formatTime(new Date())  // "2024-01-01 12:00:00"
formatTimeAgo(date)     // "1分钟前"
formatSales(15000)     // "1.5万+"
```

#### 防抖节流

```javascript
import { debounce, throttle } from '@/utils/debounce'

const handleSearch = debounce((keyword) => { ... }, 300)
const handleScroll = throttle(() => { ... }, 200)
```

#### 验证工具

```javascript
import { isPhone, isEmail, isPassword, isNotEmpty } from '@/utils/valid'

isPhone('13800138000')   // true
isEmail('test@example.com') // true
```

### 5.5 代码格式化

```bash
cd shop-aran
npm run format
```

### 5.6 安装依赖

```bash
cd shop-aran
npm install
```

### 5.7 启动前端

```bash
npm run dev
```

### 5.8 访问前端

浏览器打开 http://localhost:5173

---

## 📱 六、功能详解

### 6.1 用户模块

#### 用户注册
- 前端页面：[Register.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Register.vue)
- 后端接口：[UserController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/UserController.java)

#### 用户登录
- 前端页面：[Login.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Login.vue)

#### 用户中心
- 前端页面：[User.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/User.vue)

#### 收货地址管理
- 前端页面：[Address.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Address.vue)
- 后端接口：[UserAddressController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/UserAddressController.java)

### 6.2 商家模块

#### 商家注册/登录
- 前端页面：[SellerRegister.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerRegister.vue)、[SellerLogin.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerLogin.vue)

#### 商品管理
- 前端页面：[SellerGoods.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerGoods.vue)
- 功能：添加、编辑、上架/下架商品（仅显示商家自己的商品）

#### 订单管理 🆕
- 前端页面：[SellerOrder.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerOrder.vue)
- 功能：
  - 查看所有订单列表
  - 按状态筛选（全部/待发货/已发货/已完成）
  - 订单详情查看
  - **发货功能**（处理待发货订单）
  - 订单状态流转（待支付→待发货→已发货→已完成）

#### 售后处理
- 前端页面：[SellerAfterSale.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerAfterSale.vue)
- 功能：查看和处理售后申请

### 6.3 管理员模块

#### 分类管理
- 前端页面：[CategoryManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/CategoryManage.vue)
- 功能：支持树形分类结构，保护子分类

#### 商品管理 🆕
- 前端页面：[GoodsManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/GoodsManage.vue)
- 功能：
  - 商品列表展示（含商家信息：店铺名称、商家用户名）
  - 添加商品时选择商家
  - 编辑商品时可修改商家
  - 分类下拉选择
  - 状态管理

#### 轮播图管理
- 前端页面：[BannerManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/BannerManage.vue)

### 6.4 商品模块

#### 首页
- 前端页面：[Index.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Index.vue)
- 功能：分类导航、轮播图、商品列表

#### 商品详情
- 前端页面：[GoodsDetail.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/GoodsDetail.vue)
- 功能：下架商品禁用购买按钮，显示商家信息

#### 购物车
- 前端页面：[Cart.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Cart.vue)

### 6.5 订单模块

#### 订单管理
- 前端页面：[Order.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Order.vue)
- 功能：查看订单、申请售后、确认收货

#### 结算页面
- 前端页面：[Checkout.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Checkout.vue)

### 6.6 售后模块

#### 售后申请
- 前端页面：[AfterSale.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/AfterSale.vue)
- 功能：用户申请售后、查看售后记录

### 6.7 API 接口参考

#### 订单接口

##### 创建订单 `POST /order/create`

**请求格式**：
```json
{
  "userId": 1,
  "addressId": 1,
  "goodsList": [
    {
      "goodsId": 1,
      "quantity": 2
    }
  ]
}
```

**响应格式**：
```json
{
  "code": 200,
  "msg": "创建成功",
  "data": {
    "id": 1,
    "orderNo": "20240101120000123456",
    "userId": 1,
    "addressId": 1,
    "totalPrice": 199.98,
    "payPrice": 199.98,
    "payStatus": 0,
    "orderStatus": 0,
    "createTime": "2024-01-01 12:00:00"
  }
}
```

**OrderDTO 结构**：
```java
public class OrderDTO {
    private Integer userId;           // 用户ID
    private Integer addressId;         // 收货地址ID
    private List<GoodsItem> goodsList; // 商品列表
    
    // 用于订单详情响应
    private Order order;              // 订单信息
    private List<OrderItem> orderItems; // 订单项列表
    
    public static class GoodsItem {
        private Integer goodsId;      // 商品ID
        private Integer quantity;     // 购买数量
    }
}
```

---

## 🔧 七、开发流程

### 7.1 添加新功能步骤

#### 后端开发流程

1. **创建实体类** → `entity/` 目录
2. **创建 Mapper 接口** → `mapper/` 目录
3. **创建 Service 接口** → `service/` 目录
4. **创建 Service 实现** → `service/impl/` 目录
5. **创建 Controller** → `controller/` 目录（使用构造器注入）
6. **创建 DTO（如需要）** → `dto/` 目录
7. **添加拦截规则（如需要）** → `interceptor/LoginInterceptor.java`

#### 前端开发流程

1. **创建页面组件** → `views/` 目录
2. **配置路由** → `router/index.js`
3. **使用插件** → 通过 `inject` 使用插件系统
4. **统一使用 http 插件进行 API 调用** 🆕
5. **格式化代码** → `npm run format`

### 7.2 代码格式化

```bash
cd shop-aran
npm run format
```

---

## 📦 八、测试部署

### 8.1 测试账户

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 商家 | seller1 | 123456 |
| 用户 | user1 | 123456 |

### 8.2 功能测试清单

- [ ] 用户注册登录
- [ ] 用户收货地址管理
- [ ] 商家注册登录
- [ ] 商家商品管理（添加、编辑、下架、只能看到自己的商品）
- [ ] 商家订单管理（查看订单、发货）🆕
- [ ] 商家售后处理
- [ ] 管理员登录
- [ ] 管理员分类管理（树形结构）
- [ ] 管理员商品管理（显示商家信息）🆕
- [ ] 商品浏览（分类导航）
- [ ] 商品详情（下架状态禁用购买、显示商家信息）
- [ ] 购物车功能
- [ ] 下单购买
- [ ] 订单管理
- [ ] 售后申请和处理
- [ ] 后端接口拦截保护

### 8.3 后端打包

```bash
cd springboot
.\mvnw.cmd clean package
```

### 8.4 前端打包

```bash
cd shop-aran
npm run build
```

---

## 📚 九、常用命令

### 后端命令

```bash
cd springboot
.\mvnw.cmd clean compile    # 编译
.\mvnw.cmd spring-boot:run  # 运行
.\mvnw.cmd clean package    # 打包
```

### 前端命令

```bash
cd shop-aran
npm install    # 安装依赖
npm run dev    # 启动开发
npm run format # 格式化代码
npm run build  # 打包构建
```

---

## 📝 十、问题排查

### 10.1 常见问题

#### 前端代理错误 (ECONNREFUSED)
- 原因：后端服务未启动或端口配置错误
- 解决：启动后端服务，检查 `.env` 配置

#### 时间类型转换错误
- 原因：时间格式不一致
- 解决：已通过 `JacksonConfig` 统一配置

#### 商品详情页打不开
- 原因：缺少 `@GetMapping("/get/{id}")` 端点
- 解决：已添加到 `GoodsController`

#### 订单页不显示订单
- 原因：API 调用缺少 `userId` 参数
- 解决：已修复 API 调用

#### 管理员登录 500 错误
- 原因：数据库中存在多条匹配的管理员记录
- 解决：确保数据库中管理员用户名唯一

#### 商家看不到自己的商品
- 原因：商品未关联商家或查询逻辑错误
- 解决：检查 `seller_id` 字段是否正确关联

#### 商家无法发货
- 原因：缺少商家订单管理页面或发货接口
- 解决：已添加 SellerOrder.vue 和订单发货功能

#### 前端请求方式不统一
- 原因：部分页面使用原生 fetch，部分使用 http 插件
- 解决：已统一所有页面使用 http 插件

---

## 🎯 十一、开发最佳实践

### 11.1 代码规范
1. 遵循 RESTful API 设计
2. 使用环境变量管理配置（不要硬编码）
3. 使用构造函数注入代替 `@Autowired`
4. 使用 Lombok 简化代码
5. 代码提交前格式化
6. 使用常量类管理状态码和错误码
7. **统一使用 http 插件进行 API 调用** 🆕

### 11.2 安全实践
1. 敏感信息存储在 `.env` 文件
2. `.env` 文件添加到 `.gitignore`
3. 只提交 `.env.example` 作为模板
4. 不同环境使用不同的配置
5. 使用拦截器保护后端接口

---

## 📞 十二、项目文件速查表

| 文件 | 说明 |
|------|------|
| [database_init.sql](file:///c:/Users/Lenovo/Desktop/cxode/database_init.sql) | 数据库初始化脚本 |
| [springboot/pom.xml](file:///c:/Users/Lenovo/Desktop/cxode/springboot/pom.xml) | 后端依赖配置 |
| [springboot/src/main/java/com/mybatisplus/config/Constants.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/config/Constants.java) | 常量类（订单状态定义） |
| [springboot/src/main/java/com/mybatisplus/config/JacksonConfig.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/config/JacksonConfig.java) | 时间格式配置 |
| [springboot/src/main/java/com/mybatisplus/config/GlobalExceptionHandler.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/config/GlobalExceptionHandler.java) | 全局异常处理 |
| [springboot/src/main/java/com/mybatisplus/interceptor/LoginInterceptor.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/interceptor/LoginInterceptor.java) | 登录拦截器 |
| [springboot/src/main/java/com/mybatisplus/dto/GoodsWithSellerVO.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/dto/GoodsWithSellerVO.java) | 商品视图对象（含商家信息）🆕 |
| [shop-aran/package.json](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/package.json) | 前端依赖配置 |
| [shop-aran/src/router/index.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/router/index.js) | 路由配置 |
| [shop-aran/src/plugins/index.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/plugins/index.js) | 前端插件入口 |
| [shop-aran/src/views/seller/SellerOrder.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerOrder.vue) | 商家订单管理页面 🆕 |
| [shop-aran/src/views/admin/GoodsManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/GoodsManage.vue) | 管理员商品管理（含商家信息）🆕 |

---

## ✅ 十三、检查清单

### 开发前检查
- [ ] JDK 17+ 安装
- [ ] Node.js 20+ 安装
- [ ] MySQL 8.0+ 安装
- [ ] 数据库初始化（运行 database_init.sql）
- [ ] 后端 .env 文件配置
- [ ] 前端 .env 文件配置

### 开发中检查
- [ ] 后端启动成功
- [ ] 前端启动成功
- [ ] API 接口测试通过
- [ ] 用户功能测试
- [ ] 商家功能测试（只能看到自己的商品）
- [ ] 商家订单管理测试（发货功能）🆕
- [ ] 管理员功能测试（商品显示商家信息）🆕
- [ ] 售后功能测试
- [ ] 商品下架功能测试
- [ ] 订单状态流转测试（待支付→待发货→已发货→已完成）🆕
- [ ] 后端接口拦截测试
- [ ] 代码格式化通过
- [ ] 前端统一使用 http 插件 🆕

### 部署前检查
- [ ] 代码格式化完成
- [ ] 所有测试通过
- [ ] 文档更新
- [ ] 配置检查
- [ ] 敏感信息未提交

---

**祝您开发顺利！🎉**
