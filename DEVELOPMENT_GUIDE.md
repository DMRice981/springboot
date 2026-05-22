# Aran Shop 项目完整开发指南

## 📋 目录

1. [环境准备
2. [数据库配置
3. [后端开发
4. [前端开发
5. [功能开发
6. [测试部署

---

## 🏗️ 一、环境准备

### 1.1 必备工具安装

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

## 🗄️ 二、数据库配置

### 2.1 数据库创建

```sql
-- 创建数据库
CREATE DATABASE shop_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE shop_mall;
```

### 2.2 初始化数据库表结构

项目已实现的表：

#### 用户表 (user)
```sql
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status INT DEFAULT 1,
    is_delete INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 商家表 (seller)
```sql
CREATE TABLE seller (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    shop_name VARCHAR(100),
    phone VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 管理员表 (admin)
```sql
CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    admin_name VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 商品分类表 (category)
```sql
CREATE TABLE category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 商品表 (goods)
```sql
CREATE TABLE goods (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT,
    goods_name VARCHAR(100) NOT NULL,
    goods_img VARCHAR(255),
    price DECIMAL(10,2),
    market_price DECIMAL(10,2),
    stock INT DEFAULT 0,
    sales INT DEFAULT 0,
    goods_desc TEXT,
    status INT DEFAULT 1,
    is_delete INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    seller_id INT
);
```

#### 购物车表 (cart)
```sql
CREATE TABLE cart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    goods_id INT,
    num INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 订单表 (`order`)
```sql
CREATE TABLE `order` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) UNIQUE,
    user_id INT,
    address_id INT,
    total_price DECIMAL(10,2),
    pay_price DECIMAL(10,2),
    pay_status INT DEFAULT 0,
    order_status INT DEFAULT 0,
    pay_time DATETIME,
    send_time DATETIME,
    confirm_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 订单项表 (order_item)
```sql
CREATE TABLE order_item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    goods_id INT,
    goods_name VARCHAR(100),
    goods_img VARCHAR(255),
    price DECIMAL(10,2),
    num INT,
    total_price DECIMAL(10,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 用户地址表 (user_address)
```sql
CREATE TABLE user_address (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    receiver_name VARCHAR(50),
    receiver_phone VARCHAR(20),
    receiver_address VARCHAR(255),
    is_default INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 商品评论表 (goods_comment)
```sql
CREATE TABLE goods_comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    goods_id INT,
    content TEXT,
    rating INT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 轮播图表 (banner)
```sql
CREATE TABLE banner (
    id INT PRIMARY KEY AUTO_INCREMENT,
    img_url VARCHAR(255),
    link_url VARCHAR(255),
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 售后表 (after_sale)
```sql
CREATE TABLE after_sale (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    user_id INT,
    seller_id INT,
    goods_id INT,
    reason TEXT,
    status INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 2.3 插入测试数据

```sql
-- 插入测试管理员
INSERT INTO admin (admin_name, password) VALUES 
VALUES ('admin', '123456');

-- 插入测试商家
INSERT INTO seller (username, password, shop_name) VALUES 
('seller1', '123456', '测试店铺');

-- 插入测试用户
INSERT INTO user (username, password, phone) VALUES 
('user1', '123456', '13800138000');

-- 插入测试分类
INSERT INTO category (category_name) VALUES 
('电子产品'), ('服装'), ('食品'), ('家居');

-- 插入测试商品
INSERT INTO goods (category_id, goods_name, price, market_price, stock, sales, goods_desc, seller_id) VALUES 
(1, '智能手机', 2999.00, 3999.00, 100, 0, '高性能智能手机', 1),
(2, '时尚T恤', 99.00, 199.00, 200, 0, '舒适透气T恤', 1),
(3, '进口零食', 59.00, 99.00, 150, 0, '美味进口零食', 1);
```

---

## 🚀 三、后端开发

### 3.1 项目结构说明

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
└── SpringbootApplication.java  # 启动类
```

### 3.2 配置环境变量

#### 步骤 1: 复制配置文件

```bash
cd springboot
copy .env.example .env
```

#### 步骤 2: 修改 .env 配置

编辑 `springboot/.env` 文件：

```env
SERVER_PORT=8081
DB_HOST=localhost
DB_PORT=3306
DB_NAME=shop_mall
DB_USERNAME=root
DB_PASSWORD=你的MySQL密码
DB_USE_UNICODE=true
DB_CHARACTER_ENCODING=utf8
DB_SERVER_TIMEZONE=GMT%2B8
DB_USE_SSL=false
```

### 3.3 后端启动

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

### 3.4 验证后端

测试接口

访问 http://localhost:8081/api/goods/list

---

## 🎨 四、前端开发

### 4.1 项目结构说明

```
shop-aran/src/
├── api/               # API 接口
├── assets/            # 资源文件
├── components/       # 组件
├── router/          # 路由配置
├── utils/           # 工具类
├── views/          # 页面
├── App.vue        # 根组件
└── main.js         # 入口文件
```

### 4.2 配置环境变量

#### 步骤 1: 复制配置文件

```bash
cd shop-aran
copy .env.example .env
```

#### 步骤 2: 修改 .env 配置

编辑 `shop-aran/.env` 文件：

```env
VITE_API_BASE_URL=http://localhost:8081
VITE_API_PREFIX=/api
VITE_REQUEST_TIMEOUT=5000
```

### 4.3 安装依赖

```bash
cd shop-aran
npm install
```

### 4.4 启动前端

```bash
npm run dev
```

### 4.5 访问前端

浏览器打开 http://localhost:5173

---

## 📱 五、功能开发

### 5.1 用户模块

#### 用户注册
1. 前端：[Register.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Register.vue)
2. 后端：[UserController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/UserController.java)

#### 用户登录
1. 前端：[Login.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Login.vue)

#### 用户中心
1. 前端：[User.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/User.vue)

### 5.2 商家模块

#### 商家注册
1. 前端：[SellerRegister.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerRegister.vue)

#### 商家登录
1. 前端：[SellerLogin.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerLogin.vue)

#### 商家中心
1. 前端：[SellerIndex.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerIndex.vue)

#### 商品管理
1. 前端：[SellerGoods.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerGoods.vue)
2. 后端：[GoodsController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/GoodsController.java)

#### 售后管理
1. 前端：[SellerAfterSale.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/seller/SellerAfterSale.vue)
2. 后端：[AfterSaleController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/AfterSaleController.java)

### 5.3 管理员模块

#### 管理员登录
1. 前端：[AdminLogin.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/AdminLogin.vue)

#### 管理后台
1. 前端：[AdminIndex.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/admin/AdminIndex.vue)

### 5.4 商品模块

#### 商品列表
1. 前端：[Index.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Index.vue)

#### 商品详情
1. 前端：[GoodsDetail.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/GoodsDetail.vue)

#### 购物车
1. 前端：[Cart.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Cart.vue)
2. 后端：[CartController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/CartController.java)

### 5.5 订单模块

#### 订单管理
1. 前端：[Order.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Order.vue)
2. 后端：[OrderController.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/controller/OrderController.java)

#### 结算页面
1. 前端：[Checkout.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/Checkout.vue)

### 5.6 售后模块

#### 售后申请
1. 前端：[AfterSale.vue](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/views/AfterSale.vue)

---

## 🔧 六、开发流程

### 6.1 添加新功能步骤

#### 后端开发流程

1. **创建实体类
   - 在 `entity/` 目录创建实体类
   - 使用 `@TableName` 注解
   - 使用 Lombok 注解简化代码

2. **创建 Mapper 接口
   - 在 `mapper/` 目录创建
   - 继承 `BaseMapper<实体>`

3. **创建 Service 接口
   - 在 `service/` 目录创建
   - 定义业务方法

4. **创建 Service 实现
   - 在 `service/impl/` 目录创建
   - 继承 `ServiceImpl<Mapper, Entity>`
   - 实现业务逻辑

5. **创建 Controller
   - 在 `controller/` 目录创建
   - 编写 RESTful 接口

#### 前端开发流程

1. **创建 API 文件
   - 在 `api/` 目录创建
   - 使用 request 封装接口

2. **创建页面组件
   - 在 `views/` 目录创建
   - 使用 Vue 3 Composition API

3. **配置路由
   - 在 `router/index.js` 配置

### 6.2 代码格式化

```bash
cd shop-aran
npm run format
```

---

## 📦 七、测试

### 7.1 后端测试

#### 用户注册测试

1. 启动后端
2. 访问 http://localhost:8081/api/goods/list
3. 使用 Postman 或浏览器测试

### 7.2 前端测试

#### 注册测试用户
- 用户名：user1
- 密码：123456

#### 注册测试商家
- 用户名：seller1
- 密码：123456

#### 登录测试管理员
- 用户名：admin
- 密码：123456

### 7.3 功能测试清单

- [ ] 用户注册登录
- [ ] 商家注册登录
- [ ] 商品浏览
- [ ] 商品详情
- [ ] 购物车功能
- [ ] 下单购买
- [ ] 商家管理商品
- [ ] 管理员后台

---

## 🚀 八、部署

### 8.1 后端打包

```bash
cd springboot
.\mvnw.cmd clean package
```

### 8.2 前端打包

```bash
cd shop-aran
npm run build
```

### 8.3 生产部署

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

### 常见问题

#### 后端启动失败

1. 检查数据库连接配置
2. 检查 .env 文件配置
3. 确认数据库已启动
4. 确认数据库表已创建

#### 前端启动失败

1. 检查 Node.js 版本
2. 删除 node_modules 重新安装
3. 检查 .env 文件配置
4. 确认后端已启动

#### 数据库连接失败

1. 确认 MySQL 服务已启动
2. 检查数据库用户名密码
3. 确认数据库名称正确
4. 检查端口 3306 是否被占用

---

## 🎯 十一、开发最佳实践

### 代码规范

1. 遵循 RESTful API 设计
2. 使用环境变量管理配置
3. 代码提交前格式化
4. 注释清晰

### Git 规范

1. 分支命名
2. 提交信息规范
3. 代码审查

---

## 📞 十二、项目文件速查表

| 文件 | 说明 |
|------|------|
| [CONFIGURATION.md](file:///c:/Users/Lenovo/Desktop/cxode/CONFIGURATION.md) | 配置指南 |
| [springboot/pom.xml](file:///c:/Users/Lenovo/Desktop/cxode/springboot/pom.xml) | 后端依赖 |
| [shop-aran/package.json](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/package.json) | 前端依赖 |
| [springboot/.env.example](file:///c:/Users/Lenovo/Desktop/cxode/springboot/.env.example) | 后端配置模板 |
| [shop-aran/.env.example](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/.env.example) | 前端配置模板 |

---

## ✅ 十三、检查清单

### 开发前检查
- [ ] JDK 17+ 安装
- [ ] Node.js 20+ 安装
- [ ] MySQL 8.0+ 安装
- [ ] 数据库创建
- [ ] 数据库表创建
- [ ] 测试数据插入
- [ ] 环境变量配置

### 开发中检查
- [ ] 后端启动成功
- [ ] 前端启动成功
- [ ] API 接口测试
- [ ] 功能测试

### 部署前检查
- [ ] 代码格式化
- [ ] 测试通过
- [ ] 文档更新
- [ ] 配置检查

---

**祝您开发顺利！🎉
