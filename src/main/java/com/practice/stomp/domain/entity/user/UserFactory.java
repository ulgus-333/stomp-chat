package com.practice.stomp.domain.entity.user;

import java.util.Map;

public class UserFactory {
    public static User googleUser(Map<String, Object> googleAttributes) {
        return User.insert(
                (String) googleAttributes.get("email"),
                (String) googleAttributes.get("name")
        );
    }
}
