package com.mybatisplus.websocket;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.mybatisplus.dto.ChatMessageDTO;
import com.mybatisplus.entity.ChatMessage;
import com.mybatisplus.mapper.ChatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageMapper chatMessageMapper;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    public ChatWebSocketHandler(@Lazy ChatMessageMapper chatMessageMapper, @Qualifier("objectMapper") JsonMapper objectMapper) {
        this.chatMessageMapper = chatMessageMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userType = getQueryParam(session.getUri(), "userType");
        String userId = getQueryParam(session.getUri(), "userId");
        if (userType == null || userId == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        session.getAttributes().put("userType", normalizeType(userType));
        session.getAttributes().put("userId", Integer.valueOf(userId));
        sessions.put(sessionKey(userType, Integer.valueOf(userId)), session);
        broadcastOnlineStatus(normalizeType(userType), Integer.valueOf(userId), true);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        JsonNode node = objectMapper.readTree(textMessage.getPayload());
        String type = node.path("type").asText("CHAT_MESSAGE");
        if (!"CHAT_MESSAGE".equals(type)) {
            return;
        }

        ChatMessageDTO dto = objectMapper.treeToValue(node, ChatMessageDTO.class);
        dto.setSenderType((String) session.getAttributes().get("userType"));
        dto.setSenderId((Integer) session.getAttributes().get("userId"));

        ChatMessage message = new ChatMessage();
        message.setConversationId(dto.getConversationId());
        message.setSenderType(normalizeType(dto.getSenderType()));
        message.setSenderId(dto.getSenderId());
        message.setReceiverType(normalizeType(dto.getReceiverType()));
        message.setReceiverId(dto.getReceiverId());
        message.setMessageType(dto.getMessageType() == null || dto.getMessageType().isEmpty() ? "TEXT" : dto.getMessageType());
        message.setContent(dto.getContent().trim());
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(message);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "CHAT_MESSAGE");
        payload.put("data", message);
        sendTo(dto.getReceiverType(), dto.getReceiverId(), payload);
        sendTo(dto.getSenderType(), dto.getSenderId(), payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userType = (String) session.getAttributes().get("userType");
        Integer userId = (Integer) session.getAttributes().get("userId");
        if (userType != null && userId != null) {
            sessions.remove(sessionKey(userType, userId));
            broadcastOnlineStatus(userType, userId, false);
        }
    }

    public boolean isOnline(String userType, Integer userId) {
        WebSocketSession session = sessions.get(sessionKey(userType, userId));
        return session != null && session.isOpen();
    }

    private void sendTo(String userType, Integer userId, Map<String, Object> payload) throws IOException {
        WebSocketSession session = sessions.get(sessionKey(userType, userId));
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
        }
    }

    private void broadcastOnlineStatus(String userType, Integer userId, boolean online) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("userType", userType);
        data.put("userId", userId);
        data.put("online", online);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ONLINE_STATUS");
        payload.put("data", data);

        String message = objectMapper.writeValueAsString(payload);
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    private String getQueryParam(URI uri, String name) {
        if (uri == null) {
            return null;
        }
        return UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst(name);
    }

    private String sessionKey(String userType, Integer userId) {
        return normalizeType(userType) + ":" + userId;
    }

    private String normalizeType(String userType) {
        return userType == null ? null : userType.trim().toUpperCase();
    }
}
