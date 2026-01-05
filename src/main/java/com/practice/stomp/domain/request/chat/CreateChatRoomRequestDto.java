package com.practice.stomp.domain.request.chat;

public record CreateChatRoomRequestDto (
        String title,
        Long invitedUserIdx
) {
}

