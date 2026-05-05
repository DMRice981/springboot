package com.mybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mybatisplus.entity.GoodsImg;
import com.mybatisplus.mapper.GoodsImgMapper;
import com.mybatisplus.service.GoodsImgService;
import org.springframework.stereotype.Service;

@Service
public class GoodsImgServiceImpl extends ServiceImpl<GoodsImgMapper, GoodsImg> implements GoodsImgService {
}