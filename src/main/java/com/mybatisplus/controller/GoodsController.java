package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Result;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    /**
     * 获取所有上架商品（用户端）
     */
    @GetMapping("/list")
    public Result<List<Goods>> list(@RequestParam(required = false) Integer categoryId,
                                     @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getIsDelete, 0)
               .eq(Goods::getStatus, 1);
        
        if (categoryId != null) {
            wrapper.eq(Goods::getCategoryId, categoryId);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Goods::getGoodsName, keyword)
                               .or()
                               .like(Goods::getGoodsDesc, keyword));
        }
        
        wrapper.orderByDesc(Goods::getSales)
               .orderByDesc(Goods::getCreateTime);
        
        return Result.success(goodsService.list(wrapper));
    }

    /**
     * 获取所有商品（管理端）
     */
    @GetMapping("/list/all")
    public Result<List<Goods>> listAll(@RequestParam(required = false) Integer sellerId,
                                        @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getIsDelete, 0);
        
        if (sellerId != null) {
            wrapper.eq(Goods::getSellerId, sellerId);
        }
        
        if (status != null) {
            wrapper.eq(Goods::getStatus, status);
        }
        
        wrapper.orderByDesc(Goods::getCreateTime);
        return Result.success(goodsService.list(wrapper));
    }

    /**
     * 获取单个商品详情
     */
    @GetMapping("/get/{id}")
    public Result<Goods> get(@PathVariable Integer id) {
        Goods goods = goodsService.getById(id);
        if (goods == null || goods.getIsDelete() == 1) {
            return Result.error("商品不存在");
        }
        return Result.success(goods);
    }

    /**
     * 获取商家商品
     */
    @GetMapping("/my")
    public Result<List<Goods>> myGoods(@RequestParam Integer sellerId) {
        List<Goods> list = goodsService.lambdaQuery()
                .eq(Goods::getSellerId, sellerId)
                .eq(Goods::getIsDelete, 0)
                .orderByDesc(Goods::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 添加商品
     */
    @PostMapping("/add")
    public Result<Goods> add(@RequestBody Goods goodsFromFront) {
        Goods goods = new Goods();
        goods.setCategoryId(goodsFromFront.getCategoryId());
        goods.setGoodsName(goodsFromFront.getGoodsName());
        goods.setPrice(goodsFromFront.getPrice());
        goods.setMarketPrice(goodsFromFront.getMarketPrice() != null ? 
                            goodsFromFront.getMarketPrice() : BigDecimal.ZERO);
        goods.setStock(goodsFromFront.getStock());
        goods.setSales(0);
        goods.setGoodsImg(goodsFromFront.getGoodsImg());
        goods.setGoodsDesc(goodsFromFront.getGoodsDesc());
        goods.setSellerId(goodsFromFront.getSellerId());
        goods.setStatus(1);
        goods.setIsDelete(0);
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        
        goodsService.save(goods);
        return Result.success("添加成功", goods);
    }

    /**
     * 更新商品
     */
    @PutMapping("/update")
    public Result<Goods> update(@RequestBody Goods goodsFromFront) {
        if (goodsFromFront.getId() == null) {
            return Result.error("商品ID不能为空");
        }
        
        Goods goods = goodsService.getById(goodsFromFront.getId());
        if (goods == null) {
            return Result.error("商品不存在");
        }
        
        goods.setCategoryId(goodsFromFront.getCategoryId());
        goods.setGoodsName(goodsFromFront.getGoodsName());
        goods.setPrice(goodsFromFront.getPrice());
        goods.setMarketPrice(goodsFromFront.getMarketPrice());
        goods.setStock(goodsFromFront.getStock());
        goods.setGoodsImg(goodsFromFront.getGoodsImg());
        goods.setGoodsDesc(goodsFromFront.getGoodsDesc());
        goods.setUpdateTime(LocalDateTime.now());
        
        goodsService.updateById(goods);
        return Result.success("更新成功", goods);
    }

    /**
     * 更新商品状态（上架/下架）
     */
    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        goodsService.lambdaUpdate()
                .eq(Goods::getId, id)
                .set(Goods::getStatus, status)
                .set(Goods::getUpdateTime, LocalDateTime.now())
                .update();
        return Result.success();
    }

    /**
     * 删除商品（逻辑删除）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        goodsService.lambdaUpdate()
                .eq(Goods::getId, id)
                .set(Goods::getIsDelete, 1)
                .set(Goods::getUpdateTime, LocalDateTime.now())
                .update();
        return Result.success("删除成功");
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<List<Goods>> search(@RequestParam String keyword) {
        List<Goods> list = goodsService.lambdaQuery()
                .eq(Goods::getIsDelete, 0)
                .eq(Goods::getStatus, 1)
                .and(w -> w.like(Goods::getGoodsName, keyword)
                          .or()
                          .like(Goods::getGoodsDesc, keyword))
                .orderByDesc(Goods::getSales)
                .list();
        return Result.success(list);
    }
}
