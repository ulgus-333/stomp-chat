package com.practice.stomp.service.relation;

import com.practice.stomp.domain.entity.chat.UserRoomRelation;
import com.practice.stomp.repository.UserRoomRelationRepository;
import com.practice.stomp.service.dto.UserRoomRelations;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserRoomRelationService {
    private final UserRoomRelationRepository userRoomRelationRepository;

    public Page<UserRoomRelation> findPagedUserRoomRelationByUserIdx(Long userIdx, Pageable pageable) {
        return userRoomRelationRepository.findAllByUserIdx(userIdx, pageable);
    }

    public UserRoomRelations findUserRoomRelationsByRoomIdxes(List<UserRoomRelation> targetRelations) {
        UserRoomRelations relations = UserRoomRelations.from(targetRelations);
        return UserRoomRelations.from(userRoomRelationRepository.findAllByRoom_IdxIn(relations.roomIdxes()));
    }
}
