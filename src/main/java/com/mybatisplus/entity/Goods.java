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
@TableName("goods")
public class Goods {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer categoryId;
    private String goodsName;
    private String goodsImg;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private Integer stock;
    private Integer sales;
    private String goodsDesc;
    private Integer status;
    private Integer isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer sellerId;
}