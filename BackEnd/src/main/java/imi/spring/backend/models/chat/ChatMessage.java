package imi.spring.backend.models.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    private Long chatId;
    private Long senderId;
    private Long recipientId;

    private String senderName;
    private String recipientName;

    private String content;
    private Date timestamp;
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
}
