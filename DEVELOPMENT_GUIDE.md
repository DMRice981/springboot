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
- **商家**：管理商品、处理售后
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
│   │   │   │       ├── config/       # 配置类（JacksonConfig, DotenvConfig）
│   │   │   │       ├── controller/   # 控制器（构造器注入）
│   │   │   │       ├── entity/       # 实体类
│   │   │   │       ├── mapper/       # 数据访问层
│   │   │   │       ├── service/      # 服务层
│   │   │   │       │   └── impl/     # 服务实现
│   │   │   │       ├── dto/          # 数据传输对象
│   │   │   │       └── SpringbootApplication.java
│   │   │   └── resources/
│   │   │       └── application.yml   # 配置文件
│   │   └── test/
│   ├── .env                      # 环境变量（不提交）
│   ├── .env.example              # 环境变量模板
│   └── pom.xml
│
├── shop-aran/                # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── assets/            # 资源文件
│   │   ├── components/       # 组件
│   │   ├── router/          # 路由配置
│   │   ├── utils/           # 工具类（request.js）
│   │   ├── views/          # 页面
│   │   │   ├── admin/      # 管理员页面
│   │   │   └── seller/     # 商家页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── public/
│   ├── .env                      # 环境变量（不提交）
│   ├── .env.example              # 环境变量模板
│   ├── vite.config.js
│   └── package.json
│
└── database_init.sql           # 数据库初始化脚本（唯一）
```

### 1.4 最新更新动态

| 更新内容 | 说明 | 状态 |
|---------|------|------|
| 构造器注入 | 所有 Controller 从 `@Autowired` 改为 `@RequiredArgsConstructor` | ✅ 已完成 |
| 常量类 | 创建 Constants.java 统一管理状态码、错误码等 | ✅ 已完成 |
| 时间格式统一 | 创建 JacksonConfig.java，统一时间格式为 `yyyy-MM-dd HH:mm:ss` | ✅ 已完成 |
| 数据库文件清理 | 删除冗余的 init-db.sql，保留 database_init.sql | ✅ 已完成 |
| 分类功能完善 | 支持树形分类结构（pid 字段） | ✅ 已完成 |
| 售后功能完善 | 完整的售后申请和处理流程 | ✅ 已完成 |
| 商品下架功能 | 下架商品禁用购买按钮 | ✅ 已完成 |
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
    order_status INT DEFAULT 0 COMMENT '订单状态：0待付款 1待发货 2已发货 3已完成 4已取消',
    pay_time DATETIME COMMENT '支付时间',
    send_time DATETIME COMMENT '发货时间',
    confirm_time DATETIME COMMENT '确认收货时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '订单表';
```

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
- 分类：电子产品、服装、食品、家居
- 商品：4个测试商品
- 轮播图：2张测试轮播图
- 收货地址：1条测试地址

---

## 🚀 四、后端开发

### 4.1 项目结构说明

```
springboot/src/main/java/com/mybatisplus/
├── config/
│   ├── DotenvConfig.java          # 环境变量配置
│   ├── JacksonConfig.java         # 统一时间格式配置
│   └── Constants.java             # 常量类（状态码、错误码等）
├── controller/                  # 控制器层（构造器注入）
├── entity/                    # 实体类（含 @JsonFormat 注解）
├── mapper/                   # 数据访问层
├── service/                  # 服务层
│   └── impl/               # 服务实现
├── dto/                     # 数据传输对象
│   ├── OrderDTO.java
│   └── AfterSaleDTO.java
└── SpringbootApplication.java  # 启动类
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
    public static final int SUCCESS = 200;
    public static final int ERROR = 500;
    public static final int ORDER_STATUS_PENDING = 0;
    public static final int ORDER_STATUS_SHIPPED = 1;
    // ...
}
```

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
├── components/       # 组件
├── router/          # 路由配置
├── utils/           # 工具类
│   └── request.js   # Axios 封装（含拦截器）
├── views/          # 页面
│   ├── admin/      # 管理员页面
│   │   ├── AdminIndex.vue
│   │   ├── AdminLogin.vue
│   │   ├── BannerManage.vue
│   │   ├── CategoryManage.vue   # 分类管理（支持树形结构）
│   │   ├── GoodsManage.vue      # 商品管理（含分类下拉）
│   │   ├── OrderManage.vue
│   │   └── UserManage.vue
│   ├── seller/     # 商家页面
│   │   ├── SellerAfterSale.vue  # 售后处理
│   │   ├── SellerGoods.vue      # 商品管理
│   │   ├── SellerIndex.vue
│   │   ├── SellerLogin.vue
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
├── App.vue        # 根组件
└── main.js         # 入口文件
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

