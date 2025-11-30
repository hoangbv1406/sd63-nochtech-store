package com.project.shopapp.repositories;

import com.project.shopapp.models.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}
