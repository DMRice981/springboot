
Aran Shop 电商平台
后端开发手册
Spring Boot + MyBatis-Plus + MySQL
适合新手从零开始构建电商后端系统
 
一、项目概述
1.1 项目简介
Aran Shop 后端是一个基于 Spring Boot 的 RESTful API 服务，采用 MyBatis-Plus 作为 ORM 框架，MySQL 作为数据库。项目实现了完整的电商后端功能，为前端提供数据接口支持。
1.2 技术栈
本项目采用以下主流技术栈：
•	后端框架：Spring Boot 3.2.5
•	ORM框架：MyBatis-Plus 3.5.6
•	数据库：MySQL 8.0+
•	JDK版本：JDK 17
•	构建工具：Maven
•	开发工具：IntelliJ IDEA
1.3 功能模块
后端包含以下核心模块：
•	用户模块：用户注册、登录、信息管理
•	商品模块：商品增删改查、上下架管理
•	购物车模块：购物车增删改查
•	订单模块：订单创建、查询、管理
•	分类模块：商品分类管理
•	管理员模块：后台管理员管理
•	商家模块：商家信息管理
二、环境准备
2.1 安装 JDK 17
Spring Boot 3.x 需要 JDK 17 或更高版本。请按以下步骤安装：
1.	访问 Oracle 官网或 OpenJDK 网站下载 JDK 17
2.	运行安装程序，按提示完成安装
3.	配置环境变量 JAVA_HOME
4.	打开命令行，输入 java -version 验证安装
2.2 安装 Maven
Maven 是 Java 项目的构建工具：
5.	访问 https://maven.apache.org/ 下载 Maven
6.	解压到指定目录
7.	配置 MAVEN_HOME 环境变量
8.	输入 mvn -v 验证安装
2.3 安装 MySQL
项目使用 MySQL 8.0+ 作为数据库：
9.	下载并安装 MySQL 8.0
10.	创建数据库 shop_mall
11.	创建用户并授权
2.4 安装 IntelliJ IDEA
推荐使用 IntelliJ IDEA 作为开发工具：
12.	访问 https://www.jetbrains.com/idea/ 下载
13.	安装并配置 JDK 路径
14.	安装 Lombok 插件
三、项目创建步骤
3.1 使用 Spring Initializr 创建项目
访问 https://start.spring.io/ 创建项目：
•	Project: Maven
•	Language: Java
•	Spring Boot: 3.2.5
•	Group: com.mybatisplus
•	Artifact: springboot
•	Java: 17
3.2 添加依赖
在 pom.xml 中添加以下依赖：
<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.6</version>
</dependency>

<!-- MySQL 驱动 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
3.3 配置 application.yml
在 src/main/resources 下创建 application.yml：
server:
  port: 8081

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/shop_mall?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root
    password: your_password

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.mybatisplus.entity
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
四、项目结构详解
4.1 标准目录结构
Spring Boot 项目采用标准的 Maven 目录结构：
springboot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mybatisplus/
│   │   │       ├── controller/   # 控制器层
│   │   │       ├── service/      # 服务层
│   │   │       │   └── impl/     # 服务实现
│   │   │       ├── mapper/       # 数据访问层
│   │   │       ├── entity/       # 实体类
│   │   │       └── SpringbootApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
4.2 启动类
SpringbootApplication.java 是项目入口：
@SpringBootApplication
@MapperScan("com.mybatisplus.mapper")
public class SpringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }
}
五、实体类开发
5.1 实体类规范
实体类对应数据库表，使用 Lombok 简化代码：
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private LocalDateTime createTime;
}
5.2 常用注解说明
注解	作用	示例
@Data	自动生成 getter/setter	@Data
@TableName	指定表名	@TableName("user")
@TableId	主键标识	@TableId(type = IdType.AUTO)
六、Mapper 层开发
6.1 Mapper 接口
Mapper 接口继承 BaseMapper，即可获得 CRUD 方法：
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
6.2 BaseMapper 提供的方法
•	insert(T entity) - 插入
•	deleteById(Serializable id) - 根据 ID 删除
•	updateById(T entity) - 根据 ID 更新
•	selectById(Serializable id) - 根据 ID 查询
•	selectList(Wrapper<T> queryWrapper) - 条件查询
七、Service 层开发
7.1 Service 接口
Service 接口继承 IService：
public interface UserService extends IService<User> {
}
7.2 Service 实现类
Service 实现类继承 ServiceImpl：
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
八、Controller 层开发
8.1 基础 CRUD 控制器
Controller 提供 RESTful API 接口：
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @PostMapping("/add")
    public boolean add(@RequestBody User user) {
        return userService.save(user);
    }
}
8.2 常用注解说明
注解	作用	示例
@RestController	REST 控制器	@RestController
@RequestMapping	基础路径	@RequestMapping("/user")
@GetMapping	GET 请求	@GetMapping("/list")
@PostMapping	POST 请求	@PostMapping("/add")
@RequestBody	接收 JSON 参数	@RequestBody User user
九、条件查询
9.1 LambdaQueryWrapper
使用 LambdaQueryWrapper 进行条件查询：
// 等值查询
List<User> list = userService.lambdaQuery()
        .eq(User::getStatus, 1)
        .list();

// 模糊查询
List<User> list = userService.lambdaQuery()
        .like(User::getUsername, "张")
        .list();
9.2 LambdaUpdateWrapper
使用 LambdaUpdateWrapper 进行条件更新：
userService.lambdaUpdate()
        .set(User::getStatus, 0)
        .eq(User::getId, 1)
        .update();
十、运行与部署
10.1 本地运行
在 IntelliJ IDEA 中运行：
15.	打开项目，等待 Maven 加载依赖
16.	找到 SpringbootApplication.java
17.	右键点击 main 方法，选择 Run
18.	访问 http://localhost:8081
10.2 Maven 打包
使用 Maven 打包成可运行 JAR：
mvn clean package
打包后的 JAR 文件在 target 目录下。
10.3 运行 JAR 包
java -jar springboot-0.0.1-SNAPSHOT.jar
十一、常见问题解答
Q1: 数据库连接失败？
A: 检查以下几点：
•	MySQL 服务是否启动
•	数据库名、用户名、密码是否正确
•	数据库驱动版本是否匹配
Q2: Mapper 注入失败？
A: 确保：
•	Mapper 接口添加了 @Mapper 注解
•	启动类添加了 @MapperScan 注解
Q3: 端口被占用？
A: 修改 application.yml 中的 server.port 配置，或关闭占用 8081 端口的程序。
结语
本手册详细介绍了 Aran Shop 后端项目的开发流程和核心技术点。通过本手册的学习，新手开发者可以：
•	掌握 Spring Boot 项目的搭建方法
•	理解 MyBatis-Plus 的 CRUD 操作
•	学会构建 RESTful API 接口
•	实现完整的电商后端功能
希望本手册能帮助您顺利开发出稳定高效的后端服务！
