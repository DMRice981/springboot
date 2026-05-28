
# Aran Shop 电商系统 - 数据库原理及应用课程设计

## 项目简介

Aran Shop 是一个功能完善的电商平台，采用前后端分离架构开发。本项目作为数据库原理及应用课程设计，深入实践了数据库设计、SQL优化、事务处理、触发器等核心技术。

### 技术栈

**后端：**
- Spring Boot 3.2.5
- MyBatis Plus 3.5.6
- MySQL 8.0
- Lombok
- Dotenv

**前端：**
- Vue 3
- Vite
- Element Plus
- Vue Router
- Axios

## 功能特性

### 用户端
- 用户注册与登录
- 商品浏览与搜索
- 购物车管理
- 订单管理（创建、支付、发货、收货、取消）
- 收货地址管理
- 商品评论
- 售后申请

### 商家端
- 商家注册与登录
- 商品管理（上架、下架、编辑）
- 订单处理
- 售后处理

### 管理员端
- 分类管理（树形结构）
- 商品管理
- 用户管理
- 订单管理
- 轮播图管理

## 数据库设计

### 核心数据表
1. **user** - 用户表
2. **seller** - 商家表
3. **admin** - 管理员表
4. **goods** - 商品表
5. **category** - 商品分类表
6. **order** - 订单表
7. **order_item** - 订单项表
8. **cart** - 购物车表
9. **user_address** - 用户收货地址表
10. **goods_comment** - 商品评论表
11. **banner** - 轮播图表
12. **after_sale** - 售后表

### 数据库特性
- 使用触发器自动管理库存和销量
- 软删除机制保证数据可追溯
- 索引优化提升查询性能
- 事务保证数据一致性

## 快速开始

### 环境要求
- JDK 17+
- Node.js 20.10+
- MySQL 8.0+
- Maven 3.6+

### 1. 数据库初始化

```bash
# 创建数据库并导入初始数据
mysql -u root -p &lt; database_init.sql

# 导入触发器
mysql -u root -p shop_mall &lt; database_triggers.sql
```

### 2. 后端启动

```bash
# 进入后端目录
cd /workspace

# 复制环境变量配置文件
cp .env.example .env

# 编辑 .env 文件，配置数据库连接信息
# DB_HOST=localhost
# DB_PORT=3306
# DB_NAME=shop_mall
# DB_USERNAME=root
# DB_PASSWORD=your_password

# 使用 Maven 启动
./mvnw spring-boot:run

# 或者先打包再运行
./mvnw clean package
java -jar target/springboot-0.0.1-SNAPSHOT.jar
```

后端服务将在 `http://localhost:8081` 启动

### 3. 前端启动

```bash
# 进入前端目录（如果已创建）
cd shop-aran

# 复制环境变量配置文件
cp .env.example .env

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

## 测试账号

### 管理员
- 用户名: admin
- 密码: 123456

### 商家
- 用户名: seller1
- 密码: 123456

### 用户
- 用户名: user1
- 密码: 123456

## 项目结构

```
/workspace
├── src/
│   ├── main/
│   │   ├── java/com/mybatisplus/
│   │   │   ├── common/          # 公共类
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── entity/          # 实体类
│   │   │   ├── mapper/          # 数据访问层
│   │   │   ├── service/         # 服务层
│   │   │   └── SpringbootApplication.java
│   │   └── resources/
│   │       └── application.yml  # 配置文件
├── database_init.sql            # 数据库初始化脚本
├── database_triggers.sql        # 数据库触发器脚本
├── 课程设计报告.md             # 课程设计报告
├── 答辩PPT大纲.md              # 答辩PPT大纲
├── DEVELOPMENT_GUIDE.md         # 开发指南
└── README.md                    # 项目说明
```

## 核心功能说明

### 订单流程

1. **创建订单** - 检查库存，生成订单号，计算总价
2. **支付订单** - 更新支付状态，触发器自动扣减库存
3. **商家发货** - 更新订单状态为已发货
4. **确认收货** - 更新订单状态为已完成
5. **取消订单** - 检查状态，回滚库存（如已支付）

### 库存管理

使用数据库触发器 `trg_after_order_paid` 在订单支付时自动扣减库存并增加销量，保证数据一致性。

## 开发说明

详细的开发指南请参考 [DEVELOPMENT_GUIDE.md](file:///workspace/DEVELOPMENT_GUIDE.md)

## 课程设计材料

本项目包含完整的课程设计材料：

1. **课程设计报告** - [课程设计报告.md](file:///workspace/课程设计报告.md)
2. **答辩PPT大纲** - [答辩PPT大纲.md](file:///workspace/答辩PPT大纲.md)
3. **数据库设计文档** - 包含在报告中
4. **系统源代码** - 完整的前后端代码

## 常见问题

### 后端启动失败
- 检查数据库连接配置是否正确
- 确认数据库已启动
- 检查端口8081是否被占用

### 前端无法连接后端
- 确认后端服务已启动
- 检查前端 .env 配置的 API 地址是否正确
- 检查跨域配置

### 数据库触发器不工作
- 确认触发器已正确导入
- 检查 MySQL 用户是否有足够权限

## 许可证

本项目仅供课程设计学习使用。

## 联系方式

如有问题，请联系课程设计小组。

