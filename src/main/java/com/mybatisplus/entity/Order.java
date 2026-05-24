package com.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField("total_price")
    private BigDecimal totalPrice;
    @TableField("pay_price")
    private BigDecimal payPrice;
    @TableField("pay_status")
    private Integer payStatus;
    @TableField("order_status")
    private Integer orderStatus;
    private Integer isDelete;
    private LocalDateTime payTime;
    private LocalDateTime sendTime;
    private LocalDateTime confirmTime;
    private LocalDateTime createTime;
}
