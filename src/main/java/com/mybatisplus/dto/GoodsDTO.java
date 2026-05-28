package com.mybatisplus.dto;

import com.mybatisplus.entity.Goods;
import com.mybatisplus.entity.Seller;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDTO {
    private Goods goods;
    private Seller seller;
}
