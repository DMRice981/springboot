package com.mybatisplus.controller;

import com.mybatisplus.entity.Cart;
import com.mybatisplus.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/list")
    public List<Cart> list(){
        return cartService.list();
    }

    @GetMapping("/get/{id}")
    public Cart get(@PathVariable Integer id){
        return cartService.getById(id);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody Cart cart){
        return cartService.save(cart);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Cart cart){
        return cartService.updateById(cart);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return cartService.removeById(id);
    }
}