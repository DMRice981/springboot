package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.entity.AfterSale;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.entity.Order;
import com.mybatisplus.service.AfterSaleService;
import com.mybatisplus.service.GoodsService;
import com.mybatisplus.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/after-sale")
@RequiredArgsConstructor
public class AfterSaleController {

    private final AfterSaleService afterSaleService;
    private final OrderService orderService;
    private final GoodsService goodsService;

    /**
     * 创建售后申请
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody AfterSale afterSale) {
        Map<String, Object> res = new HashMap<>();
        try {
            afterSale.setStatus(0);
            afterSale.setCreateTime(LocalDateTime.now());
            
            // 获取订单信息，设置sellerId
            if (afterSale.getOrderId() != null) {
                Order order = orderService.getById(afterSale.getOrderId());
                if (order != null) {
                    // 通过订单商品获取sellerId
                    // 这里简化处理，实际应从订单项获取
                    if (afterSale.getGoodsId() != null) {
                        Goods goods = goodsService.getById(afterSale.getGoodsId());
                        if (goods != null) {
                            afterSale.setSellerId(goods.getSellerId());
                        }
                    }
                }
            }
            
            afterSaleService.save(afterSale);
            res.put("code", 200);
            res.put("msg", "申请成功");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", "申请失败：" + e.getMessage());
        }
        return res;
    }

    /**
     * 获取用户的售后列表
     */
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) Integer userId,
                                    @RequestParam(required = false) Integer sellerId) {
        Map<String, Object> res = new HashMap<>();
        LambdaQueryWrapper<AfterSale> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(AfterSale::getUserId, userId);
        }
        if (sellerId != null) {
            wrapper.eq(AfterSale::getSellerId, sellerId);
        }
        
        wrapper.orderByDesc(AfterSale::getCreateTime);
        List<AfterSale> list = afterSaleService.list(wrapper);
        res.put("code", 200);
        res.put("data", list);
        return res;
    }

    /**
     * 处理售后（商家端）
     */
    @PostMapping("/handle")
    public Map<String, Object> handle(@RequestParam Integer id) {
        Map<String, Object> res = new HashMap<>();
        AfterSale afterSale = afterSaleService.getById(id);
        if (afterSale == null) {
            res.put("code", 404);
            res.put("msg", "售后不存在");
            return res;
        }
        
        afterSale.setStatus(1);
        afterSaleService.updateById(afterSale);
        res.put("code", 200);
        res.put("msg", "处理成功");
        return res;
    }

    /**
     * 获取售后详情
     */
    @GetMapping("/get/{id}")
    public AfterSale get(@PathVariable Integer id) {
        return afterSaleService.getById(id);
    }

    /**
     * 删除售后
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        Map<String, Object> res = new HashMap<>();
        boolean success = afterSaleService.removeById(id);
        res.put("code", success ? 200 : 500);
        return res;
    }
}
