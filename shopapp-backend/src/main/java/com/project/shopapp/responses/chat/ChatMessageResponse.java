package com.project.shopapp.responses.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.ChatMessage;
import com.project.shopapp.shared.base.BaseResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResponse extends BaseResponse {

    private Long id;

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("sender_id")
    private Long senderId;

    private String content;

    private String type;

    @JsonProperty("attachment_url")
    private String attachmentUrl;

    @JsonProperty("is_read")
    private boolean isRead;

    public static ChatMessageResponse fromMessage(ChatMessage message) {
        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(message.getId())
                .roomId(message.getChatRoom() != null ? message.getChatRoom().getId() : null)
                .senderId(message.getSender() != null ? message.getSender().getId() : null)
                .content(message.getContent())
                .type(message.getType() != null ? message.getType().name() : null)
                .attachmentUrl(message.getAttachmentUrl())
                .isRead(message.isRead())
                .build();

        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());
        return response;
    }

}
