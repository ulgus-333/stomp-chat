package com.practice.stomp.service.user;

import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.entity.user.User;
import com.practice.stomp.domain.type.OAuth2UserType;
import com.practice.stomp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserType userType = OAuth2UserType.findByRegistrationId(userRequest.getClientRegistration().getRegistrationId());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(userType.generateUser(attributes)));

        return CustomOAuth2User.of(user, attributes);
    }
}
