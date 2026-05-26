package com.mybatisplus.controller;

import com.mybatisplus.common.Result;
import com.mybatisplus.entity.OrderItem;
import com.mybatisplus.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderItem")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/list")
    public Result<List<OrderItem>> list(@RequestParam(required = false) String orderNo) {
        if (orderNo != null) {
            return Result.success(orderItemService.lambdaQuery()
                    .eq(OrderItem::getOrderNo, orderNo)
                    .list());
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
