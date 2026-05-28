package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Constants;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final GoodsService goodsService;

    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam Integer userId) {
        List<Order> list = orderService.lambdaQuery()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime)
                .list();
        return Result.success(list);
    }

    @GetMapping("/list/all")
    public Result<List<Order>> listAll(@RequestParam(required = false) Integer sellerId,
                                       @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Order::getOrderStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        return Result.success(orderService.list(wrapper));
    }

    @GetMapping("/get/{id}")
    public Result<OrderDTO> get(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        List<OrderItem> items = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderNo, order.getOrderNo())
                .list();

        OrderDTO dto = new OrderDTO();
        dto.setOrder(order);
        dto.setOrderItems(items);
        return Result.success(dto);
    }

    @PostMapping("/create")
    @Transactional
    public Result<OrderDTO> create(@RequestBody Map<String, Object> data) {
        Integer userId = (Integer) data.get("userId");
        Integer addressId = (Integer) data.get("addressId");
        List<Map<String, Object>> goodsList = (List<Map<String, Object>>) data.get("goodsList");

        if (userId == null || addressId == null || goodsList == null || goodsList.isEmpty()) {
            return Result.error("参数不完整");
        }

        Order order = new Order();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        Random random = new Random();
        String randomStr = String.format("%04d", random.nextInt(10000));
        String orderNo = Constants.ORDER_PREFIX + timestamp + randomStr;
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setOrderStatus(Constants.OrderStatus.PENDING);
        order.setPayStatus(Constants.PayStatus.UNPAID);
        order.setTotalPrice(BigDecimal.ZERO);
        order.setPayPrice(BigDecimal.ZERO);
        order.setCreateTime(LocalDateTime.now());

        orderService.save(order);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map<String, Object> goodsData : goodsList) {
            Integer goodsId = (Integer) goodsData.get("goodsId");
            Integer quantity = (Integer) goodsData.get("quantity");
            if (quantity == null) {
                quantity = (Integer) goodsData.get("num");
            }
            if (quantity == null) quantity = 1;

            Goods goods = goodsService.getById(goodsId);
            if (goods == null) {
                return Result.error("商品不存在");
            }
            if (goods.getStatus() != Constants.GoodsStatus.ON_SHELF) {
                return Result.error(goods.getGoodsName() + " 商品已下架");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete() == Constants.Status.DELETED) {
                return Result.error(goods.getGoodsName() + " 商品已删除");
            }
            if (goods.getStock() < quantity) {
                return Result.error(goods.getGoodsName() + " 库存不足");
            }

            OrderItem item = new OrderItem();
            item.setOrderNo(order.getOrderNo());
            item.setGoodsId(goodsId);
            item.setGoodsName(goods.getGoodsName());
            item.setGoodsImg(goods.getGoodsImg());
            item.setPrice(goods.getPrice());
            item.setNum(quantity);
            item.setCreateTime(LocalDateTime.now());

            orderItemService.save(item);
            items.add(item);

            BigDecimal itemTotal = goods.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalPrice = totalPrice.add(itemTotal);

            goods.setStock(goods.getStock() - quantity);
            goods.setSales(goods.getSales() + quantity);
            goods.setUpdateTime(LocalDateTime.now());
            goodsService.updateById(goods);
        }

        order.setTotalPrice(totalPrice);
        order.setPayPrice(totalPrice);
        orderService.updateById(order);

        OrderDTO dto = new OrderDTO();
        dto.setOrder(order);
        dto.setOrderItems(items);

        return Result.success("下单成功", dto);
    }

    @PutMapping("/update")
    public Result<Order> update(@RequestBody Order order) {
        orderService.updateById(order);
        return Result.success("更新成功", order);
    }

    @PostMapping("/pay/{id}")
    @Transactional
    public Result<Void> pay(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getPayStatus().equals(Constants.PayStatus.PAID)) {
            return Result.error("订单已支付");
        }

        order.setPayStatus(Constants.PayStatus.PAID);
        order.setOrderStatus(Constants.OrderStatus.PAID);
        order.setPayTime(LocalDateTime.now());
        orderService.updateById(order);

        return Result.success();
    }

    @PostMapping("/send/{id}")
    public Result<Void> send(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getOrderStatus().equals(Constants.OrderStatus.PAID)) {
            return Result.error("订单状态不正确，需为待发货状态");
        }

        order.setOrderStatus(Constants.OrderStatus.SHIPPED);
        order.setSendTime(LocalDateTime.now());
        orderService.updateById(order);

        return Result.success();
    }

    @PostMapping("/confirm/{id}")
    public Result<Void> confirm(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getOrderStatus().equals(Constants.OrderStatus.SHIPPED)) {
            return Result.error("订单状态不正确，需为已发货状态");
        }

        order.setOrderStatus(Constants.OrderStatus.COMPLETED);
        order.setConfirmTime(LocalDateTime.now());
        orderService.updateById(order);

        return Result.success();
    }

    @PostMapping("/cancel/{id}")
    @Transactional
    public Result<Void> cancel(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getOrderStatus().equals(Constants.OrderStatus.CANCELLED)) {
            return Result.error("订单已取消");
        }
        if (order.getOrderStatus().equals(Constants.OrderStatus.COMPLETED)) {
            return Result.error("订单已完成，无法取消");
        }

        order.setOrderStatus(Constants.OrderStatus.CANCELLED);
        orderService.updateById(order);

        if (order.getPayStatus().equals(Constants.PayStatus.PAID)) {
            List<OrderItem> items = orderItemService.lambdaQuery()
                    .eq(OrderItem::getOrderNo, order.getOrderNo())
                    .list();

            for (OrderItem item : items) {
                Goods goods = goodsService.getById(item.getGoodsId());
                if (goods != null) {
                    goods.setStock(goods.getStock() + item.getNum());
                    goods.setSales(Math.max(0, goods.getSales() - item.getNum()));
                    goods.setUpdateTime(LocalDateTime.now());
                    goodsService.updateById(goods);
                }
            }
        }

        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 删除订单项
        orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderNo, order.getOrderNo())
                .list()
                .forEach(item -> orderItemService.removeById(item.getId()));

        // 删除订单
        orderService.removeById(id);

        return Result.success();
    }
}
