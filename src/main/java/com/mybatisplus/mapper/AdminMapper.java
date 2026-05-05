package com.mybatisplus.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mybatisplus.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}