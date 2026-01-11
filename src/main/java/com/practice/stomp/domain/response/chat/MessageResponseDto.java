package com.practice.stomp.domain.response.chat;

import com.practice.stomp.domain.entity.chat.Message;
import com.practice.stomp.domain.response.user.UserDetailResponseDto;
import com.practice.stomp.domain.type.MessageType;

import java.time.LocalDateTime;

public record MessageResponseDto (
        Long idx,
        MessageResponseType type,
        UserDetailResponseDto user,
        String message,
        LocalDateTime messagedAt
) {
    public static MessageResponseDto from(Message message) {
        return new MessageResponseDto(
                message.getIdx(),
                MessageResponseType.TEXT,
                UserDetailResponseDto.from(message.getUser(), null),
                message.getMessage(),
                message.getCreateAt()
        );
    }

    enum MessageResponseType {
        TEXT,
        SYSTEM,
        ;

        MessageResponseType from(MessageType type) {
            return switch (type) {
                case TEXT -> TEXT;
                case SYSTEM -> SYSTEM;
            };
        }
    }
}
