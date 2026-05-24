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
│   │   │   │       ├── config/       # 配置类
│   │   │   │       ├── controller/   # 控制器
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
└── shop-aran/                # 前端项目
    ├── src/
    │   ├── api/               # API 接口
    │   ├── assets/            # 资源文件
    │   ├── components/       # 组件
    │   ├── router/          # 路由配置
    │   ├── utils/           # 工具类
    │   ├── views/          # 页面
    │   │   ├── admin/      # 管理员页面
    │   │   └── seller/     # 商家页面
    │   ├── App.vue
    │   └── main.js
    ├── public/
    ├── .env                      # 环境变量（不提交）
    ├── .env.example              # 环境变量模板
    ├── vite.config.js
    └── package.json
```

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

### 3.1 数据库创建

```sql
-- 创建数据库
CREATE DATABASE shop_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE shop_mall;
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

#### 商品分类表 (category)

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
                     ┌─────────────┐
                     │    goods    │
                     └─────────────┘
                            │ 1:N
                            ▼
                     ┌─────────────┐
                     │  goods_img   │
                     └─────────────┘

┌─────────────┐
│   seller    │───────┐
└─────────────┘       │ 1:N
                      ▼
┌─────────────┐      │
│    goods    │◄─────┘
└─────────────┘

┌─────────────┐
│   admin     │
└─────────────┘

┌─────────────┐
│  category   │
└─────────────┘

┌─────────────┐
│    banner   │
└─────────────┘

┌─────────────┐      ┌─────────────┐
│goods_comment│◄─────│    goods    │
└─────────────┘      └─────────────┘

┌─────────────┐      ┌─────────────┐
│  after_sale │◄─────│    order    │
└─────────────┘      └─────────────┘
```

### 3.4 插入测试数据

```sql
-- 插入测试管理员
INSERT INTO admin (admin_name, password, nickname) VALUES ('admin', '123456', '超级管理员');

-- 插入测试商家
INSERT INTO seller (username, password, shop_name, phone) VALUES ('seller1', '123456', '测试店铺', '13800138001');

-- 插入测试用户
INSERT INTO user (username, password, phone, email) VALUES ('user1', '123456', '13800138000', 'user1@example.com');

-- 插入测试分类
INSERT INTO category (name, pid, sort) VALUES 
('电子产品', 0, 1),
('服装', 0, 2),
('食品', 0, 3),
('家居', 0, 4);

-- 插入测试商品
INSERT INTO goods (category_id, goods_name, goods_img, price, market_price, stock, sales, goods_desc, seller_id) VALUES 
(1, '智能手机', 'https://picsum.photos/400/400?1', 2999.00, 3999.00, 100, 0, '高性能智能手机', 1),
(2, '时尚T恤', 'https://picsum.photos/400/400?2', 99.00, 199.00, 200, 0, '舒适透气T恤', 1),
(3, '进口零食', 'https://picsum.photos/400/400?3', 59.00, 99.00, 150, 0, '美味进口零食', 1);

-- 插入轮播图
INSERT INTO banner (img_url, link_url, sort) VALUES 
('https://picsum.photos/1200/400?1', '/', 1),
('https://picsum.photos/1200/400?2', '/', 2);

-- 插入测试收货地址
INSERT INTO user_address (user_id, name, phone, province, city, district, detail, is_default) VALUES 
(1, '张三', '13800138000', '广东省', '深圳市', '南山区', '科技园1号', 1);
```

### 3.5 数据库触发器

项目已配置数据库触发器用于自动处理业务逻辑，详见 [database_triggers.sql](file:///c:/Users/Lenovo/Desktop/cxode/database_triggers.sql)。

**主要触发器功能：**

1. **订单支付后自动扣减库存并增加销量** - 当订单支付状态变更时自动更新商品库存和销量
2. **订单状态时间自动记录** - 自动设置支付时间、发货时间、确认收货时间
3. **时间戳自动管理** - 自动管理订单、商品、购物车等表的时间戳字段

---

## 🚀 四、后端开发

### 4.1 项目结构说明

```
springboot/src/main/java/com/mybatisplus/
├── config/
│   └── DotenvConfig.java          # 环境变量配置
├── controller/                  # 控制器层
├── entity/                    # 实体类
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

