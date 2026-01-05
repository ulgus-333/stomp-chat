package com.practice.stomp.service.user;

import com.practice.stomp.domain.dao.user.UserSearchDao;
import com.practice.stomp.domain.entity.user.User;
import com.practice.stomp.domain.request.user.UserSearchRequestDto;
import com.practice.stomp.domain.response.user.UserDetailResponseDto;
import com.practice.stomp.domain.response.user.UserDetailsResponseDto;
import com.practice.stomp.repository.UserRepository;
import com.practice.stomp.repository.dsl.UserQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserQueryDslRepository userQueryDslRepository;

    public User findUserByIdx(Long userIdx) {
        return userRepository.findById(userIdx)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatusCode.valueOf(404)));
    }

    public UserDetailResponseDto findUserDetail(Long userIdx) {
        return UserDetailResponseDto.from(findUserByIdx(userIdx));
    }

    public UserDetailsResponseDto searchUserDetail(Long requestUserIdx, UserSearchRequestDto requestDto, Pageable pageable) {
        UserSearchDao searchDao = requestDto.toDao(requestUserIdx, pageable);
        Page<User> searchUser = userQueryDslRepository.searchUserByDao(searchDao);
        return UserDetailsResponseDto.from(searchUser);
    }
}
