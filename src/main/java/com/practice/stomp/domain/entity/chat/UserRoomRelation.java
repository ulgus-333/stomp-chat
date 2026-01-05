package com.practice.stomp.domain.entity.chat;

import com.practice.stomp.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Table(name = "user_room_relation")
@NoArgsConstructor
@Entity
public class UserRoomRelation {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idx;

    @JoinColumn(name = "user_idx", nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(name = "room_idx", nullable = false)
    @ManyToOne
    private Room room;

    @Column
    private Integer unreadMessageCount;

    public static UserRoomRelation insert(User user, Room room) {
        return UserRoomRelation.builder()
                .user(user)
                .room(room)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private UserRoomRelation(Long idx, User user, Room room, Integer unreadMessageCount) {
        this.idx = idx;
        this.user = user;
        this.room = room;
        this.unreadMessageCount = unreadMessageCount;
    }

    public Long roomIdx() {
        return this.room.getIdx();
    }

    public String userName() {
        return this.user.decryptName();
    }

    public LocalDateTime lastMessagedAt() {
        return this.room.getLastMessagedAt();
    }
}
