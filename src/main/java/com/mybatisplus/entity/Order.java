package com.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer addressId;
    private BigDecimal totalPrice;
    private BigDecimal payPrice;
    private Integer payStatus;
    private Integer orderStatus;
    private LocalDateTime payTime;
    private LocalDateTime sendTime;
    private LocalDateTime confirmTime;
    private LocalDateTime createTime;
}