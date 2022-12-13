package imi.spring.backend.repositories;

import imi.spring.backend.models.chat.ChatMessage;
import imi.spring.backend.models.chat.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Long countBySenderIdAndRecipientIdAndStatus(Long senderId, Long recipientId, MessageStatus status);

    @Query(
            value = "SELECT cm FROM chat_message cm" +
                    "WHERE (cm.sender_id = ?1 AND cm.recipiend_id = ?2) OR (cm.sender_id = ?2 AND cm.recipiend_id = ?1)" +
                    "ORDER BY cm.timestamp ASC",
            nativeQuery = true
    )
    List<ChatMessage> getChatBetweenUsers(Long senderId, Long recipientId);
}
