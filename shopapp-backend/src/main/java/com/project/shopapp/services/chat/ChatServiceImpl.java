package com.project.shopapp.services.chat;

import com.project.shopapp.dtos.ChatMessageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.ChatMessage;
import com.project.shopapp.models.ChatParticipant;
import com.project.shopapp.models.ChatRoom;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.ChatMessageRepository;
import com.project.shopapp.repositories.ChatParticipantRepository;
import com.project.shopapp.repositories.ChatRoomRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    @Override
    @Transactional
    public ChatRoom getOrCreateRoom(Long senderId, Long receiverId) {
        return chatRoomRepository.findRoomBetweenUsers(senderId, receiverId)
                .orElseGet(() -> {
                    try {
                        ChatRoom newRoom = new ChatRoom();
                        newRoom = chatRoomRepository.save(newRoom);

                        User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người gửi với ID: " + senderId));
                        User receiver = userRepository.findById(receiverId)
                                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người nhận với ID: " + receiverId));

                        ChatParticipant participant1 = ChatParticipant.builder()
                                .chatRoom(newRoom)
                                .user(sender)
                                .build();
                        chatParticipantRepository.save(participant1);

                        ChatParticipant participant2 = ChatParticipant.builder()
                                .chatRoom(newRoom)
                                .user(receiver)
                                .build();
                        chatParticipantRepository.save(participant2);

                        return newRoom;

                    } catch (DataNotFoundException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
    }

    @Override
    @Transactional
    public ChatMessage sendMessage(ChatMessageDTO dto) throws Exception {
        ChatRoom room = chatRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy phòng chat"));

        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người gửi"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(dto.getContent())
                .type(dto.getType())
                .attachmentUrl(dto.getAttachmentUrl())
                .isRead(false)
                .build();

        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getMessages(Long roomId, Long userId) throws Exception {
        boolean isParticipant = chatParticipantRepository.existsByChatRoomIdAndUserId(roomId, userId);
        if (!isParticipant) {
            throw new Exception("Cảnh báo: Bạn không có quyền xem tin nhắn của phòng chat này!");
        }

        Pageable pageable = PageRequest.of(0, 50);
        return chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable).getContent();
    }
}
