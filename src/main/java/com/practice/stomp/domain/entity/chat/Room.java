package com.practice.stomp.domain.entity.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Table(name = "room")
@NoArgsConstructor
@Entity
public class Room {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idx;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private LocalDateTime createAt;

    private LocalDateTime lastMessagedAt;

    public static Room insert(String title, LocalDateTime createAt) {
        return Room.builder()
                .title(title)
                .createAt(createAt)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Room(Long idx, String title, LocalDateTime createAt, LocalDateTime lastMessagedAt) {
        this.idx = idx;
        this.title = title;
        this.createAt = createAt;
        this.lastMessagedAt = lastMessagedAt;
    }

    public void updateLastMessagedAt(LocalDateTime lastMessagedAt) {
        this.lastMessagedAt = lastMessagedAt;
    }
}
