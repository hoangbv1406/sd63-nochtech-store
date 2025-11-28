package com.project.shopapp.services.chat;

import com.project.shopapp.dtos.ChatMessageDTO;
import com.project.shopapp.models.ChatMessage;
import com.project.shopapp.models.ChatRoom;

import java.util.List;

public interface ChatService {
    ChatRoom getOrCreateRoom(Long senderId, Long receiverId);
    ChatMessage sendMessage(ChatMessageDTO messageDTO) throws Exception;
    List<ChatMessage> getMessages(Long roomId);
}