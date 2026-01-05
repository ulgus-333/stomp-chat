package com.practice.stomp.service.message;

import com.practice.stomp.domain.entity.chat.Message;
import com.practice.stomp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Transactional
    public Message insert(Message message) {
        return messageRepository.save(message);
    }
}
