package com.practice.stomp.service.chat;

import com.practice.stomp.domain.entity.chat.UserRoomRelation;
import com.practice.stomp.domain.response.ChatRoomsResponseDto;
import com.practice.stomp.service.dto.UserRoomRelations;
import com.practice.stomp.service.message.MessageService;
import com.practice.stomp.service.relation.UserRoomRelationService;
import com.practice.stomp.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatAggregateService {
    private final MessageService messageService;
    private final RoomService roomService;
    private final UserRoomRelationService userRoomRelationService;

    public ChatRoomsResponseDto findRoomsByUserIdx(Long idx, Pageable pageable) {
        Page<UserRoomRelation> relationPage = userRoomRelationService.findPagedUserRoomRelationByUserIdx(idx, pageable);
        if (relationPage.isEmpty()) {
            return ChatRoomsResponseDto.empty();
        }

        UserRoomRelations roomRelations = userRoomRelationService.findUserRoomRelationsByRoomIdxes(relationPage.getContent());
        Map<Long, List<String>> mapper = roomRelations.roomIdxUserNamesMapper();

        return ChatRoomsResponseDto.of(relationPage, mapper);
    }
}
