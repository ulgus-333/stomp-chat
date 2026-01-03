package com.practice.stomp.controller;

import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.response.ChatRoomsResponseDto;
import com.practice.stomp.service.chat.ChatAggregateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/chats")
@RestController
public class ChatController {
    private final ChatAggregateService chatAggregateService;

    @GetMapping("/rooms")
    public ResponseEntity<ChatRoomsResponseDto> findChatRooms(@AuthenticationPrincipal CustomOAuth2User requestUser,
                                                              Pageable pageable) {

        return ResponseEntity.ok(chatAggregateService.findRoomsByUserIdx(requestUser.userIdx(), pageable));
    }

}
