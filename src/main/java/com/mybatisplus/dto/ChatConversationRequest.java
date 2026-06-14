package com.mybatisplus.dto;

import lombok.Data;

@Data
public class ChatConversationRequest {
    private String userType;
    private Integer userId;
    private String targetType;
    private Integer targetId;
}
