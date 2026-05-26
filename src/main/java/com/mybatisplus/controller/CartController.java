package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Constants;
import com.mybatisplus.common.Result;
import com.mybatisplus.entity.Cart;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.service.CartService;
import com.mybatisplus.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final GoodsService goodsService;

    @GetMapping("/list")
    public Result<List<Cart>> list(@RequestParam Integer userId) {
        List<Cart> list = cartService.lambdaQuery()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime)
                .list();
        return Result.success(list);
    }

    @PostMapping("/add")
    @Transactional
    public Result<Cart> add(@RequestBody Cart cart) {
        Goods goods = goodsService.getById(cart.getGoodsId());
        if (goods == null || goods.getStatus().equals(Constants.GoodsStatus.OFF_SHELF)) {
            return Result.error("商品不存在或已下架");
        }

        if (goods.getStock() < cart.getNum()) {
            return Result.error("库存不足");
        }

        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, cart.getUserId())
               .eq(Cart::getGoodsId, cart.getGoodsId());
        Cart existCart = cartService.getOne(wrapper);

        if (existCart != null) {
            existCart.setNum(existCart.getNum() + cart.getNum());
            existCart.setUpdateTime(LocalDateTime.now());
            cartService.updateById(existCart);
            return Result.success("添加成功", existCart);
        } else {
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            cartService.save(cart);
            return Result.success("添加成功", cart);
        }
    }

    @PutMapping("/update")
    public Result<Cart> update(@RequestBody Cart cart) {
        cart.setUpdateTime(LocalDateTime.now());
        cartService.updateById(cart);
        return Result.success("更新成功", cart);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        cartService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/clear")
    public Result<Void> clear(@RequestParam Integer userId) {
        cartService.lambdaUpdate()
                .eq(Cart::getUserId, userId)
                .remove();
        return Result.success();
    }
}
