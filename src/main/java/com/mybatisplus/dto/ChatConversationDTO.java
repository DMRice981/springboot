package com.mybatisplus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatConversationDTO {
    private Integer id;
    private String targetType;
    private Integer targetId;
    private String targetName;
    private String targetExtra;
    private String lastMessage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;
    private Long unreadCount;
    private Boolean online;
}
