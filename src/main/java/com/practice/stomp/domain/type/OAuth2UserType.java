package com.practice.stomp.domain.type;

import com.practice.stomp.domain.entity.user.User;
import com.practice.stomp.domain.entity.user.UserFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum OAuth2UserType {
    GOOGLE("google", UserFactory::googleUser),
    ;

    private static final Map<String, OAuth2UserType> MAPPER;

    static {
        MAPPER = Arrays.stream(values())
                .collect(Collectors.toMap(OAuth2UserType::getRegistrationId, Function.identity()));
    }

    private final String registrationId;
    private final Function<Map<String, Object>, User> userFactory;

    public static OAuth2UserType findByRegistrationId(String registrationId) {
        Assert.hasLength(registrationId, "registrationId cannot be empty");

        if (MAPPER.containsKey(registrationId)) {
            return MAPPER.get(registrationId);
        }

        throw new IllegalArgumentException("Unknown registration id " + registrationId);
    }

    public User generateUser(Map<String, Object> attributes) {
        return this.userFactory.apply(attributes);
    }
}
