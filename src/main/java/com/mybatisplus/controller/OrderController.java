package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mybatisplus.common.Constants;
import com.mybatisplus.common.Result;
import com.mybatisplus.dto.OrderDTO;
import com.mybatisplus.dto.PageResult;
import com.mybatisplus.entity.Goods;
import com.mybatisplus.entity.Order;
import com.mybatisplus.entity.OrderItem;
import com.mybatisplus.service.GoodsService;
import com.mybatisplus.service.OrderItemService;
import com.mybatisplus.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 订单管理控制器
 *
 * @author Aran Shop Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final GoodsService goodsService;

    /**
     * 获取用户订单列表
     *
     * @param userId 用户ID
     * @return 订单列表（按创建时间倒序）
     */
    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam Integer userId) {
        List<Order> list = orderService.lambdaQuery()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 获取所有订单列表（商家/管理员）
     *
     * @param sellerId 商家ID（可选，用于商家查看自己的订单）
     * @param status 订单状态（可选，用于筛选）
     * @return 订单列表
     */
    @GetMapping("/list/all")
    public Result<List<Order>> listAll(@RequestParam(required = false) Integer sellerId,
                                       @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Order::getOrderStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        List<Order> orders = orderService.list(wrapper);

        // 商家查看自己的订单
        if (sellerId != null) {
            orders = orders.stream()
                    .filter(order -> {
                        List<OrderItem> items = orderItemService.lambdaQuery()
                                .eq(OrderItem::getOrderNo, order.getOrderNo())
                                .list();
                        return items.stream().anyMatch(item -> {
                            Goods goods = goodsService.getById(item.getGoodsId());
                            return goods != null && sellerId.equals(goods.getSellerId());
                        });
                    })
                    .toList();
        }

        return Result.success(orders);
    }

    /**
     * 分页获取用户订单列表
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param userId 用户ID
     * @param status 订单状态（可选，用于筛选）
     * @return 分页后的订单列表
     */
    @GetMapping("/list/paged")
    public Result<PageResult<Order>> listPaged(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Integer userId,
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        
        if (status != null) {
            wrapper.eq(Order::getOrderStatus, status);
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> page = new Page<>(pageNum, pageSize);
        IPage<Order> pageResult = orderService.page(page, wrapper);
        
        PageResult<Order> result = new PageResult<>(
                pageResult.getTotal(),
                pageNum,
                pageSize,
                pageResult.getRecords()
        );
        
        return Result.success(result);
    }

    /**
     * 分页获取所有订单列表（商家/管理员）
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param sellerId 商家ID（可选，用于商家查看自己的订单）
     * @param status 订单状态（可选，用于筛选）
     * @param keyword 订单号关键词搜索（可选）
     * @return 分页后的订单列表
     */
    @GetMapping("/list/all/paged")
    public Result<PageResult<Order>> listAllPaged(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer sellerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Order::getOrderStatus, status);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Order::getOrderNo, keyword);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> page = new Page<>(pageNum, pageSize);
        IPage<Order> pageResult = orderService.page(page, wrapper);
        
        // 如果是商家，需要过滤只显示自己的订单
        List<Order> orders = pageResult.getRecords();
        if (sellerId != null) {
            // 获取商家所有的商品ID
            List<Goods> sellerGoods = goodsService.lambdaQuery()
                    .eq(Goods::getSellerId, sellerId)
                    .list();
            Set<Integer> sellerGoodsIds = sellerGoods.stream()
                    .map(Goods::getId)
                    .collect(Collectors.toSet());
            
            // 获取商家商品对应的所有订单号
            List<OrderItem> orderItems = orderItemService.list();
            Set<String> sellerOrderNos = orderItems.stream()
                    .filter(item -> sellerGoodsIds.contains(item.getGoodsId()))
                    .map(OrderItem::getOrderNo)
                    .collect(Collectors.toSet());
            
            // 过滤只显示商家的订单
            orders = orders.stream()
                    .filter(order -> sellerOrderNos.contains(order.getOrderNo()))
                    .collect(Collectors.toList());
        }
        
        PageResult<Order> result = new PageResult<>(
                pageResult.getTotal(),
                pageNum,
                pageSize,
                orders
        );
        
        return Result.success(result);
    }

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情（包含订单项列表）
     */
    @GetMapping("/get/{id}")
    public Result<OrderDTO> get(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        List<OrderItem> orderItems = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderNo, order.getOrderNo())
                .list();

        OrderDTO dto = new OrderDTO();
        dto.setOrder(order);
        dto.setOrderItems(orderItems);

        return Result.success(dto);
    }

    /**
     * 创建订单
     * 事务操作：创建订单 → 创建订单项 → 扣减库存
     *
     * @param dto 订单信息
     * @return 创建结果
     */
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Result<Order> create(@RequestBody OrderDTO dto) {
        if (dto.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (dto.getAddressId() == null) {
            return Result.error("收货地址不能为空");
        }
        if (dto.getGoodsList() == null || dto.getGoodsList().isEmpty()) {
            return Result.error("订单商品不能为空");
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 计算总价
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderDTO.GoodsItem item : dto.getGoodsList()) {
            Goods goods = goodsService.getById(item.getGoodsId());
            if (goods == null) {
                return Result.error("商品不存在: " + item.getGoodsId());
            }
            if (goods.getStock() < item.getQuantity()) {
                return Result.error("商品库存不足: " + goods.getGoodsName());
            }

            totalPrice = totalPrice.add(goods.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(dto.getUserId());
        order.setAddressId(dto.getAddressId());
        order.setTotalPrice(totalPrice);
        order.setPayPrice(totalPrice);
        order.setPayStatus(0);
        order.setOrderStatus(Constants.OrderStatus.PENDING);
        order.setCreateTime(LocalDateTime.now());

        orderService.save(order);

        // 扣减库存并创建订单项
        for (OrderDTO.GoodsItem item : dto.getGoodsList()) {
            Goods goods = goodsService.getById(item.getGoodsId());
            
            // 扣减库存
            goodsService.lambdaUpdate()
                    .eq(Goods::getId, goods.getId())
                    .set(Goods::getStock, goods.getStock() - item.getQuantity())
                    .set(Goods::getSales, goods.getSales() + item.getQuantity())
                    .update();

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderNo(orderNo);
            orderItem.setGoodsId(goods.getId());
            orderItem.setGoodsName(goods.getGoodsName());
            orderItem.setGoodsImg(goods.getGoodsImg());
            orderItem.setPrice(goods.getPrice());
            orderItem.setNum(item.getQuantity());
            orderItem.setCreateTime(LocalDateTime.now());

            orderItemService.save(orderItem);
        }

        return Result.success("创建成功", order);
    }

    /**
     * 支付订单
     * 更新支付状态为已支付，订单状态为待发货
     *
     * @param id 订单ID
     * @return 支付结果
     */
    @PostMapping("/pay/{id}")
    public Result<Void> pay(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!Constants.OrderStatus.PENDING.equals(order.getOrderStatus())) {
            return Result.error("订单状态不允许支付");
        }

        order.setPayStatus(Constants.PayStatus.PAID);
        order.setPayTime(LocalDateTime.now());
        order.setOrderStatus(Constants.OrderStatus.PAID);

        orderService.updateById(order);

        return Result.successMsg("支付成功");
    }

    /**
     * 商家发货
     * 更新订单状态为已发货
     *
     * @param id 订单ID
     * @return 发货结果
     */
    @PostMapping("/send/{id}")
    public Result<Void> send(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!Constants.OrderStatus.PAID.equals(order.getOrderStatus())) {
            return Result.error("订单状态不允许发货");
        }

        order.setOrderStatus(Constants.OrderStatus.SHIPPED);
        order.setSendTime(LocalDateTime.now());

        orderService.updateById(order);

        return Result.successMsg("发货成功");
    }

    /**
     * 确认收货
     * 更新订单状态为已完成
     *
     * @param id 订单ID
     * @return 确认结果
     */
    @PostMapping("/confirm/{id}")
    public Result<Void> confirm(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!Constants.OrderStatus.SHIPPED.equals(order.getOrderStatus())) {
            return Result.error("订单状态不允许确认收货");
        }

        order.setOrderStatus(Constants.OrderStatus.COMPLETED);
        order.setConfirmTime(LocalDateTime.now());

        orderService.updateById(order);

        return Result.successMsg("确认收货成功");
    }

    /**
     * 取消订单
     * 仅允许取消待支付的订单，并退还库存
     *
     * @param id 订单ID
     * @return 取消结果
     */
    @PostMapping("/cancel/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancel(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!Constants.OrderStatus.PENDING.equals(order.getOrderStatus())) {
            return Result.error("订单状态不允许取消");
        }

        // 退还库存
        List<OrderItem> orderItems = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderNo, order.getOrderNo())
                .list();

        for (OrderItem item : orderItems) {
            Goods goods = goodsService.getById(item.getGoodsId());
            if (goods != null) {
                goodsService.lambdaUpdate()
                        .eq(Goods::getId, goods.getId())
                        .set(Goods::getStock, goods.getStock() + item.getNum())
                        .set(Goods::getSales, Math.max(0, goods.getSales() - item.getNum()))
                        .update();
            }
        }

        order.setOrderStatus(Constants.OrderStatus.CANCELLED);

        orderService.updateById(order);

        return Result.successMsg("取消成功");
    }

    /**
     * 删除订单（管理员）
     *
     * @param id 订单ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 删除订单项
        orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderNo, order.getOrderNo())
                .list()
                .forEach(item -> orderItemService.removeById(item.getId()));

        // 删除订单
        orderService.removeById(id);

        return Result.successMsg("删除成功");
    }

    /**
     * 生成订单号
     * 格式: 时间戳(14位) + 随机数(6位)
     *
     * @return 订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", new Random().nextInt(1000000));
        return timestamp + random;
    }
}
