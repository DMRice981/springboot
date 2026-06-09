# Aran Shop - 全栈电商系统

> 基于 Spring Boot + Vue 3 + Element Plus 的全栈电商系统，支持用户、商家、管理员三种角色。

---

## 🏗️ 技术架构

| 层级 | 技术栈 |
|------|--------|
| **后端框架** | Spring Boot 3.2.5 |
| **ORM** | MyBatis Plus 3.5.6 |
| **数据库** | MySQL 8.0+ |
| **前端框架** | Vue 3 (Composition API) |
| **构建工具** | Vite |
| **UI组件库** | Element Plus |
| **路由管理** | Vue Router 4 |
| **HTTP客户端** | Axios |
| **Java版本** | JDK 17+ |
| **Node版本** | 20.19+ |

---

## 📁 项目结构

```
cxode/
├── shop-aran/                    # 前端项目 (Vue 3)
│   ├── src/
│   │   ├── api/                  # API 接口定义
│   │   ├── assets/               # 静态资源
│   │   ├── plugins/              # Vue 插件系统
│   │   │   ├── index.js          # 插件入口
│   │   │   ├── auth.js           # 认证管理
│   │   │   ├── element.js        # Element Plus 增强
│   │   │   ├── progress.js       # 路由进度条
│   │   │   └── request.js        # HTTP 请求封装
│   │   ├── router/               # 路由配置
│   │   ├── utils/                # 工具函数
│   │   │   ├── format.js         # 格式化工具
│   │   │   ├── debounce.js       # 防抖节流
│   │   │   └── valid.js          # 验证工具
│   │   └── views/                # 页面组件
│   │       ├── admin/            # 管理员页面
│   │       ├── seller/           # 商家页面
│   │       └── ...               # 用户页面
│   ├── vite.config.js
│   └── package.json
│
├── springboot/                    # 后端项目 (Spring Boot)
│   └── src/main/java/com/mybatisplus/
│       ├── config/               # 配置类
│       │   ├── CorsConfig.java
│       │   ├── DotenvConfig.java
│       │   ├── JacksonConfig.java
│       │   ├── GlobalExceptionHandler.java
│       │   ├── MybatisPlusConfig.java   # 分页插件
│       │   └── WebMvcConfig.java
│       ├── common/               # 公共类
│       │   ├── Constants.java    # 常量定义
│       │   └── Result.java       # 统一响应结果
│       ├── controller/           # 控制器 (13个)
│       ├── entity/               # 实体类 (13个)
│       ├── mapper/               # 数据访问层
│       ├── service/              # 业务逻辑层
│       ├── dto/                  # 数据传输对象
│       │   ├── OrderDTO.java
│       │   ├── AfterSaleDTO.java
│       │   ├── GoodsWithSellerVO.java
│       │   ├── PageRequest.java  # 分页请求
│       │   └── PageResult.java   # 分页响应
│       ├── interceptor/          # 拦截器
│       │   └── LoginInterceptor.java
│       └── SpringbootApplication.java
│
├── database_init.sql              # 数据库初始化脚本（唯一）
├── INDEX.md                       # 项目索引文档
├── DEVELOPMENT_GUIDE.md           # 详细开发指南
└── README.md                      # 本文件
```

---

## 🎯 核心功能模块

### 🔑 用户模块
- ✅ 用户注册与登录
- ✅ 商品浏览与关键词搜索
- ✅ 购物车管理
- ✅ 订单管理（分页）
- ✅ 收货地址管理
- ✅ 售后申请

### 🏪 商家模块
- ✅ 商家注册与登录
- ✅ 商品管理（添加、编辑、上架/下架，仅显示自己的商品）
- ✅ 订单管理（分页 + 搜索 + 发货功能）
- ✅ 售后处理

### 🛡️ 管理员模块
- ✅ 分类管理（支持树形结构）
- ✅ 商品管理（分页 + 搜索 + 商家信息显示）
- ✅ 轮播图管理
- ✅ 用户管理（分页 + 搜索）
- ✅ 订单管理（分页 + 搜索 + 状态筛选）

### 🚀 系统特性
- ✅ **前后端分离架构** - 完全独立部署
- ✅ **统一认证系统** - auth插件管理三种角色状态
- ✅ **统一HTTP请求** - 所有前端页面使用http插件
- ✅ **分页与搜索** - 所有列表页支持分页和关键词搜索
- ✅ **订单状态流转** - 0待支付 → 1待发货 → 2已发货 → 3已完成 → 4已取消
- ✅ **后端接口保护** - 登录拦截器保护敏感接口
- ✅ **全局异常处理** - 统一的异常处理和错误响应
- ✅ **时间格式统一** - Jackson配置统一JSON时间格式

---

## 📦 API 接口（分页与搜索）

所有管理列表页面均支持分页和搜索功能。

### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `pageNum` | Integer | 否 | 页码，从1开始，默认1 |
| `pageSize` | Integer | 否 | 每页条数，默认10 |
| `keyword` | String | 否 | 关键词搜索 |
| `status` | Integer | 否 | 状态筛选 |
| `sellerId` | Integer | 否 | 商家ID（商家相关接口） |
| `categoryId` | Integer | 否 | 分类ID（商品列表） |

### 响应格式

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "list": [...]
  }
}
```

### 分页接口列表

| 接口 | 方法 | 说明 |
|------|------|------|
| `/goods/list/paged` | GET | 商品列表（公开，按分类和关键词筛选） |
| `/goods/list/all/paged` | GET | 商品列表（管理，含商家信息） |
| `/goods/my/paged` | GET | 商家自己的商品列表 |
| `/order/list/paged` | GET | 用户订单列表 |
| `/order/list/all/paged` | GET | 订单列表（管理） |
| `/user/list/paged` | GET | 用户列表（管理） |
| `/seller/list/paged` | GET | 商家列表 |
| `/after-sale/list/paged` | GET | 售后列表 |

### 订单状态流转

| 状态值 | 名称 | 说明 | 可执行操作 |
|--------|------|------|------------|
| 0 | 待支付 | 用户创建订单，等待支付 | 支付、取消 |
| 1 | 待发货 | 用户已支付，等待商家发货 | 商家发货 |
| 2 | 已发货 | 商家已发货，配送中 | 用户确认收货 |
| 3 | 已完成 | 用户确认收货，订单完成 | 申请售后、评价 |
| 4 | 已取消 | 订单被取消 | 无 |

---

## 🚀 快速开始

### 1️⃣ 环境准备

确保已安装以下工具：
- **JDK 17+** - Java开发工具包
- **Node.js 20.19+** - JavaScript运行环境
- **MySQL 8.0+** - 关系型数据库
- **Maven** - Java构建工具（项目包含wrapper）

### 2️⃣ 数据库初始化

```bash
# 进入项目根目录
cd cxode

# 执行数据库初始化脚本
mysql -u root -p < database_init.sql
```

> 脚本会自动创建 `shop_mall` 数据库及所有表，并预置测试数据

### 3️⃣ 配置环境变量

**后端配置** — 复制并修改 `springboot/.env`:

```env
# 服务器配置
SERVER_PORT=8081

# 数据库连接配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=shop_mall
DB_USERNAME=root
DB_PASSWORD=your_password_here

# 数据库可选配置
DB_USE_UNICODE=true
DB_CHARACTER_ENCODING=utf8
DB_SERVER_TIMEZONE=GMT%2B8
DB_USE_SSL=false
```

**前端配置** — 复制并修改 `shop-aran/.env`:

```env
# API 基础地址
VITE_API_BASE_URL=http://localhost:8081

# API 前缀
VITE_API_PREFIX=/api

# 请求超时（毫秒）
VITE_REQUEST_TIMEOUT=5000
```

> 快速生成配置文件：
> ```bash
> # 复制后端配置
> copy springboot\.env.example springboot\.env
>
> # 复制前端配置
> copy shop-aran\.env.example shop-aran\.env
> ```

### 4️⃣ 安装前端依赖

```bash
cd shop-aran
npm install
```

### 5️⃣ 启动项目

**启动后端服务**（新终端）：

```bash
cd cxode\springboot
.\mvnw.cmd spring-boot:run
```

后端启动成功后访问：http://localhost:8081

**启动前端服务**（另一个终端）：

```bash
cd cxode\shop-aran
npm run dev
```

前端启动成功后访问：http://localhost:5173

---

## 🔐 测试账号

数据库初始化脚本已预置以下测试账号：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 👨‍💼 管理员 | `admin` | `123456` |
| 🏪 商家 | `seller1` | `123456` |
| 👤 用户 | `user1` | `123456` |

---

## 📊 功能测试清单

### 核心功能测试

- [x] 用户注册 / 登录 / 登出
- [x] 商家注册 / 登录 / 登出
- [x] 管理员登录
- [x] 商品浏览（首页、分类筛选、搜索）
- [x] 商品详情（含商家信息、库存状态）
- [x] 购物车功能（添加、修改数量、删除）
- [x] 下单购买（选择地址、生成订单）
- [x] 订单管理（用户端：分页、状态筛选）
- [x] 收货地址管理
- [x] 售后申请
- [x] 商家商品管理（仅显示自己的商品，分页+搜索）
- [x] 商家订单管理（发货功能，分页+搜索+状态筛选）
- [x] 商家售后处理
- [x] 管理员分类管理（树形结构）
- [x] 管理员商品管理（含商家信息，分页+搜索）
- [x] 管理员用户管理（分页+搜索+状态筛选）
- [x] 管理员订单管理（分页+搜索+状态筛选+发货）
- [x] 商品下架 / 上架状态管理
- [x] 后端接口拦截保护（登录拦截器）

---

## 🛠️ 常用命令

### 后端命令

```bash
cd springboot

# 编译项目
.\mvnw.cmd clean compile

# 启动开发服务
.\mvnw.cmd spring-boot:run

# 打包生产版本
.\mvnw.cmd clean package

