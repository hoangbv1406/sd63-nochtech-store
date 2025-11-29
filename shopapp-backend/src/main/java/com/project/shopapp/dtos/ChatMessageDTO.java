package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.enums.ChatMessageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    @JsonProperty("sender_id")
    private Long senderId;

    @JsonProperty("room_id")
    @NotNull(message = "Room ID is required")
    private Long roomId;

    private String content;

    @Builder.Default
    private ChatMessageType type = ChatMessageType.TEXT;

    @JsonProperty("attachment_url")
    private String attachmentUrl;

}
