package com.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mybatisplus.dto.ChatContactDTO;
import com.mybatisplus.dto.ChatConversationDTO;
import com.mybatisplus.dto.ChatMessageDTO;
import com.mybatisplus.dto.PageResult;
import com.mybatisplus.entity.ChatConversation;
import com.mybatisplus.entity.ChatMessage;

import java.util.List;

public interface ChatService extends IService<ChatConversation> {
    ChatConversation createOrGetConversation(String userType, Integer userId, String targetType, Integer targetId);

    ChatMessage saveMessage(ChatMessageDTO dto);

    List<ChatConversationDTO> listConversations(String userType, Integer userId);

    PageResult<ChatMessage> listMessages(Integer conversationId, Integer pageNum, Integer pageSize);

    void markRead(Integer conversationId, String userType, Integer userId);

    Long unreadCount(String userType, Integer userId);

    List<ChatContactDTO> listContacts(String currentType, Integer currentId, String keyword);
}
