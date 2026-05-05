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
@TableName("goods_img")
public class GoodsImg {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer goodsId;
    private String imgUrl;
    private Integer sort;
    private LocalDateTime createTime;
}