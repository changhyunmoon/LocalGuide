package com.team6.module.chat.infrastructure;

import com.team6.module.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findById(Long chatRoomId);

    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
            "JOIN cr.participants p " +
            "WHERE p.userId = :userId " +
            "ORDER BY cr.lastSendAt DESC")
    List<ChatRoom> findAllChatRoomByUserId(@Param("userId") Long userId);
}
