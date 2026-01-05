package com.practice.stomp.domain.request.user;

import com.practice.stomp.domain.dao.user.UserSearchDao;
import com.practice.stomp.utils.CipherUtils;
import org.springframework.data.domain.Pageable;

public record UserSearchRequestDto (
        String name,
        String email
) {
    public UserSearchDao toDao(Long requestUserIdx, Pageable pageable) {
        return new UserSearchDao(
                requestUserIdx,
                CipherUtils.encrypt(this.name),
                this.email,
                pageable
        );
    }
}
