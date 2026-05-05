package com.mybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mybatisplus.entity.GoodsComment;
import com.mybatisplus.mapper.GoodsCommentMapper;
import com.mybatisplus.service.GoodsCommentService;
import org.springframework.stereotype.Service;

@Service
public class GoodsCommentServiceImpl extends ServiceImpl<GoodsCommentMapper, GoodsComment> implements GoodsCommentService {
}