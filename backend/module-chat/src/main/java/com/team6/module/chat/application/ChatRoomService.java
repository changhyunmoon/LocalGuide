package com.team6.module.chat.application;

import com.team6.module.chat.domain.ChatRoom;
import com.team6.module.chat.domain.repository.ChatRoomRepository;
import com.team6.module.chat.dto.ChatRoomCreateServiceRequest;
import com.team6.module.chat.dto.ChatRoomListServiceRequest;
import com.team6.module.chat.dto.responseDTO.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    //-----------------------------------------------------------
    //방 생성 (참여자 포함)
    //나중에 방 생성 시 똑같은 멤버의 참여자가 있는 방을 만드는 상황에 대한 예외처리 필요
    @Transactional
    public ChatRoomResponse createRoom(ChatRoomCreateServiceRequest request) {
        // 1. 채팅방 엔티티 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(request.roomName())
                .build();

        // 2. 참여자 명단 합치기 (호스트 + 초대받은 인원)
        List<Long> allMemberIds = new ArrayList<>(request.participantIds());
        if (!allMemberIds.contains(request.hostId())) {
            allMemberIds.add(0, request.hostId()); // 호스트를 맨 앞에 추가
        }

        // 3. 채팅방에 참여자 추가 (엔티티 내부 리스트에 담기)
        chatRoom.addParticipants(allMemberIds);

        // 4. DB 저장 (JPA Cascade로 참여자까지 한 번에 저장)
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 6. Redis 멤버 정보 업데이트
        //updateRedisMembers(savedRoom.getId(), allMemberIds);

        log.info("채팅방 생성 완료: {}, 참여 인원: {}명", savedRoom.getRoomName(), savedRoom.getParticipantCount());

        return ChatRoomResponse.from(savedRoom);
    }


    //-----------------------------------------------------------
    //특정 회원의 채팅방 목록 출력
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getUserChatRooms(ChatRoomListServiceRequest serviceRequest) {
        // 1. 해당 유저가 참여 중인 채팅방 목록을 DB에서 조회
        List<ChatRoom> chatRooms = chatRoomRepository.findAllChatRoomByUserId(serviceRequest.userId());

        if (chatRooms.isEmpty()) {
            log.info("사용자 {}가 참여 중인 채팅방이 없습니다.", serviceRequest.userId());
            return List.of();
        }

        return chatRooms.stream()
                .map(ChatRoomResponse::from)
                .toList();
    }


}