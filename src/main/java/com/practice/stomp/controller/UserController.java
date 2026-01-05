package com.practice.stomp.controller;

import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.request.user.UserSearchRequestDto;
import com.practice.stomp.domain.response.user.UserDetailResponseDto;
import com.practice.stomp.domain.response.user.UserDetailsResponseDto;
import com.practice.stomp.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping("/search")
    public ResponseEntity<UserDetailsResponseDto> searchUserDetail(@AuthenticationPrincipal CustomOAuth2User requestUser,
                                                                   @ModelAttribute @Valid UserSearchRequestDto requestDto,
                                                                   Pageable pageable) {

        return ResponseEntity.ok(userService.searchUserDetail(requestUser.userIdx(), requestDto, pageable));
    }
}
