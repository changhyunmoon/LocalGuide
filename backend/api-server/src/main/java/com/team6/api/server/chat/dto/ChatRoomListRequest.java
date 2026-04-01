package com.team6.api.server.chat.dto;

import com.team6.module.chat.dto.ChatRoomListServiceRequest;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;

@Builder
public record ChatRoomListRequest(
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId
) {
    // Controller에서 받은 userId를 Service용 객체로 변환
    public ChatRoomListServiceRequest toServiceRequest() {
        return ChatRoomListServiceRequest.builder()
                .userId(userId)
                .build();
    }
}
