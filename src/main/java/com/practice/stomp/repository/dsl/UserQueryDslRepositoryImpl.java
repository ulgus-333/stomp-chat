package com.practice.stomp.repository.dsl;

import com.practice.stomp.domain.dao.user.UserSearchDao;
import com.practice.stomp.domain.entity.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.practice.stomp.domain.entity.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<User> searchUserByDao(UserSearchDao searchDao) {
        Long searchCount = queryFactory.select(user.count())
                .from(user)
                .where(searchDao.condition())
                .fetchOne();

        if (searchCount == null || searchCount <= 0) {
            return Page.empty();
        }

        List<User> content = queryFactory.selectFrom(user)
                .where(searchDao.condition())
                .offset(searchDao.offset())
                .limit(searchDao.limit())
                .orderBy(user.name.asc())
                .fetch();

        return new PageImpl<>(content, searchDao.pageable(), searchCount);
    }
}
