package com.mybatisplus.controller;

import com.mybatisplus.entity.Seller;
import com.mybatisplus.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Seller seller) {
        Map<String, Object> res = new HashMap<>();

        long count = sellerService.lambdaQuery()
                .eq(Seller::getUsername, seller.getUsername())
                .count();
        if (count > 0) {
            res.put("code", 500);
            res.put("msg", "账号已存在");
            return res;
        }

        seller.setCreateTime(new Date());
        sellerService.save(seller);

        res.put("code", 200);
        res.put("msg", "注册成功");
        return res;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Seller seller) {
        Map<String, Object> res = new HashMap<>();

        Seller one = sellerService.lambdaQuery()
                .eq(Seller::getUsername, seller.getUsername())
                .eq(Seller::getPassword, seller.getPassword())
                .one();

        if (one != null) {
            res.put("code", 200);
            res.put("data", one);
        } else {
            res.put("code", 500);
            res.put("msg", "账号或密码错误");
        }
        return res;
    }
}