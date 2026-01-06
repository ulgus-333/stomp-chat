package com.practice.stomp.domain.response.chat;

import com.practice.stomp.domain.entity.chat.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

public record MessagesResponseDto (
        PagedModel<MessageResponseDto> messages
) {
    public static MessagesResponseDto from(Page<Message> messages) {
        Page<MessageResponseDto> response = messages.map(MessageResponseDto::from);

        return new MessagesResponseDto(new PagedModel<>(response));
    }
}
