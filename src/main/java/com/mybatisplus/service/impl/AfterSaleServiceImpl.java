package com.mybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mybatisplus.entity.AfterSale;
import com.mybatisplus.mapper.AfterSaleMapper;
import com.mybatisplus.service.AfterSaleService;
import org.springframework.stereotype.Service;

@Service
public class AfterSaleServiceImpl extends ServiceImpl<AfterSaleMapper, AfterSale> implements AfterSaleService {
}