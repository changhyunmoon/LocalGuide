package com.team6.module.chat.domain;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    private String id; // MongoDB는 기본적으로 String(ObjectId) 사용

    @Indexed // 조회를 위해 인덱스 설정
    private Long chatRoomId;

    private Long senderId;
    private String senderRole; // "TRAVELER" or "GUIDE"
    private String content;
    private String messageType; // "TALK", "ENTER", "IMAGE"

    @CreatedDate // 자동으로 생성 시간 기록
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(Long chatRoomId, Long senderId, String senderRole, String content, String messageType) {
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.senderRole = senderRole;
        this.content = content;
        this.messageType = messageType;
        this.createdAt = LocalDateTime.now();
    }
}