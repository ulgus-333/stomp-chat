package com.practice.stomp.service.room;

import com.practice.stomp.domain.entity.chat.Room;
import com.practice.stomp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RoomService {
    private final RoomRepository roomRepository;

    @Transactional
    public Room insert(Room room) {
        return roomRepository.save(room);
    }
}
