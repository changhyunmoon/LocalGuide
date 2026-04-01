package com.team6.module.chat.domain;

import com.team6.module.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    // 단방향 OneToMany 설정
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_room_id")
    private List<ChatParticipant> participants = new ArrayList<>();
    private int participantCount = 0;

    private String lastMessage;
    private LocalDateTime lastSendAt;

    @Builder
    public ChatRoom(String roomName) {
        this.roomName = roomName;
        this.lastSendAt = LocalDateTime.now();
    }

    // 단일 참여자 추가 메서드 (한 명씩 초대할 때 사용)
    public void addParticipant(Long memberId) {
        this.participants.add(new ChatParticipant(memberId));
        this.participantCount++;
    }

    // 다수 참여자 추가 메서드 (방 생성 시 한꺼번에 넣을 때 사용)
    public void addParticipants(List<Long> memberIds) {
        memberIds.forEach(this::addParticipant);
    }
}
