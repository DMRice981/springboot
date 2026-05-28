package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Result;
import com.mybatisplus.entity.Order;
import com.mybatisplus.entity.OrderItem;
import com.mybatisplus.service.OrderItemService;
import com.mybatisplus.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/orderItem")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;

    @GetMapping("/list")
    public Result<List<OrderItem>> list(@RequestParam(required = false) String orderNo,
                                        @RequestParam(required = false) Integer orderId) {
        if (orderNo != null) {
            return Result.success(orderItemService.lambdaQuery()
                    .eq(OrderItem::getOrderNo, orderNo)
                    .list());
        }
        if (orderId != null) {
            Order order = orderService.getById(orderId);
            if (order != null && order.getOrderNo() != null) {
                return Result.success(orderItemService.lambdaQuery()
                        .eq(OrderItem::getOrderNo, order.getOrderNo())
                        .list());
            }
            return Result.success(Collections.emptyList());
        }
        return Result.success(orderItemService.list());
    }

    @GetMapping("/get/{id}")
    public Result<OrderItem> get(@PathVariable Integer id) {
        OrderItem item = orderItemService.getById(id);
        if (item == null) {
            return Result.error("订单项不存在");
        }
        return Result.success(item);
    }

    @PostMapping("/add")
    public Result<OrderItem> add(@RequestBody OrderItem orderItem) {
        orderItemService.save(orderItem);
        return Result.success("添加成功", orderItem);
    }

    @PutMapping("/update")
    public Result<OrderItem> update(@RequestBody OrderItem orderItem) {
        orderItemService.updateById(orderItem);
        return Result.success("更新成功", orderItem);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        orderItemService.removeById(id);
        return Result.success();
    }
}