### 4.3 后端架构说明

#### 依赖注入改进

所有 Controller 已从 `@Autowired` 字段注入重构为构造函数注入，使用 Lombok 的 `@RequiredArgsConstructor` 注解：

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

#### DTO 数据传输对象

- `OrderDTO`：订单数据传输对象
- `AfterSaleDTO`：售后数据传输对象（包含商品信息）

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

测试接口

访问 http://localhost:8081/api/goods/list

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
│   └── request.js
├── views/          # 页面
│   ├── admin/      # 管理员页面
│   │   ├── AdminIndex.vue
│   │   ├── AdminLogin.vue
│   │   ├── BannerManage.vue
│   │   ├── CategoryManage.vue
│   │   ├── GoodsManage.vue
│   │   ├── OrderManage.vue
│   │   └── UserManage.vue
│   ├── seller/     # 商家页面
│   │   ├── SellerAfterSale.vue
│   │   ├── SellerGoods.vue
│   │   ├── SellerIndex.vue
│   │   ├── SellerLogin.vue
│   │   └── SellerRegister.vue
│   ├── Address.vue
│   ├── AfterSale.vue
│   ├── Cart.vue
│   ├── Checkout.vue
│   ├── Comment.vue
│   ├── GoodsDetail.vue
│   ├── Index.vue
│   ├── Login.vue
│   ├── Order.vue
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

项目已配置 Prettier 代码格式化：

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
- 功能：用户注册账号

#### 用户登录
- 前端页面：[Login.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Login.vue)
- 功能：用户登录验证

#### 用户中心
- 前端页面：[User.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/User.vue)
- 功能：查看个人信息、收货地址管理

#### 收货地址管理
- 前端页面：[Address.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Address.vue)
- 后端接口：[UserAddressController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/UserAddressController.java)
- 功能：添加、编辑、删除收货地址，设置默认地址

### 6.2 商家模块

#### 商家注册
- 前端页面：[SellerRegister.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerRegister.vue)
- 后端接口：[SellerController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/SellerController.java)

#### 商家登录
- 前端页面：[SellerLogin.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerLogin.vue)

#### 商家中心
- 前端页面：[SellerIndex.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerIndex.vue)

#### 商品管理
- 前端页面：[SellerGoods.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerGoods.vue)
- 后端接口：[GoodsController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/GoodsController.java)
- 功能：添加商品、编辑商品、上架/下架商品、删除商品、查看商品列表

#### 售后管理
- 前端页面：[SellerAfterSale.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerAfterSale.vue)
- 后端接口：[AfterSaleController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/AfterSaleController.java)
- 功能：查看售后申请列表、处理售后申请

### 6.3 管理员模块

#### 管理员登录
- 前端页面：[AdminLogin.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/AdminLogin.vue)
- 后端接口：[AdminController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/AdminController.java)

#### 管理后台
- 前端页面：[AdminIndex.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/AdminIndex.vue)
- 功能：平台总览

#### 用户管理
- 前端页面：[UserManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/UserManage.vue)
- 功能：查看用户列表、管理用户

#### 商品管理
- 前端页面：[GoodsManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/GoodsManage.vue)
- 功能：平台商品审核、管理

#### 订单管理
- 前端页面：[OrderManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/OrderManage.vue)
- 功能：查看平台订单列表

#### 分类管理
- 前端页面：[CategoryManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/CategoryManage.vue)
- 后端接口：[CategoryController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/CategoryController.java)
- 功能：添加、编辑、删除商品分类

#### 轮播图管理
- 前端页面：[BannerManage.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/BannerManage.vue)
- 后端接口：[BannerController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/BannerController.java)
- 功能：添加、编辑、删除首页轮播图

### 6.4 商品模块