# 跳过测试打包
.\mvnw.cmd clean package -DskipTests
```

### 前端命令

```bash
cd shop-aran

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 生产环境构建
npm run build

# 代码格式化
npm run format

# 预览生产构建
npm run preview
```

---

## 📖 项目文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 开发指南 | [DEVELOPMENT_GUIDE.md](file:///c:/Users/Lenovo/Desktop/cxode/springboot/DEVELOPMENT_GUIDE.md) | 完整开发指南、API参考、架构说明 |
| 项目索引 | [INDEX.md](file:///c:/Users/Lenovo/Desktop/cxode/INDEX.md) | 项目文件速查索引 |
| 前端文档 | [shop-aran/README.md](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/README.md) | 前端项目详细文档 |
| 数据库脚本 | [database_init.sql](file:///c:/Users/Lenovo/Desktop/cxode/database_init.sql) | 数据库初始化脚本 |

---

## 🔧 开发规范

### 后端规范

- **构造器注入**：所有 Controller 使用 `@RequiredArgsConstructor` 进行依赖注入，禁止使用 `@Autowired` 字段注入
- **常量管理**：所有状态码、枚举值使用 `Constants.java` 统一管理
- **异常处理**：使用 `GlobalExceptionHandler` 统一处理异常
- **时间格式**：通过 `JacksonConfig` 统一配置 `yyyy-MM-dd HH:mm:ss`
- **分页查询**：所有列表接口使用 MyBatis-Plus 分页插件，配合 `PageRequest` / `PageResult` DTO

### 前端规范

- **API调用**：统一使用 `http` 插件，禁止直接使用 `fetch` 或 `axios`
- **认证管理**：统一使用 `auth` 插件，禁止直接操作 `localStorage`
- **组件开发**：使用 `<script setup>` 语法，Composition API 风格
- **样式隔离**：使用 `<style scoped>` 确保样式不泄露
- **图标导入**：从 `@element-plus/icons-vue` 按需导入图标组件，模板中通过动态组件 `<component :is="IconXxx" />` 引用；不要使用包中不存在的图标（如 `Shield`、`Truck`），可替换为 `Medal`、`Van` 等存在的图标；`el-option` 的 "全部" 选项使用 `value=""` 而非 `:value="null"`
- **HTTP 参数过滤**：`http.get()` 会自动忽略值为 `null`、`undefined` 或 `''` 的查询参数，避免后端将字符串 `"null"` 当作 Integer 解析导致类型转换错误

### Git 提交规范

```
feat:     新增功能
fix:      修复 bug
docs:     文档更新
style:    代码格式调整
refactor: 代码重构
test:     测试相关
chore:    构建/工具/依赖相关
```

---

## ⚠️ 开发注意事项

1. **数据库脚本**：仅使用 `database_init.sql`，不要创建其他初始化文件
2. **环境变量**：`.env` 文件包含敏感信息，不要提交到版本控制（已在 `.gitignore` 中排除）
3. **商家隔离**：商家只能查看和管理自己的商品和订单，后端通过 `sellerId` 隔离
4. **订单状态**：请严格遵循订单状态流转逻辑（0 → 1 → 2 → 3），不要跳过或逆向流转
5. **分页参数**：前端请求列表接口时，应始终传递 `pageNum` 和 `pageSize` 参数
6. **代码格式化**：提交前端代码前执行 `npm run format` 统一代码风格

---

## 📝 待优化项

- [ ] 商品评论功能完善（图片上传、评分统计）
- [ ] 消息通知系统（订单状态变更通知、售后处理通知）
- [ ] 数据统计报表（销售统计、用户统计、商品热门排行）
- [ ] 商品推荐算法（基于分类/销量的智能推荐）
- [ ] 支付接口集成（模拟支付流程）
- [ ] 文件上传服务（商品图片、用户头像）

---

## 📞 项目文件速查

| 关键文件 | 路径 |
|----------|------|
| 主入口 | [SpringbootApplication.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/SpringbootApplication.java) |
| 路由配置 | [router/index.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/router/index.js) |
| 常量定义 | [Constants.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/common/Constants.java) |
| 统一响应 | [Result.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/common/Result.java) |
| 登录拦截器 | [LoginInterceptor.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/interceptor/LoginInterceptor.java) |
| 认证插件 | [auth.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/plugins/auth.js) |
| HTTP插件 | [request.js](file:///c:/Users/Lenovo/Desktop/cxode/shop-aran/src/plugins/request.js) |
| 分页DTO | [PageRequest.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/dto/PageRequest.java) / [PageResult.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/dto/PageResult.java) |
| 订单DTO | [OrderDTO.java](file:///c:/Users/Lenovo/Desktop/cxode/springboot/src/main/java/com/mybatisplus/dto/OrderDTO.java) |

---

## 📄 许可证

本项目仅供学习交流使用。

---

<div align="center">

**如果项目对你有帮助，请给个 ⭐ Star 支持一下**

**Happy Coding! 🚀**

</div>
