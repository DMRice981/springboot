package com.mybatisplus.dto;

import lombok.Data;

/**
 * 分页请求参数
 */
@Data
public class PageRequest {
    /**
     * 当前页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 状态筛选
     */
    private Integer status;

    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
