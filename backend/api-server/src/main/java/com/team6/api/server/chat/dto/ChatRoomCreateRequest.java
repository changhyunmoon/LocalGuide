package com.team6.api.server.chat.dto;

import com.team6.module.chat.dto.ChatRoomCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ChatRoomCreateRequest (
        @NotBlank(message = "채팅방 이름은 필수입니다.")
        String roomName,


        @NotEmpty(message = "참여 유저 목록이 비어있을 수 없습니다.")
        List<Long> participantIds
){
        public ChatRoomCreateServiceRequest toServiceRequest(Long hostId) {
                return ChatRoomCreateServiceRequest.builder()
                        .roomName(roomName)
                        .participantIds(participantIds)
                        .hostId(hostId)
                        .build();
        }
}
