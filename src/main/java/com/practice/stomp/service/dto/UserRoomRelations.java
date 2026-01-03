package com.practice.stomp.service.dto;

import com.practice.stomp.domain.entity.chat.UserRoomRelation;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserRoomRelations {
    private final List<UserRoomRelation> relations = new ArrayList<>();

    public static UserRoomRelations from(List<UserRoomRelation> relations) {
        return new UserRoomRelations(relations);
    }

    private UserRoomRelations(List<UserRoomRelation> relations) {
        if (!CollectionUtils.isEmpty(relations)) {
            return;
        }
        this.relations.addAll(relations);
    }

    public List<Long> roomIdxes() {
        return this.relations.stream()
                .map(UserRoomRelation::roomIdx)
                .toList();
    }

    public Map<Long, List<String>> roomIdxUserNamesMapper() {
        return this.relations.stream()
                .collect(Collectors.groupingBy(
                        UserRoomRelation::roomIdx,
                        Collectors.mapping(UserRoomRelation::userName, Collectors.toList())));
    }

    public Stream<UserRoomRelation> stream() {
        return this.relations.stream();
    }
}
