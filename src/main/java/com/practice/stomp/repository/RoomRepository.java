package com.practice.stomp.repository;

import com.practice.stomp.domain.entity.chat.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
