package com.practice.stomp.service.user;

import com.practice.stomp.domain.response.UserDetailResponseDto;
import com.practice.stomp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserDetailResponseDto findUserDetail(Long userIdx) {
        return userRepository.findById(userIdx)
                .map(UserDetailResponseDto::from)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatusCode.valueOf(404)));
    }
}
