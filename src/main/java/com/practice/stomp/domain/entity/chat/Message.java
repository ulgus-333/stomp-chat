package com.practice.stomp.domain.entity.chat;

import com.practice.stomp.domain.entity.user.User;
import jakarta.persistence.*;
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
}
