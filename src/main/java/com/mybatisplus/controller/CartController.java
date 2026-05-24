package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final GoodsService goodsService;

    /**
     * 获取用户的购物车列表
     */
    @GetMapping("/list")
    public Result<List<Cart>> list(@RequestParam Integer userId) {
        List<Cart> list = cartService.lambdaQuery()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public Result<Cart> add(@RequestBody Cart cart) {
        Goods goods = goodsService.getById(cart.getGoodsId());
        if (goods == null) {
            return Result.error("商品不存在");
        }
        if (goods.getStatus() == 0) {
            return Result.error("商品已下架");
        }
        if (goods.getStock() < cart.getNum()) {
            return Result.error("库存不足");
        }

        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, cart.getUserId())
               .eq(Cart::getGoodsId, cart.getGoodsId());
        Cart existCart = cartService.getOne(wrapper);
        
        if (existCart != null) {
            int newNum = existCart.getNum() + cart.getNum();
            if (newNum > goods.getStock()) {
                return Result.error("超出库存限制");
            }
            existCart.setNum(newNum);
            existCart.setUpdateTime(LocalDateTime.now());
            cartService.updateById(existCart);
            return Result.success(existCart);
        }
        
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        cartService.save(cart);
        return Result.success(cart);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public Result<Cart> update(@RequestBody Cart cart) {
        Goods goods = goodsService.getById(cart.getGoodsId());
        if (goods == null) {
            return Result.error("商品不存在");
        }
        if (cart.getNum() > goods.getStock()) {
            return Result.error("库存不足");
        }
        
        cart.setUpdateTime(LocalDateTime.now());
        cartService.updateById(cart);
        return Result.success(cart);
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        cartService.removeById(id);
        return Result.success();
    }

    /**
     * 清空用户购物车
     */
    @DeleteMapping("/clear/{userId}")
    public Result<Void> clear(@PathVariable Integer userId) {
        cartService.lambdaUpdate()
                .eq(Cart::getUserId, userId)
                .remove();
        return Result.success();
    }
}
