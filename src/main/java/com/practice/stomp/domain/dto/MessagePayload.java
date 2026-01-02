package com.practice.stomp.domain.dto;

public record MessagePayload (
        String sender,
        String message
) {

}
