package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mybatisplus.common.Result;
import com.mybatisplus.dto.AfterSaleDTO;
import com.mybatisplus.dto.PageResult;
import com.mybatisplus.entity.AfterSale;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.entity.Order;
import com.mybatisplus.service.AfterSaleService;
import com.mybatisplus.service.GoodsService;
import com.mybatisplus.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/after-sale")
@RequiredArgsConstructor
public class AfterSaleController {

    private final AfterSaleService afterSaleService;
    private final GoodsService goodsService;
    private final OrderService orderService;

    @PostMapping("/add")
    public Result<AfterSale> add(@RequestBody AfterSale afterSale) {
        if (afterSale.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (afterSale.getOrderId() == null) {
            return Result.error("订单ID不能为空");
        }
        if (afterSale.getGoodsId() == null) {
            return Result.error("商品ID不能为空");
        }
        if (afterSale.getReason() == null || afterSale.getReason().trim().isEmpty()) {
            return Result.error("售后原因不能为空");
        }
        
        Order order = orderService.getById(afterSale.getOrderId());
        if (order != null) {
            afterSale.setOrderNo(order.getOrderNo());
        }
        
        afterSale.setStatus(0);
        afterSale.setCreateTime(LocalDateTime.now());
        
        Goods goods = goodsService.getById(afterSale.getGoodsId());
        if (goods != null) {
            afterSale.setSellerId(goods.getSellerId());
        }
        
        afterSaleService.save(afterSale);
        return Result.success("申请成功", afterSale);
    }

    @GetMapping("/list")
    public Result<List<AfterSaleDTO>> list(@RequestParam(required = false) Integer userId,
                                           @RequestParam(required = false) Integer sellerId) {
        LambdaQueryWrapper<AfterSale> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(AfterSale::getUserId, userId);
        }
        if (sellerId != null) {
            wrapper.eq(AfterSale::getSellerId, sellerId);
        }
        
        wrapper.orderByDesc(AfterSale::getCreateTime);
        List<AfterSale> list = afterSaleService.list(wrapper);
        
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
        
        return Result.success(dtoList);
    }

    /**
     * 分页获取售后列表
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param userId 用户ID（可选）
     * @param sellerId 商家ID（可选）
     * @param status 售后状态（可选）
     * @return 分页后的售后列表
     */
    @GetMapping("/list/paged")
    public Result<PageResult<AfterSaleDTO>> listPaged(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer sellerId,
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<AfterSale> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(AfterSale::getUserId, userId);
        }
        if (sellerId != null) {
            wrapper.eq(AfterSale::getSellerId, sellerId);
        }
        if (status != null) {
            wrapper.eq(AfterSale::getStatus, status);
        }
        
        wrapper.orderByDesc(AfterSale::getCreateTime);
        
        Page<AfterSale> page = new Page<>(pageNum, pageSize);
        IPage<AfterSale> pageResult = afterSaleService.page(page, wrapper);
        
        List<AfterSaleDTO> dtoList = new ArrayList<>();
        for (AfterSale afterSale : pageResult.getRecords()) {
            AfterSaleDTO dto = new AfterSaleDTO();
            dto.setAfterSale(afterSale);
            
            if (afterSale.getGoodsId() != null) {
                Goods goods = goodsService.getById(afterSale.getGoodsId());
                dto.setGoods(goods);
            }
            
            dtoList.add(dto);
        }
        
        PageResult<AfterSaleDTO> result = new PageResult<>(
                pageResult.getTotal(),
                pageNum,
                pageSize,
                dtoList
        );
        
        return Result.success(result);
    }

    @PostMapping("/handle")
    public Result<Void> handle(@RequestParam Integer id) {
        AfterSale afterSale = afterSaleService.getById(id);
        if (afterSale == null) {
            return Result.error("售后不存在");
        }
        
        afterSale.setStatus(1);
        afterSaleService.updateById(afterSale);
        return Result.successMsg("处理成功");
    }

    @GetMapping("/get/{id}")
    public Result<AfterSale> get(@PathVariable Integer id) {
        AfterSale afterSale = afterSaleService.getById(id);
        if (afterSale == null) {
            return Result.error("售后不存在");
        }
        return Result.success(afterSale);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = afterSaleService.removeById(id);
        if (!success) {
            return Result.error("删除失败");
        }
        return Result.success();
    }
}
