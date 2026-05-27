package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Result;
import com.mybatisplus.entity.Seller;
import com.mybatisplus.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/register")
    public Result<Seller> register(@RequestBody Seller seller) {
        if (seller.getUsername() == null || seller.getUsername().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (seller.getPassword() == null || seller.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }

        long count = sellerService.lambdaQuery()
                .eq(Seller::getUsername, seller.getUsername())
                .count();
        if (count > 0) {
            return Result.error("账号已存在");
        }

        seller.setCreateTime(LocalDateTime.now());
        sellerService.save(seller);

        return Result.success("注册成功", seller);
    }

    @PostMapping("/login")
    public Result<Seller> login(@RequestBody Seller seller) {
        if (seller.getUsername() == null || seller.getUsername().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (seller.getPassword() == null || seller.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }

        LambdaQueryWrapper<Seller> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seller::getUsername, seller.getUsername())
               .eq(Seller::getPassword, seller.getPassword());
        Seller one = sellerService.getOne(wrapper);

        if (one != null) {
            return Result.success("登录成功", one);
        } else {
            return Result.error("账号或密码错误");
        }
    }

    @GetMapping("/list")
    public Result<List<Seller>> list() {
        return Result.success(sellerService.list());
    }

    @GetMapping("/get/{id}")
    public Result<Seller> get(@PathVariable Integer id) {
        Seller seller = sellerService.getById(id);
        if (seller == null) {
            return Result.error("商家不存在");
        }
        return Result.success(seller);
    }
}