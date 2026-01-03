package com.practice.stomp.domain.response;

import com.practice.stomp.domain.entity.chat.UserRoomRelation;

import java.time.LocalDateTime;
import java.util.List;

public record ChatRoomResponseDto (
        Long idx,
        String title,
        List<String> users,
        Integer unreadMessageCount,
        LocalDateTime lastMessagedAt
) {
    public static ChatRoomResponseDto of(UserRoomRelation relation, List<String> users) {
        return new ChatRoomResponseDto(
                relation.getRoom().getIdx(),
                relation.getRoom().getTitle(),
                users,
                relation.getUnreadMessageCount(),
                relation.lastMessagedAt()
        );
    }
}
