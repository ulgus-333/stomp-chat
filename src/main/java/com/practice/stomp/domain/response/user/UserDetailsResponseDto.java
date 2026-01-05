package com.practice.stomp.domain.response.user;

import com.practice.stomp.domain.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

public record UserDetailsResponseDto (
        PagedModel<UserDetailResponseDto> users
) {
    public static UserDetailsResponseDto from(Page<User> users) {
        Page<UserDetailResponseDto> response = users.map(UserDetailResponseDto::from);

        return new UserDetailsResponseDto(new PagedModel<>(response));
    }

    public static UserDetailsResponseDto empty() {
        return new UserDetailsResponseDto(new PagedModel<>(Page.empty()));
    }
}
