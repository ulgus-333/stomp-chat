package com.practice.stomp.domain.dao.user;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import static com.practice.stomp.domain.entity.user.QUser.user;

public record UserSearchDao(
        Long requestUserIdx,
        String name,
        String email,
        Pageable pageable
) {
    public BooleanBuilder condition() {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(user.idx.ne(requestUserIdx));
        if (StringUtils.hasText(this.name)) {
            builder.and(user.name.eq(name));
        }
        if (StringUtils.hasText(this.email)) {
            builder.and(user.email.eq(email));
        }
        return builder;
    }

    public Long offset() {
        return this.pageable.getOffset();
    }

    public Integer limit() {
        return this.pageable.getPageSize();
    }
}
