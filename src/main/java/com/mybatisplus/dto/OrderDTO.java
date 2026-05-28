package com.mybatisplus.dto;

import com.mybatisplus.entity.Order;
import com.mybatisplus.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Integer userId;
    private Integer addressId;
    private List<GoodsItem> goodsList;
    
    // 以下是用于订单详情响应的字段
    private Order order;
    private List<OrderItem> orderItems;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GoodsItem {
        private Integer goodsId;
        private Integer quantity;
    }
}
