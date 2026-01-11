package com.practice.stomp.domain.response.file;

public record FileResponseDto (
        String parUrl,
        String filePath,
        String fileName
) {
}
