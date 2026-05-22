package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.dto.AfterSaleDTO;
import com.mybatisplus.entity.AfterSale;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.service.AfterSaleService;
import com.mybatisplus.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/after-sale")
@RequiredArgsConstructor
public class AfterSaleController {

    private final AfterSaleService afterSaleService;
    private final GoodsService goodsService;

    /**
     * 创建售后申请
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody AfterSale afterSale) {
        Map<String, Object> res = new HashMap<>();
        try {
            // 校验必填字段
            if (afterSale.getUserId() == null) {
                res.put("code", 400);
                res.put("msg", "用户ID不能为空");
                return res;
            }
            if (afterSale.getOrderId() == null) {
                res.put("code", 400);
                res.put("msg", "订单ID不能为空");
                return res;
            }
            if (afterSale.getGoodsId() == null) {
                res.put("code", 400);
                res.put("msg", "商品ID不能为空");
                return res;
            }
            if (afterSale.getReason() == null || afterSale.getReason().trim().isEmpty()) {
                res.put("code", 400);
                res.put("msg", "售后原因不能为空");
                return res;
            }
            
            afterSale.setStatus(0);
            afterSale.setCreateTime(LocalDateTime.now());
            
            // 通过商品获取sellerId
            Goods goods = goodsService.getById(afterSale.getGoodsId());
            if (goods != null) {
                afterSale.setSellerId(goods.getSellerId());
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
     * 获取用户的售后列表（包含商品信息）
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
        
        // 组装DTO，包含商品信息
        List<AfterSaleDTO> dtoList = new ArrayList<>();
        for (AfterSale afterSale : list) {
            AfterSaleDTO dto = new AfterSaleDTO();
            dto.setAfterSale(afterSale);
            
            if (afterSale.getGoodsId() != null) {
                Goods goods = goodsService.getById(afterSale.getGoodsId());
                dto.setGoods(goods);
            }
            
            dtoList.add(dto);
        }
        
        res.put("code", 200);
        res.put("data", dtoList);
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
