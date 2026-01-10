package com.practice.stomp.config;

import com.practice.stomp.service.user.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/login", "/oauth2/**", "/error", "/main.css", "/stomp.js", "/mypage.js").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(c -> c.userService(oAuth2UserService))
                        .defaultSuccessUrl("/", true))
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login"))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
