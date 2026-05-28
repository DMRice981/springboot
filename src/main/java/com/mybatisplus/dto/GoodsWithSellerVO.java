package com.mybatisplus.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsWithSellerVO {
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
    private Integer sellerId;
    private String sellerName;
    private String shopName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
