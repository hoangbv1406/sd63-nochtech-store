package com.project.shopapp.repositories;

import com.project.shopapp.models.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = "SELECT m FROM ChatMessage m JOIN FETCH m.sender WHERE m.chatRoom.id = :roomId ORDER BY m.createdAt DESC", countQuery = "SELECT count(m) FROM ChatMessage m WHERE m.chatRoom.id = :roomId")
    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(@Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m JOIN FETCH m.sender WHERE m.chatRoom.id = :roomId AND m.createdAt < :lastCreatedAt ORDER BY m.createdAt DESC")
    List<ChatMessage> findMessagesBeforeCursor(@Param("roomId") Long roomId, @Param("lastCreatedAt") LocalDateTime lastCreatedAt, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom.id = :roomId AND m.sender.id != :currentUserId AND m.isRead = false")
    void markMessagesAsRead(@Param("roomId") Long roomId, @Param("currentUserId") Long currentUserId);

}
