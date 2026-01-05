package com.practice.stomp.controller;

import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.entity.user.User;
import com.practice.stomp.domain.request.chat.CreateChatRoomRequestDto;
import com.practice.stomp.domain.response.chat.ChatRoomResponseDto;
import com.practice.stomp.domain.response.chat.ChatRoomsResponseDto;
import com.practice.stomp.service.chat.ChatAggregateService;
import com.practice.stomp.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/chats")
@RestController
public class ChatController {
    private final ChatAggregateService chatAggregateService;
    private final UserService userService;

    @GetMapping("/rooms")
    public ResponseEntity<ChatRoomsResponseDto> findChatRooms(@AuthenticationPrincipal CustomOAuth2User requestUser,
                                                              Pageable pageable) {

        return ResponseEntity.ok(chatAggregateService.findRoomsByUserIdx(requestUser.userIdx(), pageable));
    }

    @PostMapping("/room")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(@AuthenticationPrincipal CustomOAuth2User requestUser,
                                                              @RequestBody @Valid CreateChatRoomRequestDto requestDto) {

        User targetUser = userService.findUserByIdx(requestDto.invitedUserIdx());
        LocalDateTime requestDateTime = LocalDateTime.now();
        return ResponseEntity.ok(chatAggregateService.createChatRoom(requestUser, targetUser, requestDto.title(), requestDateTime));
    }
}
