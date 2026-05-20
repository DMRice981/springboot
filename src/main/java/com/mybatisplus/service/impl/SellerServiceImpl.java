package com.mybatisplus.service.impl;

import com.mybatisplus.entity.Seller;
import com.mybatisplus.mapper.SellerMapper;
import com.mybatisplus.service.SellerService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SellerServiceImpl extends ServiceImpl<SellerMapper, Seller> implements SellerService {
}