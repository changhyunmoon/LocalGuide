package com.team6.module.chat.domain;

import com.team6.module.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_participant")
@NoArgsConstructor
public class ChatParticipant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long userId; // 참여자 사용자 ID

    @Builder
    public ChatParticipant( Long userId) {
        this.userId = userId;
    }
}