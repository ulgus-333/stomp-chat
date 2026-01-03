package com.practice.stomp.repository;

import com.practice.stomp.domain.entity.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
