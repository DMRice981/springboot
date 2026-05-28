package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Constants;
import com.mybatisplus.common.Result;
import com.mybatisplus.dto.GoodsWithSellerVO;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.entity.Seller;
import com.mybatisplus.service.GoodsService;
import com.mybatisplus.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;
    private final SellerService sellerService;

    @GetMapping("/list")
    public Result<List<Goods>> list(@RequestParam(required = false) Integer categoryId,
                                     @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getIsDelete, Constants.Status.NOT_DELETED)
               .eq(Goods::getStatus, Constants.GoodsStatus.ON_SHELF);
        
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

    @GetMapping("/list/all")
    public Result<List<GoodsWithSellerVO>> listAll(@RequestParam(required = false) Integer sellerId,
                                                    @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getIsDelete, Constants.Status.NOT_DELETED);
        
        if (sellerId != null) {
            wrapper.eq(Goods::getSellerId, sellerId);
        }
        
        if (status != null) {
            wrapper.eq(Goods::getStatus, status);
        }
        
        wrapper.orderByDesc(Goods::getCreateTime);
        List<Goods> goodsList = goodsService.list(wrapper);
        
        // 获取所有相关的商家信息
        List<Integer> sellerIds = goodsList.stream()
                .map(Goods::getSellerId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Integer, Seller> sellerMap = sellerIds.isEmpty() ? 
                Map.of() : 
                sellerService.listByIds(sellerIds).stream()
                        .collect(Collectors.toMap(Seller::getId, s -> s));
        
        // 转换为 VO
        List<GoodsWithSellerVO> voList = new ArrayList<>();
        for (Goods goods : goodsList) {
            GoodsWithSellerVO vo = new GoodsWithSellerVO();
            BeanUtils.copyProperties(goods, vo);
            
            if (goods.getSellerId() != null && sellerMap.containsKey(goods.getSellerId())) {
                Seller seller = sellerMap.get(goods.getSellerId());
                vo.setSellerName(seller.getUsername());
                vo.setShopName(seller.getShopName());
            }
            
            voList.add(vo);
        }
        
        return Result.success(voList);
    }

    @GetMapping("/get/{id}")
    public Result<Goods> get(@PathVariable Integer id) {
        Goods goods = goodsService.getById(id);
        if (goods == null || goods.getIsDelete().equals(Constants.Status.DELETED)) {
            return Result.error("商品不存在");
        }
        return Result.success(goods);
    }

    @GetMapping("/my")
    public Result<List<Goods>> myGoods(HttpSession session) {
        Seller seller = (Seller) session.getAttribute("seller");
        if (seller == null) {
            return Result.error("请先登录商家账号");
        }
        List<Goods> list = goodsService.lambdaQuery()
                .eq(Goods::getSellerId, seller.getId())
                .eq(Goods::getIsDelete, Constants.Status.NOT_DELETED)
                .orderByDesc(Goods::getCreateTime)
                .list();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<Goods> add(@RequestBody Goods goodsFromFront, HttpSession session) {
        Seller seller = (Seller) session.getAttribute("seller");
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
        goods.setStatus(goodsFromFront.getStatus() != null ? goodsFromFront.getStatus() : Constants.GoodsStatus.ON_SHELF);
        goods.setIsDelete(Constants.Status.NOT_DELETED);
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        
        // 如果有商家登录，使用登录商家的ID；否则使用前端传过来的sellerId
        if (seller != null) {
            goods.setSellerId(seller.getId());
        } else {
            goods.setSellerId(goodsFromFront.getSellerId());
        }
        
        goodsService.save(goods);
        return Result.success("添加成功", goods);
    }

    @PutMapping("/update")
    public Result<Goods> update(@RequestBody Goods goodsFromFront, HttpSession session) {
        if (goodsFromFront.getId() == null) {
            return Result.error("商品ID不能为空");
        }
        
        Goods goods = goodsService.getById(goodsFromFront.getId());
        if (goods == null) {
            return Result.error("商品不存在");
        }
        
        Seller seller = (Seller) session.getAttribute("seller");
        // 如果是商家登录，检查是否有权限
        if (seller != null && !goods.getSellerId().equals(seller.getId())) {
            return Result.error("您无权操作该商品");
        }
        
        goods.setCategoryId(goodsFromFront.getCategoryId());
        goods.setGoodsName(goodsFromFront.getGoodsName());
        goods.setPrice(goodsFromFront.getPrice());
        goods.setMarketPrice(goodsFromFront.getMarketPrice());
        goods.setStock(goodsFromFront.getStock());
        goods.setGoodsImg(goodsFromFront.getGoodsImg());
        goods.setGoodsDesc(goodsFromFront.getGoodsDesc());
        goods.setStatus(goodsFromFront.getStatus() != null ? goodsFromFront.getStatus() : goods.getStatus());
        
        // 管理员可以修改商家
        if (seller == null && goodsFromFront.getSellerId() != null) {
            goods.setSellerId(goodsFromFront.getSellerId());
        }
        
        goods.setUpdateTime(LocalDateTime.now());
        
        goodsService.updateById(goods);
        return Result.success("更新成功", goods);
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestParam Integer id, @RequestParam Integer status, HttpSession session) {
        if (id == null) {
            return Result.error("商品ID不能为空");
        }
        if (status == null || (status != Constants.GoodsStatus.ON_SHELF && status != Constants.GoodsStatus.OFF_SHELF)) {
            return Result.error("状态值无效");
        }
        
        Seller seller = (Seller) session.getAttribute("seller");
        if (seller == null) {
            return Result.error("请先登录商家账号");
        }
        
        Goods goods = goodsService.getById(id);
        if (goods == null) {
            return Result.error("商品不存在");
        }
        
        if (!goods.getSellerId().equals(seller.getId())) {
            return Result.error("您无权操作该商品");
        }
        
        goodsService.lambdaUpdate()
                .eq(Goods::getId, id)
                .set(Goods::getStatus, status)
                .set(Goods::getUpdateTime, LocalDateTime.now())
                .update();
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpSession session) {
        Goods goods = goodsService.getById(id);
        if (goods == null) {
            return Result.error("商品不存在");
        }
        
        Seller seller = (Seller) session.getAttribute("seller");
        // 如果是商家登录，检查是否有权限
        if (seller != null && !goods.getSellerId().equals(seller.getId())) {
            return Result.error("您无权操作该商品");
        }
        
        goodsService.lambdaUpdate()
                .eq(Goods::getId, id)
                .set(Goods::getIsDelete, Constants.Status.DELETED)
                .set(Goods::getUpdateTime, LocalDateTime.now())
                .update();
        return Result.success();
    }

    @GetMapping("/search")
    public Result<List<Goods>> search(@RequestParam String keyword) {
        List<Goods> list = goodsService.lambdaQuery()
                .eq(Goods::getIsDelete, Constants.Status.NOT_DELETED)
                .eq(Goods::getStatus, Constants.GoodsStatus.ON_SHELF)
                .and(w -> w.like(Goods::getGoodsName, keyword)
                          .or()
                          .like(Goods::getGoodsDesc, keyword))
                .orderByDesc(Goods::getSales)
                .list();
        return Result.success(list);
    }
}
