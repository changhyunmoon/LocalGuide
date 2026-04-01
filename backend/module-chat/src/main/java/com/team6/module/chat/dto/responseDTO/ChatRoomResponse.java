package com.team6.module.chat.dto.responseDTO;

import com.team6.module.chat.domain.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        Long roomId,
        String roomName,
        int participantCount,    // 현재 인원수 필드 추가
        String lastMessage,      // 목록 조회 시 유용한 마지막 메시지
        LocalDateTime lastSendAt // 마지막 메시지 전송 시간
) {
    public static ChatRoomResponse from(ChatRoom room) {
        return new ChatRoomResponse(
                room.getId(),
                room.getRoomName(),
                room.getParticipantCount(),
                room.getLastMessage(),
                room.getLastSendAt()
        );
    }
}