package com.practice.stomp.controller;

import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.response.UserDetailResponseDto;
import com.practice.stomp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDetailResponseDto> findUserDetail(@AuthenticationPrincipal CustomOAuth2User requestUser) {
        return ResponseEntity.ok(userService.findUserDetail(requestUser.userIdx()));
    }
}
