package com.team6.module.chat.infrastructure;

import com.team6.module.chat.domain.ChatRoom;
import com.team6.module.chat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    private final JpaChatRoomRepository jpaChatRoomRepository;

    @Override
    public ChatRoom save(ChatRoom chatRoom){
        return jpaChatRoomRepository.save(chatRoom);
    }

    @Override
    public Optional<ChatRoom> findByChatRoomId(Long chatRoomId){
        return jpaChatRoomRepository.findById(chatRoomId);
    }

    @Override
    public List<ChatRoom> findAllChatRoomByUserId(Long userId){
        return jpaChatRoomRepository.findAllChatRoomByUserId(userId);
    }

}
