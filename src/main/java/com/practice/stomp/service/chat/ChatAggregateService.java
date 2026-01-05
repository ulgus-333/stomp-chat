package com.practice.stomp.service.chat;

import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.dto.MessagePayloadDto;
import com.practice.stomp.domain.entity.chat.Message;
import com.practice.stomp.domain.entity.chat.Room;
import com.practice.stomp.domain.entity.chat.UserRoomRelation;
import com.practice.stomp.domain.entity.user.User;
import com.practice.stomp.domain.response.chat.ChatRoomResponseDto;
import com.practice.stomp.domain.response.chat.ChatRoomsResponseDto;
import com.practice.stomp.service.dto.UserRoomRelations;
import com.practice.stomp.service.message.MessageService;
import com.practice.stomp.service.relation.UserRoomRelationService;
import com.practice.stomp.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    @Transactional
    public ChatRoomResponseDto createChatRoom(CustomOAuth2User requestUser, User targetUser, String roomTitle, LocalDateTime requestDatetime) {
        Room newRoom = roomService.insert(Room.insert(roomTitle, requestDatetime));

        List<UserRoomRelation> relations = Stream.of(requestUser.user(), targetUser)
                .map(user -> UserRoomRelation.insert(user, newRoom))
                .toList();

        UserRoomRelation myRelation = userRoomRelationService.insertAll(relations).stream()
                .filter(relation -> relation.getUser().getIdx().equals(requestUser.userIdx()))
                .findFirst()
                .orElseThrow();

        return ChatRoomResponseDto.of(myRelation, List.of(requestUser.user().getName(), targetUser.getName()));
    }

    @Transactional
    public MessagePayloadDto insertMessage(CustomOAuth2User requestUser, Long roomIdx, MessagePayloadDto messagePayloadDto, LocalDateTime messagedAt) {
        Room room = roomService.findByIdx(roomIdx);

        Message message = messageService.insert(Message.insert(messagePayloadDto.message(), requestUser.user(), room, messagedAt));
        UserRoomRelations targetRelations = userRoomRelationService.findUserRoomRelationByRoomIdxAndNotUserIdx(roomIdx, requestUser.userIdx());
        targetRelations.increaseUnreadCount();

        room.updateLastMessagedAt(messagedAt);

        return MessagePayloadDto.chat(requestUser.user().decryptName(), message.getMessage());
    }
}
