package com.practice.stomp.domain.response.user;

import com.practice.stomp.domain.entity.user.User;

public record UserDetailResponseDto (
        Long idx,
        String email,
        String name,
        String nickName,
        String profileImage
) {
    public static UserDetailResponseDto from(User user, String profileImagePar) {
        return new UserDetailResponseDto(
                user.getIdx(),
                user.getEmail(),
                user.decryptName(),
                user.getNickname(),
                profileImagePar
        );
    }

    public static UserDetailResponseDto from(User user) {
        return new UserDetailResponseDto(
                user.getIdx(),
                user.getEmail(),
                user.decryptName(),
                user.getNickname(),
                null
        );
    }
}
