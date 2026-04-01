package com.team6.module.chat.domain.repository;

import com.team6.module.chat.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    Optional<ChatRoom> findByChatRoomId(Long chatRoomId);

    List<ChatRoom> findAllChatRoomByUserId(Long userId);
}