#### 商品列表
- 前端页面：[Index.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Index.vue)
- 功能：展示所有商品、商品卡片、轮播图

#### 商品详情
- 前端页面：[GoodsDetail.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/GoodsDetail.vue)
- 功能：查看商品详情、库存状态、购买按钮
- 特性：商品下架时显示提示，禁用购买按钮

#### 购物车
- 前端页面：[Cart.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Cart.vue)
- 后端接口：[CartController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/CartController.java)
- 功能：添加商品到购物车、修改数量、删除商品、结算

#### 商品评论
- 前端页面：[Comment.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Comment.vue)
- 后端接口：[GoodsCommentController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/GoodsCommentController.java)

### 6.5 订单模块

#### 订单管理
- 前端页面：[Order.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Order.vue)
- 后端接口：[OrderController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/OrderController.java)
- 功能：查看订单列表、申请售后

#### 结算页面
- 前端页面：[Checkout.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Checkout.vue)
- 功能：选择收货地址、确认订单、提交订单

### 6.6 售后模块

#### 售后申请
- 前端页面：[AfterSale.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/AfterSale.vue)
- 功能：用户申请售后、查看售后记录
- 特性：从订单页面发起售后申请，选择订单商品

---

## 🔧 七、开发流程

### 7.1 添加新功能步骤

#### 后端开发流程

1. **创建实体类**
   - 在 `entity/` 目录创建实体类
   - 使用 `@TableName` 注解指定表名
   - 使用 Lombok 的 `@Data`、`@AllArgsConstructor`、`@NoArgsConstructor` 注解
   - 使用 `@TableId(type = IdType.AUTO)` 注解主键

2. **创建 Mapper 接口**
   - 在 `mapper/` 目录创建
   - 继承 `BaseMapper<实体>`

3. **创建 Service 接口**
   - 在 `service/` 目录创建
   - 继承 `IService<实体>`
   - 定义业务方法（可选）

4. **创建 Service 实现**
   - 在 `service/impl/` 目录创建
   - 继承 `ServiceImpl<Mapper, Entity>`
   - 实现 Service 接口
   - 添加 `@Service` 注解

5. **创建 Controller**
   - 在 `controller/` 目录创建
   - 添加 `@RestController` 和 `@RequestMapping` 注解
   - 使用 `@RequiredArgsConstructor` 实现构造函数注入
   - 编写 RESTful 接口

6. **创建 DTO（如需要）**
   - 在 `dto/` 目录创建数据传输对象
   - 用于组合多个实体的数据返回

#### 前端开发流程

1. **创建 API 文件**
   - 在 `api/` 目录创建
   - 使用 request 封装接口调用

2. **创建页面组件**
   - 在 `views/` 目录创建
   - 使用 Vue 3 Composition API
   - 使用 Element Plus 组件库

3. **配置路由**
   - 在 `router/index.js` 配置路由
   - 设置路由守卫（如需要）

4. **格式化代码**
   - 运行 `npm run format`

### 7.2 代码格式化

```bash
cd shop-aran
npm run format
```

---

## 📦 八、测试部署

### 8.1 后端测试

#### 用户注册测试

1. 启动后端
2. 访问 http://localhost:8081/api/goods/list
3. 使用 Postman 或浏览器测试

### 8.2 前端测试

#### 注册测试用户
- 用户名：user1
- 密码：123456

#### 注册测试商家
- 用户名：seller1
- 密码：123456

#### 登录测试管理员
- 用户名：admin
- 密码：123456

### 8.3 功能测试清单

- [ ] 用户注册登录
- [ ] 用户收货地址管理
- [ ] 商家注册登录
- [ ] 商家商品管理（添加、编辑、下架）
- [ ] 商家售后处理
- [ ] 管理员登录
- [ ] 管理员后台功能（用户、商品、订单、分类、轮播图）
- [ ] 商品浏览
- [ ] 商品详情（包含下架状态）
- [ ] 购物车功能
- [ ] 下单购买
- [ ] 订单管理
- [ ] 售后申请和处理

