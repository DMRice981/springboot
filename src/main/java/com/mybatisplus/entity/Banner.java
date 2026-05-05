package com.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("banner")
@AllArgsConstructor
@NoArgsConstructor
public class Banner {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String imgUrl;
    private String linkUrl;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}