package com.project.shopapp.controllers;

import com.project.shopapp.dtos.ChatMessageDTO;
import com.project.shopapp.models.ChatMessage;
import com.project.shopapp.models.ChatRoom;
import com.project.shopapp.models.User;
import com.project.shopapp.shared.base.ResponseObject;
import com.project.shopapp.responses.chat.ChatMessageResponse;
import com.project.shopapp.services.chat.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room")
    public ResponseEntity<ResponseObject> getOrCreateRoom(
            @RequestParam("receiver_id") Long receiverId,
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            ChatRoom room = chatService.getOrCreateRoom(loginUser.getId(), receiverId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Lấy thông tin phòng chat thành công")
                    .status(HttpStatus.OK)
                    .data(room)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build()
            );
        }
    }

    @PostMapping("/send")
    public ResponseEntity<ResponseObject> sendMessage(
            @Valid @RequestBody ChatMessageDTO messageDTO,
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            messageDTO.setSenderId(loginUser.getId());

            ChatMessage message = chatService.sendMessage(messageDTO);

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Gửi tin nhắn thành công")
                    .status(HttpStatus.OK)
                    .data(ChatMessageResponse.fromMessage(message))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<ResponseObject> getMessages(
            @PathVariable("roomId") Long roomId,
            @AuthenticationPrincipal User loginUser
    ) {
        try {
            List<ChatMessage> messages = chatService.getMessages(roomId, loginUser.getId());

            List<ChatMessageResponse> response = messages.stream()
                    .map(ChatMessageResponse::fromMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Lấy danh sách tin nhắn thành công")
                    .status(HttpStatus.OK)
                    .data(response)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }
}
