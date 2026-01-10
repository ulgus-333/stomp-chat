package com.practice.stomp.domain.request.file;

import com.practice.stomp.domain.request.type.FilePathType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PARRequestDto(
        @NotNull(message = "파일 경로 타입을 지정해주세요.")
        FilePathType pathType,
        @NotBlank(message = "파일이름을 입력해주세요.")
        String filename
) {
    public String generateFilePath(String... variables) {
        return pathType.generateFilePath(variables) + filename;
    }
}
