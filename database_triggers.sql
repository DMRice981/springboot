
-- =====================================================
-- Aran Shop 数据库触发器
-- 功能：库存扣减、销量统计、时间戳自动更新等
-- =====================================================

-- 删除已存在的触发器
DROP TRIGGER IF EXISTS trg_after_order_paid;
DROP TRIGGER IF EXISTS trg_before_order_insert;
DROP TRIGGER IF EXISTS trg_before_order_pay_time;
DROP TRIGGER IF EXISTS trg_before_order_send_time;
DROP TRIGGER IF EXISTS trg_before_order_confirm_time;
DROP TRIGGER IF EXISTS trg_before_order_item_insert;
DROP TRIGGER IF EXISTS trg_before_goods_insert;
DROP TRIGGER IF EXISTS trg_before_goods_update;
DROP TRIGGER IF EXISTS trg_before_cart_insert;
DROP TRIGGER IF EXISTS trg_before_cart_update;

-- 1. 订单支付后自动扣减库存并增加销量
DELIMITER $$
CREATE TRIGGER trg_after_order_paid
AFTER UPDATE ON `order`
FOR EACH ROW
BEGIN
    -- 只有当订单状态从非支付变为支付时才触发
    IF OLD.pay_status = 0 AND NEW.pay_status = 1 THEN
        -- 更新商品库存和销量
        UPDATE goods g
        INNER JOIN order_item oi ON g.id = oi.goods_id
        SET g.stock = g.stock - oi.num,
            g.sales = g.sales + oi.num
        WHERE oi.order_no = NEW.order_no;
    END IF;
END$$
DELIMITER ;

-- 2. 创建订单时自动设置创建时间
DELIMITER $$
CREATE TRIGGER trg_before_order_insert
BEFORE INSERT ON `order`
FOR EACH ROW
BEGIN
    IF NEW.create_time IS NULL THEN
        SET NEW.create_time = NOW();
    END IF;
END$$
DELIMITER ;

-- 3. 订单支付时自动设置支付时间
DELIMITER $$
CREATE TRIGGER trg_before_order_pay_time
BEFORE UPDATE ON `order`
FOR EACH ROW
BEGIN
    IF OLD.pay_status = 0 AND NEW.pay_status = 1 THEN
        SET NEW.pay_time = NOW();
    END IF;
END$$
DELIMITER ;

-- 4. 订单发货时自动设置发货时间
DELIMITER $$
CREATE TRIGGER trg_before_order_send_time
BEFORE UPDATE ON `order`
FOR EACH ROW
BEGIN
    -- 假设 order_status 1:待付款, 2:已付款, 3:已发货, 4:已完成
    IF OLD.order_status < 3 AND NEW.order_status >= 3 THEN
        SET NEW.send_time = NOW();
    END IF;
END$$
DELIMITER ;

-- 5. 订单确认收货时自动设置确认时间
DELIMITER $$
CREATE TRIGGER trg_before_order_confirm_time
BEFORE UPDATE ON `order`
FOR EACH ROW
BEGIN
    -- 假设 order_status 4:已完成
    IF OLD.order_status < 4 AND NEW.order_status >= 4 THEN
        SET NEW.confirm_time = NOW();
    END IF;
END$$
DELIMITER ;

-- 6. 创建订单项时自动设置创建时间
DELIMITER $$
CREATE TRIGGER trg_before_order_item_insert
BEFORE INSERT ON order_item
FOR EACH ROW
BEGIN
    IF NEW.create_time IS NULL THEN
        SET NEW.create_time = NOW();
    END IF;
END$$
DELIMITER ;

-- 7. 创建商品时自动设置创建和更新时间
DELIMITER $$
CREATE TRIGGER trg_before_goods_insert
BEFORE INSERT ON goods
FOR EACH ROW
BEGIN
    IF NEW.create_time IS NULL THEN
        SET NEW.create_time = NOW();
    END IF;
    IF NEW.update_time IS NULL THEN
        SET NEW.update_time = NOW();
    END IF;
END$$
DELIMITER ;

-- 8. 更新商品时自动更新更新时间
DELIMITER $$
CREATE TRIGGER trg_before_goods_update
BEFORE UPDATE ON goods
FOR EACH ROW
BEGIN
    SET NEW.update_time = NOW();
END$$
DELIMITER ;

-- 9. 购物车添加商品时自动设置时间
DELIMITER $$
CREATE TRIGGER trg_before_cart_insert
BEFORE INSERT ON cart
FOR EACH ROW
BEGIN
    IF NEW.create_time IS NULL THEN
        SET NEW.create_time = NOW();
    END IF;
    IF NEW.update_time IS NULL THEN
        SET NEW.update_time = NOW();
    END IF;
END$$
DELIMITER ;

-- 10. 购物车更新时自动设置更新时间
DELIMITER $$
CREATE TRIGGER trg_before_cart_update
BEFORE UPDATE ON cart
FOR EACH ROW
BEGIN
    SET NEW.update_time = NOW();
END$$
DELIMITER ;

-- =====================================================
-- 查看已创建的触发器
-- =====================================================
SHOW TRIGGERS;

