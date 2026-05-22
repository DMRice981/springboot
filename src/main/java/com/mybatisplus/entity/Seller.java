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
@TableName("seller")
public class Seller {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String shopName;
    private String phone;
    private LocalDateTime createTime;
}