### 8.4 后端打包

```bash
cd springboot
.\mvnw.cmd clean package
```

### 8.5 前端打包

```bash
cd shop-aran
npm run build
```

### 8.6 生产部署

1. 修改环境变量配置
2. 配置生产数据库
3. 配置 Nginx 反向代理
4. 配置 HTTPS

---

## 📚 九、常用命令

### 后端命令

```bash
# 进入后端目录
cd springboot

# 编译项目
.\mvnw.cmd clean compile

# 运行项目
.\mvnw.cmd spring-boot:run

# 打包项目
.\mvnw.cmd clean package
```

### 前端命令

```bash
# 进入前端目录
cd shop-aran

# 安装依赖
npm install

# 启动开发
npm run dev

# 格式化代码
npm run format

# 打包构建
npm run build
```

---

## 📝 十、问题排查

### 10.1 常见问题

#### 后端启动失败

1. 检查数据库连接配置
2. 检查 .env 文件配置
3. 确认数据库已启动
4. 确认数据库表已创建
5. 检查端口 8081 是否被占用

#### 前端启动失败

1. 检查 Node.js 版本
2. 删除 node_modules 重新安装
3. 检查 .env 文件配置
4. 确认后端已启动
5. 检查端口 5173 是否被占用

#### 数据库连接失败

1. 确认 MySQL 服务已启动
2. 检查数据库用户名密码
3. 确认数据库名称正确
4. 检查端口 3306 是否被占用

#### 前端代理错误

- 问题：`ECONNREFUSED` when trying to connect to backend
- 解决：确保后端已启动在 8081 端口，检查 .env 配置

#### 时间类型转换错误

- 问题：`LocalDateTime` 无法转换为 `Date`
- 解决：确保所有实体类时间字段使用 `java.time.LocalDateTime`（已修复）

---

## 🎯 十一、开发最佳实践

### 11.1 代码规范

1. 遵循 RESTful API 设计
2. 使用环境变量管理配置（不要硬编码）
3. 使用构造函数注入代替 `@Autowired`
4. 使用 Lombok 简化代码
5. 代码提交前格式化
6. 注释清晰

### 11.2 Git 规范

1. 分支命名
2. 提交信息规范
3. 代码审查
4. **永远不要提交 .env 文件**

### 11.3 安全实践

1. 数据库密码等敏感信息存储在 .env 文件
2. .env 文件添加到 .gitignore
3. 只提交 .env.example 作为模板
4. 不同环境使用不同的 .env 文件
5. 定期轮换敏感信息

---

## 📞 十二、项目文件速查表

| 文件 | 说明 |
|------|------|
| [CONFIGURATION.md](file:///c:/Users/Lenovo/Desktop/cxode/CONFIGURATION.md) | 配置指南 |
| [DEVELOPMENT_GUIDE.md](file:///c:/Users/Lenovo/Desktop/cxode/DEVELOPMENT_GUIDE.md) | 本文档 |
| [database_triggers.sql](file:///c:/Users/Lenovo/Desktop/cxode/database_triggers.sql) | 数据库触发器 |
| [springboot/pom.xml](file:///c:/Users/Lenovo/Desktop/cxode/springboot/pom.xml) | 后端依赖配置 |
| [shop-aran/package.json](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/package.json) | 前端依赖配置 |
| [springboot/.env.example](file:///c:/Users/Lenovo/Desktop/cxode/springboot/.env.example) | 后端配置模板 |
| [shop-aran/.env.example](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/.env.example) | 前端配置模板 |
| [shop-aran/src/router/index.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/router/index.js) | 路由配置 |

---

## ✅ 十三、检查清单

### 开发前检查
- [ ] JDK 17+ 安装
- [ ] Node.js 20+ 安装
- [ ] MySQL 8.0+ 安装
- [ ] 数据库创建
- [ ] 数据库表创建
- [ ] 数据库触发器创建
- [ ] 测试数据插入
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
- [ ] 生产环境配置准备

---

**祝您开发顺利！🎉**
