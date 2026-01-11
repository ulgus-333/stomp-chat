package com.practice.stomp.domain.entity.user;

import com.practice.stomp.domain.type.Role;
import com.practice.stomp.utils.CipherUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@Table(name = "user")
@NoArgsConstructor
@Entity
public class User {
    private static final String DEFAULT_PROFILE = "user/profiles/default/default_profile.png";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, unique = true, length = 50, updatable = false)
    private String email;

    @Column(nullable = false, length = 50, updatable = false)
    private String name;

    @Column(length = 50)
    private String nickname;

    @Column
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public static User insert(String email, String name) {
        return User.builder()
                .email(email)
                .name(CipherUtils.encrypt(name))
                .profileImage(DEFAULT_PROFILE)
                .role(Role.USER)
                .build();
    }

    public static User update(String nickname, String profileImage) {
        String validProfileImage = StringUtils.hasLength(profileImage)
                ? profileImage
                : DEFAULT_PROFILE;
        return User.builder()
                .nickname(nickname)
                .profileImage(validProfileImage)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    public User(Long idx, String email, String name, String nickname, String profileImage, Role role) {
        this.idx = idx;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.role = role;
    }

    public String role() {
        return role.getRole();
    }

    public String decryptName() {
        return CipherUtils.decrypt(this.name);
    }

    public void update(User user) {
        this.nickname = user.nickname;
        this.profileImage = user.profileImage;
    }
}
