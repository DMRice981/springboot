package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mybatisplus.common.Constants;
import com.mybatisplus.common.Result;
import com.mybatisplus.dto.GoodsWithSellerVO;
import com.mybatisplus.dto.PageRequest;
import com.mybatisplus.dto.PageResult;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品管理控制器
 *
 * <h3>功能说明</h3>
 * <ul>
 *   <li>商品列表查询（公开接口，支持分类和关键词筛选）</li>
 *   <li>商品详情查看</li>
 *   <li>商家商品管理（添加、编辑、删除、上架/下架）</li>
 *   <li>管理员商品管理（含商家信息展示）</li>
 * </ul>
 *
 * <h3>依赖服务</h3>
 * <ul>
 *   <li>{@link GoodsService} - 商品业务逻辑层</li>
 *   <li>{@link SellerService} - 商家信息查询</li>
 * </ul>
 *
 * <h3>订单状态说明</h3>
 * <ul>
 *   <li>0: 待支付 - 用户创建订单，等待支付</li>
 *   <li>1: 待发货 - 用户已支付，等待商家发货</li>
 *   <li>2: 已发货 - 商家已发货，商品配送中</li>
 *   <li>3: 已完成 - 用户确认收货，订单完成</li>
 *   <li>4: 已取消 - 订单被取消</li>
 * </ul>
 *
 * @author Aran Shop Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;
    private final SellerService sellerService;

    /**
     * 获取商品列表（公开接口）
     * 支持按分类和关键词筛选，返回在架商品
     *
     * @param categoryId 分类ID（可选）
     * @param keyword 关键词搜索（可选，匹配商品名称或描述）
     * @return 商品列表
     */
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

    /**
     * 获取所有商品列表（管理员/商家接口）
     * 支持按商家和状态筛选，返回所有未删除商品
     *
     * @param sellerId 商家ID（可选，用于商家查看自己的商品）
     * @param status 商品状态（可选，1上架 0下架）
     * @return 包含商家信息的商品列表
     */
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

    /**
     * 获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/get/{id}")
    public Result<Goods> get(@PathVariable Integer id) {
        Goods goods = goodsService.getById(id);
        if (goods == null || goods.getIsDelete().equals(Constants.Status.DELETED)) {
            return Result.error("商品不存在");
        }
        return Result.success(goods);
    }

    /**
     * 获取商家自己的商品列表
     * 需要商家登录
     *
     * @param session HTTP会话
     * @return 商家商品列表
     */
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

    /**
     * 添加商品
     * 商家登录后添加商品会自动关联商家ID
     * 管理员添加时可以指定商家ID
     *
     * @param goodsFromFront 商品信息
     * @param session HTTP会话
     * @return 添加结果
     */
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

    /**
     * 更新商品信息
     * 商家只能修改自己的商品
     * 管理员可以修改任意商品
     *
     * @param goodsFromFront 商品信息
     * @param session HTTP会话
     * @return 更新结果
     */
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

    /**
     * 更新商品状态（上架/下架）
     *
     * @param id 商品ID
     * @param status 状态（1上架 0下架）
     * @param session HTTP会话
     * @return 更新结果
     */
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
        return Result.successMsg("更新状态成功");
    }

    /**
     * 删除商品（软删除）
     * 商家只能删除自己的商品
     *
     * @param id 商品ID
     * @param session HTTP会话
     * @return 删除结果
     */
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
        return Result.successMsg("删除成功");
    }

    /**
     * 搜索商品
     *
     * @param keyword 关键词
     * @return 商品列表
     */
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

    /**
     * 分页获取商品列表（公开接口）
     * 支持按分类和关键词筛选，返回在架商品
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param categoryId 分类ID（可选）
     * @param keyword 关键词搜索（可选，匹配商品名称或描述）
     * @return 分页后的商品列表
     */
    @GetMapping("/list/paged")
    public Result<PageResult<Goods>> listPaged(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
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
        
        Page<Goods> page = new Page<>(pageNum, pageSize);
        IPage<Goods> pageResult = goodsService.page(page, wrapper);
        
        PageResult<Goods> result = new PageResult<>(
                pageResult.getTotal(),
                pageNum,
                pageSize,
                pageResult.getRecords()
        );
        
        return Result.success(result);
    }

    /**
     * 分页获取所有商品列表（管理员/商家接口）
     * 支持按商家、状态和关键词筛选
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param sellerId 商家ID（可选，用于商家查看自己的商品）
     * @param status 商品状态（可选，1上架 0下架）
     * @param keyword 关键词搜索（可选）
     * @return 分页后的商品列表（包含商家信息）
     */
    @GetMapping("/list/all/paged")
    public Result<PageResult<GoodsWithSellerVO>> listAllPaged(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer sellerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getIsDelete, Constants.Status.NOT_DELETED);
        
        if (sellerId != null) {
            wrapper.eq(Goods::getSellerId, sellerId);
        }
        
        if (status != null) {
            wrapper.eq(Goods::getStatus, status);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Goods::getGoodsName, keyword)
                               .or()
                               .like(Goods::getGoodsDesc, keyword));
        }
        
        wrapper.orderByDesc(Goods::getCreateTime);
        
        // 先获取总数
        Page<Goods> page = new Page<>(pageNum, pageSize);
        IPage<Goods> pageResult = goodsService.page(page, wrapper);
        
        // 获取商家信息
        List<Integer> sellerIds = pageResult.getRecords().stream()
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
        for (Goods goods : pageResult.getRecords()) {
            GoodsWithSellerVO vo = new GoodsWithSellerVO();
            BeanUtils.copyProperties(goods, vo);
            
            if (goods.getSellerId() != null && sellerMap.containsKey(goods.getSellerId())) {
                Seller seller = sellerMap.get(goods.getSellerId());
                vo.setSellerName(seller.getUsername());
                vo.setShopName(seller.getShopName());
            }
            
            voList.add(vo);
        }
        
        PageResult<GoodsWithSellerVO> result = new PageResult<>(
                pageResult.getTotal(),
                pageNum,
                pageSize,
                voList
        );
        
        return Result.success(result);
    }

    /**
     * 分页获取商家自己的商品列表
     * 需要商家登录
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param keyword 关键词搜索（可选）
     * @param session HTTP会话
     * @return 分页后的商品列表
     */
    @GetMapping("/my/paged")
    public Result<PageResult<Goods>> myGoodsPaged(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        
        Seller seller = (Seller) session.getAttribute("seller");
        if (seller == null) {
            return Result.error("请先登录商家账号");
        }
        
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getSellerId, seller.getId())
               .eq(Goods::getIsDelete, Constants.Status.NOT_DELETED);
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Goods::getGoodsName, keyword)
                               .or()
                               .like(Goods::getGoodsDesc, keyword));
        }
        
        wrapper.orderByDesc(Goods::getCreateTime);
        
        Page<Goods> page = new Page<>(pageNum, pageSize);
        IPage<Goods> pageResult = goodsService.page(page, wrapper);
        
        PageResult<Goods> result = new PageResult<>(
                pageResult.getTotal(),
                pageNum,
                pageSize,
                pageResult.getRecords()
        );
        
        return Result.success(result);
    }
}
