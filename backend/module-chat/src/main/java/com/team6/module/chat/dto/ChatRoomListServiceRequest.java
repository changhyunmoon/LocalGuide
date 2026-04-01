package com.team6.module.chat.dto;

import lombok.Builder;

@Builder
public record ChatRoomListServiceRequest(
        Long userId
) {
}
