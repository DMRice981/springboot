package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.dto.OrderDTO;
import com.mybatisplus.entity.Order;
import com.mybatisplus.entity.OrderItem;
import com.mybatisplus.service.OrderItemService;
import com.mybatisplus.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping("/list")
    public List<OrderDTO> list(){
        List<Order> orders = orderService.list();
        List<OrderDTO> orderDTOList = new ArrayList<>();
        
        for (Order order : orders) {
            LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderItem::getOrderNo, order.getOrderNo());
            List<OrderItem> orderItems = orderItemService.list(wrapper);
            
            orderDTOList.add(new OrderDTO(order, orderItems));
        }
        
        return orderDTOList;
    }

    @GetMapping("/get/{id}")
    public Order get(@PathVariable Integer id){
        return orderService.getById(id);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody Order order){
        return orderService.save(order);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Order order){
        return orderService.updateById(order);
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Integer id){
        Map<String, Object> res = new HashMap<>();
        boolean success = orderService.removeById(id);
        res.put("code", success ? 200 : 500);
        return res;
    }
}