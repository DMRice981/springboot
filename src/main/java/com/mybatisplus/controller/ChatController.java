package com.mybatisplus.controller;

import com.mybatisplus.common.Result;
import com.mybatisplus.dto.ChatContactDTO;
import com.mybatisplus.dto.ChatConversationDTO;
import com.mybatisplus.dto.ChatConversationRequest;
import com.mybatisplus.dto.PageResult;
import com.mybatisplus.entity.ChatConversation;
import com.mybatisplus.entity.ChatMessage;
import com.mybatisplus.service.ChatService;
import com.mybatisplus.websocket.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatWebSocketHandler chatWebSocketHandler;

    @PostMapping("/conversation")
    public Result<ChatConversation> conversation(@RequestBody ChatConversationRequest request) {
        ChatConversation conversation = chatService.createOrGetConversation(
                request.getUserType(),
                request.getUserId(),
                request.getTargetType(),
                request.getTargetId()
        );
        return Result.success(conversation);
    }

    @GetMapping("/conversations")
    public Result<List<ChatConversationDTO>> conversations(@RequestParam String userType, @RequestParam Integer userId) {
        List<ChatConversationDTO> conversations = chatService.listConversations(userType, userId);
        conversations.forEach(item -> item.setOnline(chatWebSocketHandler.isOnline(item.getTargetType(), item.getTargetId())));
        return Result.success(conversations);
    }

    @GetMapping("/messages")
    public Result<PageResult<ChatMessage>> messages(
            @RequestParam Integer conversationId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(chatService.listMessages(conversationId, pageNum, pageSize));
    }

    @PutMapping("/read/{conversationId}")
    public Result<Void> read(@PathVariable Integer conversationId, @RequestParam String userType, @RequestParam Integer userId) {
        chatService.markRead(conversationId, userType, userId);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount(@RequestParam String userType, @RequestParam Integer userId) {
        return Result.success(chatService.unreadCount(userType, userId));
    }

    @GetMapping("/contacts")
    public Result<List<ChatContactDTO>> contacts(
            @RequestParam String userType,
            @RequestParam Integer userId,
            @RequestParam(required = false) String keyword) {
        return Result.success(chatService.listContacts(userType, userId, keyword));
    }
}
