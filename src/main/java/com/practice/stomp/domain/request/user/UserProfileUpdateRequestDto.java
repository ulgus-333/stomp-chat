package com.practice.stomp.domain.request.user;

import com.practice.stomp.domain.entity.user.User;
import org.hibernate.validator.constraints.Length;

public record UserProfileUpdateRequestDto (
        @Length(max = 50, message = "닉네임은 50자 이하로 설정해주세요.")
        String nickname
) {
    public User toEntity() {
        return User.update(nickname);
    }
}
