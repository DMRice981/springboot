package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Result;
import com.mybatisplus.dto.OrderDTO;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.entity.Order;
import com.mybatisplus.entity.OrderItem;
import com.mybatisplus.service.GoodsService;
import com.mybatisplus.service.OrderItemService;
import com.mybatisplus.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final GoodsService goodsService;

    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    public Result<List<OrderDTO>> list(@RequestParam Integer userId) {
        List<Order> orders = orderService.lambdaQuery()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime)
                .list();
        
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Order order : orders) {
            LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderItem::getOrderNo, order.getOrderNo());
            List<OrderItem> orderItems = orderItemService.list(wrapper);
            
            orderDTOList.add(new OrderDTO(order, orderItems));
        }
        
        return Result.success(orderDTOList);
    }

    /**
     * 获取所有订单（管理端）
     */
    @GetMapping("/list/all")
    public Result<List<OrderDTO>> listAll(@RequestParam(required = false) Integer userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        List<Order> orders = orderService.list(wrapper);
        
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Order order : orders) {
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderNo, order.getOrderNo());
            List<OrderItem> orderItems = orderItemService.list(itemWrapper);
            
            orderDTOList.add(new OrderDTO(order, orderItems));
        }
        
        return Result.success(orderDTOList);
    }

    /**
     * 获取单个订单详情
     */
    @GetMapping("/get/{id}")
    public Result<OrderDTO> get(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderNo, order.getOrderNo());
        List<OrderItem> orderItems = orderItemService.list(wrapper);
        
        return Result.success(new OrderDTO(order, orderItems));
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Result<OrderDTO> create(@RequestBody Map<String, Object> data) {
        Integer userId = (Integer) data.get("userId");
        Integer addressId = (Integer) data.get("addressId");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) data.get("cartItems");
        
        if (cartItems == null || cartItems.isEmpty()) {
            return Result.error("购物车不能为空");
        }
        
        String orderNo = "ORD" + System.currentTimeMillis() + new Random().nextInt(1000);
        BigDecimal totalPrice = BigDecimal.ZERO;
        
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map<String, Object> item : cartItems) {
            Integer goodsId = (Integer) item.get("goodsId");
            Integer num = (Integer) item.get("num");
            
            Goods goods = goodsService.getById(goodsId);
            if (goods == null || goods.getStatus() == 0) {
                return Result.error("商品已下架");
            }
            if (goods.getStock() < num) {
                return Result.error(goods.getGoodsName() + " 库存不足");
            }
            
            BigDecimal price = goods.getPrice();
            BigDecimal itemTotal = price.multiply(new BigDecimal(num));
            totalPrice = totalPrice.add(itemTotal);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderNo(orderNo);
            orderItem.setGoodsId(goodsId);
            orderItem.setGoodsName(goods.getGoodsName());
            orderItem.setGoodsImg(goods.getGoodsImg());
            orderItem.setPrice(price);
            orderItem.setNum(num);
            orderItem.setTotalPrice(itemTotal);
            orderItem.setCreateTime(LocalDateTime.now());
            orderItems.add(orderItem);
            
            goods.setStock(goods.getStock() - num);
            goods.setSales(goods.getSales() + num);
            goodsService.updateById(goods);
        }
        
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setTotalPrice(totalPrice);
        order.setPayPrice(totalPrice);
        order.setPayStatus(0);
        order.setOrderStatus(0);
        order.setCreateTime(LocalDateTime.now());
        orderService.save(order);
        
        for (OrderItem item : orderItems) {
            orderItemService.save(item);
        }
        
        return Result.success("创建成功", new OrderDTO(order, orderItems));
    }

    /**
     * 更新订单
     */
    @PutMapping("/update")
    public Result<Order> update(@RequestBody Order order) {
        orderService.updateById(order);
        return Result.success("更新成功", order);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> pay(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getPayStatus() == 1) {
            return Result.error("订单已支付");
        }
        
        order.setPayStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderService.updateById(order);
        
        return Result.success("支付成功");
    }

    /**
     * 发货
     */
    @PostMapping("/send/{id}")
    public Result<Void> send(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getPayStatus() == 0) {
            return Result.error("请先支付");
        }
        if (order.getOrderStatus() != 0) {
            return Result.error("订单状态不允许发货");
        }
        
        order.setOrderStatus(1);
        order.setSendTime(LocalDateTime.now());
        orderService.updateById(order);
        
        return Result.success("发货成功");
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirm/{id}")
    public Result<Void> confirm(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getOrderStatus() != 1) {
            return Result.error("订单状态不允许确认收货");
        }
        
        order.setOrderStatus(3);
        order.setConfirmTime(LocalDateTime.now());
        orderService.updateById(order);
        
        return Result.success("确认收货成功");
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancel(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getPayStatus() == 1) {
            return Result.error("已支付订单无法取消，请联系客服");
        }
        if (order.getOrderStatus() == 4) {
            return Result.error("订单已取消");
        }
        
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderNo, order.getOrderNo());
        List<OrderItem> orderItems = orderItemService.list(wrapper);
        
        for (OrderItem item : orderItems) {
            Goods goods = goodsService.getById(item.getGoodsId());
            if (goods != null) {
                goods.setStock(goods.getStock() + item.getNum());
                goods.setSales(goods.getSales() - item.getNum());
                goodsService.updateById(goods);
            }
        }
        
        order.setOrderStatus(4);
        orderService.updateById(order);
        
        return Result.success("取消成功");
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderNo, order.getOrderNo());
        orderItemService.remove(wrapper);
        
        orderService.removeById(id);
        return Result.success("删除成功");
    }
}
