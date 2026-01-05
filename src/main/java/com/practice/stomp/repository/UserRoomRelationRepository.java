package com.practice.stomp.repository;

import com.practice.stomp.domain.entity.chat.UserRoomRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoomRelationRepository extends JpaRepository<UserRoomRelation, Long> {
    Page<UserRoomRelation> findAllByUserIdx(Long idx, Pageable pageable);

    List<UserRoomRelation> findAllByRoom_IdxIn(List<Long> roomIdxes);
    List<UserRoomRelation> findAllByRoom_IdxAndUserIdxNot(Long roomIdx,Long userIdx);

}
