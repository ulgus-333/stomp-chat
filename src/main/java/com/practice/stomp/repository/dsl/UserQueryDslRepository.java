package com.practice.stomp.repository.dsl;

import com.practice.stomp.domain.dao.user.UserSearchDao;
import com.practice.stomp.domain.entity.user.User;
import org.springframework.data.domain.Page;

public interface UserQueryDslRepository {
    Page<User> searchUserByDao(UserSearchDao searchDao);
}
