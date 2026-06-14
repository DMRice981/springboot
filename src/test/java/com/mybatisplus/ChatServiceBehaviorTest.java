package com.mybatisplus;

import com.mybatisplus.dto.ChatMessageDTO;
import com.mybatisplus.entity.ChatConversation;
import com.mybatisplus.entity.ChatMessage;
import com.mybatisplus.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatServiceBehaviorTest {

    @Autowired
    private ChatService chatService;

    @Test
    void createOrGetConversationReturnsSameConversationForSamePairInReverseOrder() {
        ChatConversation first = chatService.createOrGetConversation("USER", 1, "SELLER", 1);
        ChatConversation second = chatService.createOrGetConversation("SELLER", 1, "USER", 1);

        assertNotNull(first.getId());
        assertEquals(first.getId(), second.getId());
    }

    @Test
    void sendTextMessageSavesMessageAndUpdatesConversationLastMessage() {
        ChatConversation conversation = chatService.createOrGetConversation("USER", 1, "SELLER", 1);

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setConversationId(conversation.getId());
        dto.setSenderType("USER");
        dto.setSenderId(1);
        dto.setReceiverType("SELLER");
        dto.setReceiverId(1);
        dto.setMessageType("TEXT");
        dto.setContent("你好，这个商品还有库存吗？");

        ChatMessage message = chatService.saveMessage(dto);
        ChatConversation updated = chatService.getById(conversation.getId());

        assertNotNull(message.getId());
        assertEquals("你好，这个商品还有库存吗？", message.getContent());
        assertEquals("TEXT", message.getMessageType());
        assertEquals(0, message.getIsRead());
        assertEquals("你好，这个商品还有库存吗？", updated.getLastMessage());
        assertNotNull(updated.getLastMessageTime());
    }
}
