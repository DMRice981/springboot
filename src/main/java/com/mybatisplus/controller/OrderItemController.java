package com.mybatisplus.controller;

import com.mybatisplus.entity.OrderItem;
import com.mybatisplus.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orderItem")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/list")
    public List<OrderItem> list(){
        return orderItemService.list();
    }

    @GetMapping("/get/{id}")
    public OrderItem get(@PathVariable Integer id){
        return orderItemService.getById(id);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody OrderItem orderItem){
        return orderItemService.save(orderItem);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody OrderItem orderItem){
        return orderItemService.updateById(orderItem);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return orderItemService.removeById(id);
    }
}