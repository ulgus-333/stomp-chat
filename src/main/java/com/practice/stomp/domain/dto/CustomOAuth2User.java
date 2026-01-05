package com.practice.stomp.domain.dto;

import com.practice.stomp.domain.entity.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final User user;
    private final Map<String, Object> attributes;

    public static CustomOAuth2User of(User user, Map<String, Object> attributes) {
        return new CustomOAuth2User(user, attributes);
    }

    private CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(this.user::role);
    }

    @Override
    public String getName() {
        return user.getName();
    }

    public Long userIdx() {
        return this.user.getIdx();
    }

    public User user() {
        return this.user;
    }
}
