package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Constants;
import com.mybatisplus.common.Result;
import com.mybatisplus.entity.Cart;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.service.CartService;
import com.mybatisplus.service.GoodsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "购物车模块", description = "购物车管理")
public class CartController {

    private final CartService cartService;
    private final GoodsService goodsService;

    @GetMapping("/list")
    @Tag(name = "获取购物车列表", description = "获取当前用户的购物车商品列表")
    public Result<List<Cart>> list(@RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        List<Cart> list = cartService.lambdaQuery()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime)
                .list();

        // 关联商品信息填充商品名称/图片/价格等
        for (Cart cart : list) {
            Goods goods = goodsService.getById(cart.getGoodsId());
            if (goods != null) {
                cart.setGoodsName(goods.getGoodsName());
                cart.setGoodsImg(goods.getGoodsImg());
                cart.setPrice(goods.getPrice());
                cart.setStock(goods.getStock());
                cart.setStatus(goods.getStatus());
            }
        }
        return Result.success(list);
    }

    @PostMapping("/add")
    @Transactional
    public Result<Cart> add(@RequestBody Cart cart) {
        if (cart.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (cart.getGoodsId() == null) {
            return Result.error("商品ID不能为空");
        }
        if (cart.getNum() == null || cart.getNum() <= 0) {
            return Result.error("数量必须大于0");
        }

        Goods goods = goodsService.getById(cart.getGoodsId());
        if (goods == null) {
            return Result.error("商品不存在");
        }
        if (goods.getStatus() != Constants.GoodsStatus.ON_SHELF) {
            return Result.error("商品已下架");
        }
        if (goods.getIsDelete() != null && goods.getIsDelete() == Constants.Status.DELETED) {
            return Result.error("商品已删除");
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
            // 填充商品信息（复用已查询结果）
            existCart.setGoodsName(goods.getGoodsName());
            existCart.setGoodsImg(goods.getGoodsImg());
            existCart.setPrice(goods.getPrice());
            existCart.setStock(goods.getStock());
            existCart.setStatus(goods.getStatus());
            return Result.success("添加成功", existCart);
        } else {
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            cartService.save(cart);
            // 填充商品信息（复用已查询结果）
            cart.setGoodsName(goods.getGoodsName());
            cart.setGoodsImg(goods.getGoodsImg());
            cart.setPrice(goods.getPrice());
            cart.setStock(goods.getStock());
            cart.setStatus(goods.getStatus());
            return Result.success("添加成功", cart);
        }
    }

    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public Result<Cart> update(@RequestBody Cart cart) {
        if (cart.getId() == null) {
            return Result.error("购物车ID不能为空");
        }
        Cart existCart = cartService.getById(cart.getId());
        if (existCart == null) {
            return Result.error("购物车记录不存在");
        }
        if (cart.getNum() != null && cart.getNum() <= 0) {
            return Result.error("数量必须大于0");
        }

        // 校验库存
        if (cart.getNum() != null) {
            Goods goods = goodsService.getById(existCart.getGoodsId());
            if (goods == null) {
                return Result.error("商品不存在");
            }
            if (goods.getStock() < cart.getNum()) {
                return Result.error("库存不足");
            }
            existCart.setNum(cart.getNum());
        }
        existCart.setUpdateTime(LocalDateTime.now());
        cartService.updateById(existCart);

        // 填充商品信息
        Goods goods = goodsService.getById(existCart.getGoodsId());
        if (goods != null) {
            existCart.setGoodsName(goods.getGoodsName());
            existCart.setGoodsImg(goods.getGoodsImg());
            existCart.setPrice(goods.getPrice());
            existCart.setStock(goods.getStock());
            existCart.setStatus(goods.getStatus());
        }
        return Result.success("更新成功", existCart);
    }

    /**
     * 兼容前端：按 ID 更新购物车数量
     * PUT /cart/update/{id}?num=N  或  body: { num: N }
     */
    @PutMapping("/update/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Cart> updateById(
            @PathVariable Integer id,
            @RequestBody(required = false) java.util.Map<String, Object> body) {
        if (id == null) {
            return Result.error("购物车ID不能为空");
        }
        Cart existCart = cartService.getById(id);
        if (existCart == null) {
            return Result.error("购物车记录不存在");
        }
        Integer num = null;
        if (body != null && body.get("num") != null) {
            num = ((Number) body.get("num")).intValue();
        }
        if (num != null && num <= 0) {
            return Result.error("数量必须大于0");
        }

        // 校验库存
        if (num != null) {
            Goods goods = goodsService.getById(existCart.getGoodsId());
            if (goods == null) {
                return Result.error("商品不存在");
            }
            if (goods.getStock() < num) {
                return Result.error("库存不足");
            }
            existCart.setNum(num);
        }
        existCart.setUpdateTime(LocalDateTime.now());
        cartService.updateById(existCart);

        // 填充商品信息
        Goods goods = goodsService.getById(existCart.getGoodsId());
        if (goods != null) {
            existCart.setGoodsName(goods.getGoodsName());
            existCart.setGoodsImg(goods.getGoodsImg());
            existCart.setPrice(goods.getPrice());
            existCart.setStock(goods.getStock());
            existCart.setStatus(goods.getStatus());
        }
        return Result.success("更新成功", existCart);
    }

    @DeleteMapping("/delete/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Integer id) {
        Cart cart = cartService.getById(id);
        if (cart == null) {
            return Result.error("购物车记录不存在");
        }
        cartService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/clear")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> clear(@RequestParam Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        cartService.lambdaUpdate()
                .eq(Cart::getUserId, userId)
                .remove();
        return Result.success();
    }
}
