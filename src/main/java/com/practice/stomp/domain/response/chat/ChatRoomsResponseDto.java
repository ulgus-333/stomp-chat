package com.practice.stomp.domain.response.chat;

import com.practice.stomp.domain.entity.chat.UserRoomRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.Map;

public record ChatRoomsResponseDto (
        PagedModel<ChatRoomResponseDto> rooms
) {
    public static ChatRoomsResponseDto of(Page<UserRoomRelation> relations, Map<Long, List<String>> userNameMapper) {
        Page<ChatRoomResponseDto> pagedResponse = relations.map(relation -> {
            return ChatRoomResponseDto.of(relation, userNameMapper.getOrDefault(relation.roomIdx(), List.of()));
        });
        return new ChatRoomsResponseDto(new PagedModel<>(pagedResponse));
    }

    public static ChatRoomsResponseDto empty() {
        return new ChatRoomsResponseDto(new PagedModel<>(Page.empty()));
    }
}
