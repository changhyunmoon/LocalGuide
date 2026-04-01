package com.team6.api.server.chat;


import com.team6.api.server.chat.dto.ChatRoomCreateRequest;
import com.team6.api.server.chat.dto.ChatRoomListRequest;
import com.team6.module.chat.dto.responseDTO.ChatRoomResponse;
import com.team6.module.chat.application.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createRoom(
            @RequestBody @Valid ChatRoomCreateRequest request) {

        // 인증 정보에서 가져왔다고 가정 (Security 구현 전 하드코딩)
        Long hostId = 100L;

        // Presentation DTO를 Service DTO로 변환하여 전달
        ChatRoomResponse response = chatRoomService.createRoom(request.toServiceRequest(hostId));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(@Valid ChatRoomListRequest request) {

        List<ChatRoomResponse> responses = chatRoomService.getUserChatRooms(request.toServiceRequest());

        return ResponseEntity.ok(responses);
    }
}
