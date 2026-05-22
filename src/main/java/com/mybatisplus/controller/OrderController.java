package com.mybatisplus.controller;

import com.mybatisplus.entity.Order;
import com.mybatisplus.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public List<Order> list(){
        // 如果以后要逻辑删除，可加 .eq(Order::getIsDelete, 0)
        return orderService.list();
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