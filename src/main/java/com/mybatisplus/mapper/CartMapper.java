package com.mybatisplus.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mybatisplus.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}