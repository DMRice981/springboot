package com.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("after_sale")
public class AfterSale {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer orderId;
    private Integer userId;
    private Integer sellerId;
    private Integer goodsId;
    private String reason;
    private Integer status; // 0待处理 1已处理
    private Date createTime;
}