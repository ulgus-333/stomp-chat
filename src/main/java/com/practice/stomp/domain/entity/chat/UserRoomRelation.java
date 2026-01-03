package com.practice.stomp.domain.entity.chat;

import com.practice.stomp.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Table(name = "message_room_relation")
@NoArgsConstructor
@Entity
public class UserRoomRelation {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idx;

    @JoinColumn(name = "user_idx")
    @ManyToOne
    private User user;

    @JoinColumn(name = "room_idx")
    @ManyToOne
    private Room room;

    @Column
    private Integer unreadMessageCount;

    public Long roomIdx() {
        return this.room.getIdx();
    }

    public String userName() {
        return this.user.getName();
    }

    public LocalDateTime lastMessagedAt() {
        return this.room.getLastMessagedAt();
    }
}
