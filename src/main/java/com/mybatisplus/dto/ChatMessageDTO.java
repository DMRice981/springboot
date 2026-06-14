package com.mybatisplus.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String type;
    private Integer id;
    private Integer conversationId;
    private String senderType;
    private Integer senderId;
    private String receiverType;
    private Integer receiverId;
    private String messageType;
    private String content;
    private Integer isRead;
    private String createTime;
}
