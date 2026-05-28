-- =====================================================
-- Aran Shop 电商系统数据库初始化脚本
-- 数据库：shop_mall
-- 版本：1.0
-- 作者：课程设计小组
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS shop_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shop_mall;

-- =====================================================
-- 1. 用户表
-- =====================================================
DROP TABLE IF EXISTS user;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =====================================================
-- 2. 商家表
-- =====================================================
DROP TABLE IF EXISTS seller;
CREATE TABLE seller (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '商家ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    shop_name VARCHAR(100) COMMENT '店铺名称',
    phone VARCHAR(20) COMMENT '手机号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- =====================================================
-- 3. 管理员表
-- =====================================================
DROP TABLE IF EXISTS admin;
CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    admin_name VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    status INT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- =====================================================
-- 4. 商品分类表 - 支持树形结构
-- =====================================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    pid INT DEFAULT 0 COMMENT '父级分类ID：0表示顶级分类',
    sort INT DEFAULT 0 COMMENT '排序值',
    is_delete INT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- =====================================================
-- 5. 商品表
-- =====================================================
DROP TABLE IF EXISTS goods;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- =====================================================
-- 6. 商品图片表
-- =====================================================
DROP TABLE IF EXISTS goods_img;
CREATE TABLE goods_img (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
    goods_id INT COMMENT '商品ID',
    img_url VARCHAR(255) COMMENT '图片URL',
    sort INT DEFAULT 0 COMMENT '排序值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- =====================================================
-- 7. 购物车表
-- =====================================================
DROP TABLE IF EXISTS cart;
CREATE TABLE cart (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID',
    user_id INT COMMENT '用户ID',
    goods_id INT COMMENT '商品ID',
    num INT DEFAULT 1 COMMENT '购买数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- =====================================================
-- 8. 订单表
-- =====================================================
DROP TABLE IF EXISTS `order`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- =====================================================
-- 9. 订单项表
-- =====================================================
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单项ID',
    order_no VARCHAR(50) COMMENT '订单号',
    goods_id INT COMMENT '商品ID',
    goods_name VARCHAR(100) COMMENT '商品名称',
    goods_img VARCHAR(255) COMMENT '商品图片',
    price DECIMAL(10,2) COMMENT '商品单价',
    num INT COMMENT '购买数量',
    total_price DECIMAL(10,2) COMMENT '小计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- =====================================================
-- 10. 用户地址表
-- =====================================================
DROP TABLE IF EXISTS user_address;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- =====================================================
-- 11. 商品评论表
-- =====================================================
DROP TABLE IF EXISTS goods_comment;
CREATE TABLE goods_comment (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    goods_id INT COMMENT '商品ID',
    user_id INT COMMENT '用户ID',
    order_no VARCHAR(50) COMMENT '订单号',
    score INT COMMENT '评分：1-5星',
    content TEXT COMMENT '评论内容',
    comment_img VARCHAR(255) COMMENT '评论图片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论表';

-- =====================================================
-- 12. 轮播图表
-- =====================================================
DROP TABLE IF EXISTS banner;
CREATE TABLE banner (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '轮播图ID',
    img_url VARCHAR(255) COMMENT '图片URL',
    link_url VARCHAR(255) COMMENT '跳转链接',
    sort INT DEFAULT 0 COMMENT '排序值',
    status INT DEFAULT 1 COMMENT '状态：1显示 0隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- =====================================================
-- 13. 售后表
-- =====================================================
DROP TABLE IF EXISTS after_sale;
CREATE TABLE after_sale (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '售后ID',
    order_id INT COMMENT '订单ID',
    user_id INT COMMENT '用户ID',
    seller_id INT COMMENT '商家ID',
    goods_id INT COMMENT '商品ID',
    reason TEXT COMMENT '售后原因',
    status INT DEFAULT 0 COMMENT '处理状态：0待处理 1已处理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后表';

-- =====================================================
-- 插入测试数据
-- =====================================================

-- 插入管理员
INSERT INTO admin (admin_name, password, nickname) VALUES
('admin', '123456', '系统管理员');

-- 插入商家
INSERT INTO seller (username, password, shop_name, phone) VALUES
('seller1', '123456', '电子产品专卖店', '13800138001'),
('seller2', '123456', '时尚服装屋', '13800138002');

-- 插入用户
INSERT INTO user (username, password, phone, email, avatar, status) VALUES
('user1', '123456', '13800138000', 'user1@example.com', 'https://via.placeholder.com/150', 1),
('user2', '123456', '13800138003', 'user2@example.com', 'https://via.placeholder.com/150', 1);

-- 插入分类（树形结构）
INSERT INTO category (name, pid, sort) VALUES
('电子产品', 0, 1),
('服装', 0, 2),
('食品', 0, 3),
('家居', 0, 4),
('手机', 1, 1),
('电脑', 1, 2),
('男装', 2, 1),
('女装', 2, 2);

-- 插入商品
INSERT INTO goods (category_id, goods_name, goods_img, price, market_price, stock, sales, goods_desc, status, seller_id) VALUES
(5, 'iPhone 15 Pro Max', 'https://via.placeholder.com/400x300?text=iPhone', 9999.00, 11999.00, 100, 50, '最新款苹果手机，性能强劲', 1, 1),
(5, '华为 Mate 60 Pro', 'https://via.placeholder.com/400x300?text=Huawei', 6999.00, 7999.00, 80, 35, '国产旗舰，支持5G', 1, 1),
(6, 'MacBook Pro 14英寸', 'https://via.placeholder.com/400x300?text=MacBook', 14999.00, 16999.00, 50, 20, '专业级笔记本电脑', 1, 1),
(7, '商务西装', 'https://via.placeholder.com/400x300?text=Suit', 899.00, 1299.00, 200, 80, '高品质商务正装', 1, 2),
(8, '时尚连衣裙', 'https://via.placeholder.com/400x300?text=Dress', 299.00, 499.00, 150, 60, '夏季新款连衣裙', 1, 2);

-- 插入商品图片
INSERT INTO goods_img (goods_id, img_url, sort) VALUES
(1, 'https://via.placeholder.com/400x300?text=iPhone+1', 1),
(1, 'https://via.placeholder.com/400x300?text=iPhone+2', 2),
(2, 'https://via.placeholder.com/400x300?text=Huawei+1', 1),
(3, 'https://via.placeholder.com/400x300?text=MacBook+1', 1),
(4, 'https://via.placeholder.com/400x300?text=Suit+1', 1),
(5, 'https://via.placeholder.com/400x300?text=Dress+1', 1);

-- 插入轮播图
INSERT INTO banner (img_url, link_url, sort, status) VALUES
('https://via.placeholder.com/800x300?text=Banner+1', '/goods/1', 1, 1),
('https://via.placeholder.com/800x300?text=Banner+2', '/goods/2', 2, 1);

-- 插入收货地址
INSERT INTO user_address (user_id, name, phone, province, city, district, detail, is_default) VALUES
(1, '张三', '13800138000', '北京市', '北京市', '朝阳区', '建国路88号', 1),
(1, '张三', '13800138000', '上海市', '上海市', '浦东新区', '陆家嘴金融中心', 0);

-- =====================================================
-- 数据库初始化完成
-- =====================================================
SELECT '数据库初始化完成！' AS message;
