package imi.spring.backend.services.implementations;

import imi.spring.backend.models.chat.ChatMessage;
import imi.spring.backend.models.chat.MessageStatus;
import imi.spring.backend.repositories.ChatMessageRepository;
import imi.spring.backend.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @Override
    public Long countNewMessages(Long senderId, Long recipientId) {
        return chatMessageRepository.countBySenderIdAndRecipientIdAndStatus(senderId, recipientId, MessageStatus.RECEIVED);
    }

    @Override
    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        List<ChatMessage> chatMessages = chatMessageRepository.getChatBetweenUsers(senderId, recipientId);

        if(!chatMessages.isEmpty()){
            updateStatuses(chatMessages, MessageStatus.DELIVERED);
        }

        return chatMessages;
    }

    @Override
    public ChatMessage findById(Long id) {
        return chatMessageRepository.findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return chatMessageRepository.save(chatMessage);
                })
                .orElseThrow(() ->
                        new IllegalArgumentException("Message with [id=" + id + "] not found."));
    }

    @Override
    public void updateStatuses(List<ChatMessage> chatMessages, MessageStatus status) {
        for(ChatMessage chatMessage : chatMessages){
            if(chatMessage.getStatus() != status){
                chatMessage.setStatus(status);
                chatMessageRepository.save(chatMessage);
            }
        }
    }
}
