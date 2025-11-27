package com.project.shopapp.repositories;

import com.project.shopapp.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query(value = "SELECT cr.* FROM chat_rooms cr " + "JOIN chat_participants cp ON cr.id = cp.room_id " + "WHERE cp.user_id = :userId " + "ORDER BY cr.updated_at DESC", nativeQuery = true)
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT cr.* FROM chat_rooms cr " + "JOIN chat_participants cp1 ON cr.id = cp1.room_id " + "JOIN chat_participants cp2 ON cr.id = cp2.room_id " + "WHERE cp1.user_id = :user1Id AND cp2.user_id = :user2Id", nativeQuery = true)
    Optional<ChatRoom> findRoomBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

}
