package com.practice.stomp.domain.dto;

import com.practice.stomp.domain.type.MessageType;

public record MessagePayloadDto(
        MessageType type,
        String senderNickname,
        String message
) {

    public static MessagePayloadDto chat(String senderNickname, String message) {
        return new MessagePayloadDto(MessageType.TEXT, senderNickname, message);
    }
}
