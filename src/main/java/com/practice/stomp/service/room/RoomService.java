package com.practice.stomp.service.room;

import com.practice.stomp.domain.entity.chat.Room;
import com.practice.stomp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomService {
    private final RoomRepository roomRepository;

    @Transactional
    public Room insert(Room room) {
        return roomRepository.save(room);
    }

    public Room findByIdx(Long roomIdx) {
        return roomRepository.findById(roomIdx)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatusCode.valueOf(404)));
    }
}
