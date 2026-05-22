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
    private Order order;
    private List<OrderItem> orderItems;
}
