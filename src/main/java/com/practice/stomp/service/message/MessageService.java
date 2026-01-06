package com.practice.stomp.service.message;

import com.practice.stomp.domain.entity.chat.Message;
import com.practice.stomp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Transactional
    public Message insert(Message message) {
        return messageRepository.save(message);
    }

    public Page<Message> findMessagesByRoomIdx(Long roomIdx, Pageable pageable) {
        return messageRepository.findAllByRoomIdx(roomIdx, pageable);
    }
}
