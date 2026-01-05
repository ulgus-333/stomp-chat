package com.practice.stomp.controller;

import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.dto.MessagePayloadDto;
import com.practice.stomp.service.chat.ChatAggregateService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
public class StompController {
    private final ChatAggregateService chatAggregateService;

    @MessageMapping("/chats/{roomIdx}")
    @SendTo("/sub/chats/{roomIdx}")
    public MessagePayloadDto handleMessage(@AuthenticationPrincipal Principal principal,
                                           @DestinationVariable Long roomIdx,
                                           @Payload MessagePayloadDto messagePayloadDto) {
        LocalDateTime messagedAt = LocalDateTime.now();
        CustomOAuth2User user = (CustomOAuth2User) ((OAuth2AuthenticationToken) principal).getPrincipal();
        return chatAggregateService.insertMessage(user, roomIdx, messagePayloadDto, messagedAt);
    }
}
