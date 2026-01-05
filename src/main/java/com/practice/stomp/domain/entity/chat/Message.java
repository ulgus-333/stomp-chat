package com.practice.stomp.domain.entity.chat;

import com.practice.stomp.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Table(name = "message")
@NoArgsConstructor
@Entity
public class Message {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idx;

    @Column(columnDefinition = "text", nullable = false)
    private String message;

    @JoinColumn(name = "user_idx")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "room_idx")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Column(nullable = false)
    private LocalDateTime createAt;

    public static Message insert(String message, User user, Room room, LocalDateTime createAt) {
        return Message.builder()
                .message(message)
                .user(user)
                .room(room)
                .createAt(createAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Message(Long idx, String message, User user, Room room, LocalDateTime createAt) {
        this.idx = idx;
        this.message = message;
        this.user = user;
        this.room = room;
        this.createAt = createAt;
    }
}
