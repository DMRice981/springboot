package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mybatisplus.common.Result;
import com.mybatisplus.dto.PageResult;
import com.mybatisplus.entity.Seller;
import com.mybatisplus.service.SellerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
@Tag(name = "商家模块", description = "商家注册、登录、店铺管理")
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/register")
    @Tag(name = "商家注册", description = "商家注册接口")
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
    public Result<Seller> login(@RequestBody Seller seller, HttpSession session) {
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
            session.setAttribute("seller", one);
            return Result.success("登录成功", one);
        } else {
            return Result.error("账号或密码错误");
        }
    }

    @GetMapping("/list")
    public Result<List<Seller>> list() {
        return Result.success(sellerService.list());
    }

    /**
     * 分页获取商家列表
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param keyword 关键词搜索（可选，匹配商家名称或店铺名称）
     * @return 分页后的商家列表
     */
    @GetMapping("/list/paged")
    public Result<PageResult<Seller>> listPaged(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Seller> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Seller::getUsername, keyword)
                              .or()
                              .like(Seller::getShopName, keyword));
        }
        
        wrapper.orderByDesc(Seller::getCreateTime);
        
        Page<Seller> page = new Page<>(pageNum, pageSize);
        IPage<Seller> pageResult = sellerService.page(page, wrapper);
        
        PageResult<Seller> result = new PageResult<>(
                pageResult.getTotal(),
                pageNum,
                pageSize,
                pageResult.getRecords()
        );
        
        return Result.success(result);
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