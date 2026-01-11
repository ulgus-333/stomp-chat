package com.practice.stomp.service.user;

import com.practice.stomp.domain.dao.user.UserSearchDao;
import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.entity.user.User;
import com.practice.stomp.domain.request.type.FilePathType;
import com.practice.stomp.domain.request.user.UserProfileUpdateRequestDto;
import com.practice.stomp.domain.request.user.UserSearchRequestDto;
import com.practice.stomp.domain.response.user.UserDetailResponseDto;
import com.practice.stomp.domain.response.user.UserDetailsResponseDto;
import com.practice.stomp.repository.UserRepository;
import com.practice.stomp.repository.dsl.UserQueryDslRepository;
import com.practice.stomp.service.dto.CacheKey;
import com.practice.stomp.service.infra.FileService;
import com.practice.stomp.service.infra.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserQueryDslRepository userQueryDslRepository;
    private final RedisCacheService cacheService;
    private final FileService fileService;

    public User findUserByIdx(Long userIdx) {
        return userRepository.findById(userIdx)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatusCode.valueOf(404)));
    }

    public UserDetailResponseDto findUserDetail(Long userIdx) {
        User findUser = findUserByIdx(userIdx);
        return UserDetailResponseDto.from(findUser, profileImageParUrl(findUser.getIdx(), findUser.getProfileImage()));
    }

    public UserDetailsResponseDto searchUserDetail(Long requestUserIdx, UserSearchRequestDto requestDto, Pageable pageable) {
        UserSearchDao searchDao = requestDto.toDao(requestUserIdx, pageable);
        Page<User> searchUser = userQueryDslRepository.searchUserByDao(searchDao);
        return UserDetailsResponseDto.from(searchUser);
    }

    private String profileImageParUrl(Long userIdx, String filePath) {
        String cacheKey = CacheKey.OCI_USER_READ_KEY.generateKey(String.valueOf(userIdx), FilePathType.PROFILE.name());
        return cacheService.get(cacheKey, String.class)
                .orElseGet(() -> {
                    String generateParReadUrl = fileService.generateParReadUrl(filePath);
                    cacheService.set(cacheKey, generateParReadUrl, CacheKey.OCI_USER_READ_KEY.expire());
                    return generateParReadUrl;
                });
    }

    @Transactional
    public void updateUserProfile(CustomOAuth2User requestUser, UserProfileUpdateRequestDto requestDto) {
         User targetUser = userRepository.findById(requestUser.userIdx())
                         .orElseThrow(() -> new HttpClientErrorException(HttpStatusCode.valueOf(404)));
         if (!targetUser.getProfileImage().equals(requestDto.profileImageUrl())) {
             String cacheKey = CacheKey.OCI_USER_READ_KEY.generateKey(String.valueOf(requestUser.userIdx()), FilePathType.PROFILE.name());
             cacheService.delete(cacheKey);
             fileService.deleteFile(targetUser.getProfileImage());
         }
         targetUser.update(requestDto.toEntity());
    }
}
