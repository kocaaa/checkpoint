package imi.spring.backend.services;

import imi.spring.backend.models.chat.ChatMessage;
import imi.spring.backend.models.chat.MessageStatus;

import java.util.List;

public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);
    Long countNewMessages(Long senderId, Long RecipientId);
    List<ChatMessage> findChatMessages(Long senderId, Long RecipientId);
    ChatMessage findById(Long id);
    void updateStatuses(List<ChatMessage> chatMessages, MessageStatus status);
}
