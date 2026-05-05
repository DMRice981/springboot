package com.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("goods_comment")
public class GoodsComment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer goodsId;
    private Integer userId;
    private String orderNo;
    private Integer score;
    private String content;
    private String commentImg;
    private LocalDateTime createTime;
}