### 5.3 代码格式化

```bash
cd shop-aran
npm run format
```

### 5.4 安装依赖

```bash
cd shop-aran
npm install
```

### 5.5 启动前端

```bash
npm run dev
```

### 5.6 访问前端

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
- 功能：添加、编辑、上架/下架商品

#### 售后处理
- 前端页面：[SellerAfterSale.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerAfterSale.vue)
- 功能：查看和处理售后申请

### 6.3 管理员模块

#### 分类管理
- 前端页面：[CategoryManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/CategoryManage.vue)
- 功能：支持树形分类结构，保护子分类

#### 商品管理
- 前端页面：[GoodsManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/GoodsManage.vue)
- 功能：含分类下拉选择、状态管理

#### 轮播图管理
- 前端页面：[BannerManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/BannerManage.vue)

### 6.4 商品模块

#### 首页
- 前端页面：[Index.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Index.vue)
- 功能：分类导航、轮播图、商品列表

#### 商品详情
- 前端页面：[GoodsDetail.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/GoodsDetail.vue)
- 功能：下架商品禁用购买按钮

#### 购物车
- 前端页面：[Cart.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Cart.vue)

### 6.5 订单模块

#### 订单管理
- 前端页面：[Order.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Order.vue)
- 功能：查看订单、申请售后

#### 结算页面
- 前端页面：[Checkout.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Checkout.vue)

### 6.6 售后模块

#### 售后申请
- 前端页面：[AfterSale.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/AfterSale.vue)
- 功能：用户申请售后、查看售后记录

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

#### 前端开发流程

1. **创建 API 文件** → `api/` 目录
2. **创建页面组件** → `views/` 目录
3. **配置路由** → `router/index.js`
4. **格式化代码** → `npm run format`

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
- [ ] 商家商品管理（添加、编辑、下架）
- [ ] 商家售后处理
- [ ] 管理员登录
- [ ] 管理员分类管理（树形结构）
- [ ] 商品浏览（分类导航）
- [ ] 商品详情（下架状态禁用购买）
- [ ] 购物车功能
- [ ] 下单购买
- [ ] 订单管理
- [ ] 售后申请和处理

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

---

## 🎯 十一、开发最佳实践

### 11.1 代码规范
1. 遵循 RESTful API 设计
2. 使用环境变量管理配置（不要硬编码）
3. 使用构造函数注入代替 `@Autowired`
4. 使用 Lombok 简化代码
5. 代码提交前格式化
6. 使用常量类管理状态码和错误码

### 11.2 安全实践
1. 敏感信息存储在 `.env` 文件
2. `.env` 文件添加到 `.gitignore`
3. 只提交 `.env.example` 作为模板
4. 不同环境使用不同的配置

---

## 📞 十二、项目文件速查表

| 文件 | 说明 |
|------|------|
| [database_init.sql](file:///c:/Users/Lenovo/Desktop/cxode/database_init.sql) | 数据库初始化脚本 |
| [springboot/pom.xml](file:///c:/Users/Lenovo/Desktop/cxode/springboot/pom.xml) | 后端依赖配置 |
| [springboot/src/main/java/com/mybatisplus/config/Constants.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/config/Constants.java) | 常量类 |
| [springboot/src/main/java/com/mybatisplus/config/JacksonConfig.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/config/JacksonConfig.java) | 时间格式配置 |
| [shop-aran/package.json](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/package.json) | 前端依赖配置 |
| [shop-aran/src/router/index.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/router/index.js) | 路由配置 |

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
- [ ] 商家功能测试
- [ ] 管理员功能测试
- [ ] 售后功能测试
- [ ] 商品下架功能测试
- [ ] 代码格式化通过

### 部署前检查
- [ ] 代码格式化完成
- [ ] 所有测试通过
- [ ] 文档更新
- [ ] 配置检查
- [ ] 敏感信息未提交

---

**祝您开发顺利！🎉**