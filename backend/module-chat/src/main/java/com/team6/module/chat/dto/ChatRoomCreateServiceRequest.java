package com.team6.module.chat.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record ChatRoomCreateServiceRequest(
        String roomName,
        List<Long> participantIds,
        Long hostId
) {
}