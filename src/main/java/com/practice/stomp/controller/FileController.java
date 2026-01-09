package com.practice.stomp.controller;

import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.request.file.PARGenerateRequestDto;
import com.practice.stomp.domain.response.file.FileResponseDto;
import com.practice.stomp.service.infra.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/files")
@RestController
public class FileController {
    private final FileService fileService;

    @PostMapping("/presigned")
    public ResponseEntity<FileResponseDto> generatePresignedUrl(@AuthenticationPrincipal CustomOAuth2User requestUser,
                                                                @RequestBody @Valid PARGenerateRequestDto requestDto) {
        return ResponseEntity.ok(fileService.generatePARUrl(requestUser, requestDto));
    }
}
