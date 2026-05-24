package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    /**
     * 原有接口：根据卖家ID查询商品（只查未删除的）
     */
    @GetMapping("/my")
    public Map<String, Object> myGoods(@RequestParam Integer sellerId) {
        Map<String, Object> res = new HashMap<>();
        List<Goods> list = goodsService.lambdaQuery()
                .eq(Goods::getSellerId, sellerId)
                .eq(Goods::getIsDelete, 0)
                .list();
        res.put("code", 200);
        res.put("data", list);
        return res;
    }

    /**
     * 原有接口：更新商品状态（上架/下架）
     */
    @PostMapping("/status")
    public Map<String, Object> updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        goodsService.lambdaUpdate()
                .set(Goods::getStatus, status)
                .set(Goods::getUpdateTime, LocalDateTime.now())
                .eq(Goods::getId, id)
                .update();
        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", "操作成功");
        return res;
    }

    // ==================== 新增接口（供前端商品管理调用） ====================

    /**
     * 1. 获取所有未删除且已上架的商品列表（用户端）
     * 前端期望直接返回数组
     */
    @GetMapping("/list")
    public List<Goods> list() {
        return goodsService.lambdaQuery()
                .eq(Goods::getIsDelete, 0)   // 只查未删除的
                .eq(Goods::getStatus, 1)     // 只查已上架的
                .orderByDesc(Goods::getCreateTime)
                .list();
    }

    /**
     * 获取所有未删除的商品列表（包含下架商品，管理端）
     */
    @GetMapping("/list/all")
    public List<Goods> listAll(@RequestParam(required = false) Integer sellerId) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getIsDelete, 0);
        if (sellerId != null) {
            wrapper.eq(Goods::getSellerId, sellerId);
        }
        wrapper.orderByDesc(Goods::getCreateTime);
        return goodsService.list(wrapper);
    }

    /**
     * 2. 新增商品
     * 前端传 { goodsName, price, stock, categoryId, sellerId, goodsImg, goodsDesc }
     * 后端设置默认值（图片、市场价、销量、描述、状态等）
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Goods goodsFromFront) {
        Map<String, Object> res = new HashMap<>();
        try {
            Goods goods = new Goods();
            goods.setGoodsName(goodsFromFront.getGoodsName());
            goods.setPrice(goodsFromFront.getPrice());
            goods.setStock(goodsFromFront.getStock());
            goods.setCategoryId(goodsFromFront.getCategoryId());
            goods.setSellerId(goodsFromFront.getSellerId());  // 从前端传入
            goods.setGoodsImg(goodsFromFront.getGoodsImg());  // 从前端传入
            goods.setGoodsDesc(goodsFromFront.getGoodsDesc()); // 从前端传入

            // 设置默认值
            goods.setMarketPrice(BigDecimal.ZERO);       // 市场价默认为0
            goods.setSales(0);                           // 初始销量0
            goods.setStatus(1);                          // 1-上架
            goods.setIsDelete(0);
            goods.setCreateTime(LocalDateTime.now());
            goods.setUpdateTime(LocalDateTime.now());

            goodsService.save(goods);
            res.put("code", 200);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "新增失败：" + e.getMessage());
        }
        return res;
    }

    /**
     * 3. 更新商品
     * 前端传 { id, goodsName, price, stock, categoryId, goodsImg, goodsDesc }
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Goods goodsFromFront) {
        Map<String, Object> res = new HashMap<>();
        if (goodsFromFront.getId() == null) {
            res.put("code", 400);
            res.put("msg", "id不能为空");
            return res;
        }

        LambdaUpdateWrapper<Goods> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Goods::getId, goodsFromFront.getId())
                .set(Goods::getGoodsName, goodsFromFront.getGoodsName())
                .set(Goods::getPrice, goodsFromFront.getPrice())
                .set(Goods::getStock, goodsFromFront.getStock())
                .set(Goods::getCategoryId, goodsFromFront.getCategoryId())
                .set(Goods::getGoodsImg, goodsFromFront.getGoodsImg())
                .set(Goods::getGoodsDesc, goodsFromFront.getGoodsDesc())
                .set(Goods::getUpdateTime, LocalDateTime.now());

        boolean success = goodsService.update(updateWrapper);
        res.put("code", success ? 200 : 500);
        return res;
    }

    /**
     * 4. 删除商品（逻辑删除）
     * 前端调用 DELETE /api/goods/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        Map<String, Object> res = new HashMap<>();
        boolean success = goodsService.lambdaUpdate()
                .set(Goods::getIsDelete, 1)
                .set(Goods::getUpdateTime, LocalDateTime.now())
                .eq(Goods::getId, id)
                .update();
        res.put("code", success ? 200 : 500);
        if (!success) {
            res.put("msg", "删除失败，商品可能不存在");
        }
        return res;
    }

    /**
     * 5. 获取单个商品详情
     * 前端调用 GET /api/goods/get/{id}
     */
    @GetMapping("/get/{id}")
    public Goods get(@PathVariable Integer id) {
        return goodsService.getById(id);
    }
}