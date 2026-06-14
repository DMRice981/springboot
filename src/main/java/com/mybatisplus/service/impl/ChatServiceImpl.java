package com.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mybatisplus.dto.ChatContactDTO;
import com.mybatisplus.dto.ChatConversationDTO;
import com.mybatisplus.dto.ChatMessageDTO;
import com.mybatisplus.dto.PageResult;
import com.mybatisplus.entity.Admin;
import com.mybatisplus.entity.ChatConversation;
import com.mybatisplus.entity.ChatMessage;
import com.mybatisplus.entity.Seller;
import com.mybatisplus.entity.User;
import com.mybatisplus.mapper.ChatConversationMapper;
import com.mybatisplus.service.AdminService;
import com.mybatisplus.service.ChatMessageService;
import com.mybatisplus.service.ChatService;
import com.mybatisplus.service.SellerService;
import com.mybatisplus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversation> implements ChatService {

    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final SellerService sellerService;
    private final AdminService adminService;

    @Override
    @Transactional
    public ChatConversation createOrGetConversation(String userType, Integer userId, String targetType, Integer targetId) {
        validateParticipant(userType, userId);
        validateParticipant(targetType, targetId);
        ParticipantPair pair = normalizePair(userType, userId, targetType, targetId);

        ChatConversation exist = lambdaQuery()
                .eq(ChatConversation::getUserAType, pair.userAType)
                .eq(ChatConversation::getUserAId, pair.userAId)
                .eq(ChatConversation::getUserBType, pair.userBType)
                .eq(ChatConversation::getUserBId, pair.userBId)
                .one();
        if (exist != null) {
            return exist;
        }

        ChatConversation conversation = new ChatConversation();
        conversation.setUserAType(pair.userAType);
        conversation.setUserAId(pair.userAId);
        conversation.setUserBType(pair.userBType);
        conversation.setUserBId(pair.userBId);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        save(conversation);
        return conversation;
    }

    @Override
    @Transactional
    public ChatMessage saveMessage(ChatMessageDTO dto) {
        if (dto.getConversationId() == null) {
            throw new IllegalArgumentException("会话ID不能为空");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        ChatConversation conversation = getById(dto.getConversationId());
        if (conversation == null) {
            throw new IllegalArgumentException("会话不存在");
        }
        validateParticipant(dto.getSenderType(), dto.getSenderId());
        validateParticipant(dto.getReceiverType(), dto.getReceiverId());
        if (!participantInConversation(conversation, dto.getSenderType(), dto.getSenderId()) ||
                !participantInConversation(conversation, dto.getReceiverType(), dto.getReceiverId())) {
            throw new IllegalArgumentException("消息参与方不属于当前会话");
        }

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
        chatMessageService.save(message);

        conversation.setLastMessage(message.getContent());
        conversation.setLastMessageTime(message.getCreateTime());
        conversation.setUpdateTime(LocalDateTime.now());
        updateById(conversation);
        return message;
    }

    @Override
    public List<ChatConversationDTO> listConversations(String userType, Integer userId) {
        validateParticipant(userType, userId);
        String normalizedType = normalizeType(userType);
        List<ChatConversation> conversations = lambdaQuery()
                .and(w -> w.eq(ChatConversation::getUserAType, normalizedType)
                        .eq(ChatConversation::getUserAId, userId)
                        .or()
                        .eq(ChatConversation::getUserBType, normalizedType)
                        .eq(ChatConversation::getUserBId, userId))
                .orderByDesc(ChatConversation::getLastMessageTime)
                .orderByDesc(ChatConversation::getUpdateTime)
                .list();

        return conversations.stream().map(conversation -> toDTO(conversation, normalizedType, userId)).toList();
    }

    @Override
    public PageResult<ChatMessage> listMessages(Integer conversationId, Integer pageNum, Integer pageSize) {
        Page<ChatMessage> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 20 : pageSize);
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getCreateTime)
                .orderByAsc(ChatMessage::getId);
        IPage<ChatMessage> result = chatMessageService.page(page, wrapper);
        return new PageResult<>(result.getTotal(), (int) result.getCurrent(), (int) result.getSize(), result.getRecords());
    }

    @Override
    public void markRead(Integer conversationId, String userType, Integer userId) {
        validateParticipant(userType, userId);
        chatMessageService.lambdaUpdate()
                .eq(ChatMessage::getConversationId, conversationId)
                .eq(ChatMessage::getReceiverType, normalizeType(userType))
                .eq(ChatMessage::getReceiverId, userId)
                .set(ChatMessage::getIsRead, 1)
                .update();
    }

    @Override
    public Long unreadCount(String userType, Integer userId) {
        validateParticipant(userType, userId);
        return chatMessageService.lambdaQuery()
                .eq(ChatMessage::getReceiverType, normalizeType(userType))
                .eq(ChatMessage::getReceiverId, userId)
                .eq(ChatMessage::getIsRead, 0)
                .count();
    }

    @Override
    public List<ChatContactDTO> listContacts(String currentType, Integer currentId, String keyword) {
        String normalizedType = normalizeType(currentType);
        List<ChatContactDTO> contacts = new ArrayList<>();
        String kw = keyword == null ? "" : keyword.trim();

        userService.list().stream()
                .filter(user -> !Objects.equals(normalizedType, "USER") || !Objects.equals(currentId, user.getId()))
                .filter(user -> kw.isEmpty() || contains(user.getUsername(), kw) || contains(user.getPhone(), kw))
                .forEach(user -> contacts.add(new ChatContactDTO("USER", user.getId(), user.getUsername(), user.getPhone())));

        sellerService.list().stream()
                .filter(seller -> !Objects.equals(normalizedType, "SELLER") || !Objects.equals(currentId, seller.getId()))
                .filter(seller -> kw.isEmpty() || contains(seller.getUsername(), kw) || contains(seller.getShopName(), kw))
                .forEach(seller -> contacts.add(new ChatContactDTO("SELLER", seller.getId(), seller.getShopName() == null || seller.getShopName().isEmpty() ? seller.getUsername() : seller.getShopName(), seller.getUsername())));

        adminService.list().stream()
                .filter(admin -> !Objects.equals(normalizedType, "ADMIN") || !Objects.equals(currentId, admin.getId()))
                .filter(admin -> kw.isEmpty() || contains(admin.getAdminName(), kw) || contains(admin.getNickname(), kw))
                .forEach(admin -> contacts.add(new ChatContactDTO("ADMIN", admin.getId(), admin.getNickname() == null || admin.getNickname().isEmpty() ? admin.getAdminName() : admin.getNickname(), admin.getAdminName())));

        contacts.sort(Comparator.comparing(ChatContactDTO::getUserType).thenComparing(ChatContactDTO::getDisplayName, Comparator.nullsLast(String::compareTo)));
        return contacts;
    }

    private ChatConversationDTO toDTO(ChatConversation conversation, String currentType, Integer currentId) {
        boolean currentIsA = Objects.equals(conversation.getUserAType(), currentType) && Objects.equals(conversation.getUserAId(), currentId);
        String targetType = currentIsA ? conversation.getUserBType() : conversation.getUserAType();
        Integer targetId = currentIsA ? conversation.getUserBId() : conversation.getUserAId();

        ChatConversationDTO dto = new ChatConversationDTO();
        dto.setId(conversation.getId());
        dto.setTargetType(targetType);
        dto.setTargetId(targetId);
        dto.setTargetName(resolveName(targetType, targetId));
        dto.setTargetExtra(resolveExtra(targetType, targetId));
        dto.setLastMessage(conversation.getLastMessage());
        dto.setLastMessageTime(conversation.getLastMessageTime());
        dto.setUnreadCount(chatMessageService.lambdaQuery()
                .eq(ChatMessage::getConversationId, conversation.getId())
                .eq(ChatMessage::getReceiverType, currentType)
                .eq(ChatMessage::getReceiverId, currentId)
                .eq(ChatMessage::getIsRead, 0)
                .count());
        dto.setOnline(false);
        return dto;
    }

    private String resolveName(String type, Integer id) {
        if ("USER".equals(type)) {
            User user = userService.getById(id);
            return user == null ? "用户" + id : user.getUsername();
        }
        if ("SELLER".equals(type)) {
            Seller seller = sellerService.getById(id);
            if (seller == null) return "商家" + id;
            return seller.getShopName() == null || seller.getShopName().isEmpty() ? seller.getUsername() : seller.getShopName();
        }
        Admin admin = adminService.getById(id);
        if (admin == null) return "管理员" + id;
        return admin.getNickname() == null || admin.getNickname().isEmpty() ? admin.getAdminName() : admin.getNickname();
    }

    private String resolveExtra(String type, Integer id) {
        if ("USER".equals(type)) {
            User user = userService.getById(id);
            return user == null ? "" : user.getPhone();
        }
        if ("SELLER".equals(type)) {
            Seller seller = sellerService.getById(id);
            return seller == null ? "" : seller.getUsername();
        }
        Admin admin = adminService.getById(id);
        return admin == null ? "" : admin.getAdminName();
    }

    private ParticipantPair normalizePair(String userType, Integer userId, String targetType, Integer targetId) {
        String left = normalizeType(userType) + ":" + userId;
        String right = normalizeType(targetType) + ":" + targetId;
        if (left.compareTo(right) <= 0) {
            return new ParticipantPair(normalizeType(userType), userId, normalizeType(targetType), targetId);
        }
        return new ParticipantPair(normalizeType(targetType), targetId, normalizeType(userType), userId);
    }

    private boolean participantInConversation(ChatConversation conversation, String type, Integer id) {
        String normalizedType = normalizeType(type);
        return Objects.equals(conversation.getUserAType(), normalizedType) && Objects.equals(conversation.getUserAId(), id)
                || Objects.equals(conversation.getUserBType(), normalizedType) && Objects.equals(conversation.getUserBId(), id);
    }

    private void validateParticipant(String type, Integer id) {
        if (type == null || id == null) {
            throw new IllegalArgumentException("用户类型和ID不能为空");
        }
        String normalizedType = normalizeType(type);
        if (!"USER".equals(normalizedType) && !"SELLER".equals(normalizedType) && !"ADMIN".equals(normalizedType)) {
            throw new IllegalArgumentException("用户类型无效");
        }
    }

    private String normalizeType(String type) {
        return type == null ? null : type.trim().toUpperCase();
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.contains(keyword);
    }

    private record ParticipantPair(String userAType, Integer userAId, String userBType, Integer userBId) {
    }
}
