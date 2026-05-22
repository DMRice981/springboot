package com.mybatisplus.dto;

import com.mybatisplus.entity.AfterSale;
import com.mybatisplus.entity.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AfterSaleDTO {
    private AfterSale afterSale;
    private Goods goods;
}
