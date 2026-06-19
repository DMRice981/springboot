package com.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("cart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer goodsId;
    private Integer num;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // ============= 非持久化字段：用于返回商品信息给前端 =============
    @TableField(exist = false)
    private String goodsName;

    @TableField(exist = false)
    private String goodsImg;

    @TableField(exist = false)
    private java.math.BigDecimal price;

    @TableField(exist = false)
    private Integer stock;

    @TableField(exist = false)
    private Integer status;
}
