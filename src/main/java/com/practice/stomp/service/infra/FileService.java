package com.practice.stomp.service.infra;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.practice.stomp.config.properties.OciProperties;
import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.request.file.PARGenerateRequestDto;
import com.practice.stomp.domain.response.file.FileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {
    private final ObjectStorage objectStorage;
    private final OciProperties ociProperties;

    public FileResponseDto generatePARUrl(CustomOAuth2User requestUser, PARGenerateRequestDto requestDto) {
        Date expiresAt = Date.from(Instant.now().plus(OciProperties.EXPIRE_DURATION));
        CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                .name("par-" + UUID.randomUUID())
                .objectName(requestDto.generateFilePath(String.valueOf(requestUser.userIdx())))
                .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite)
                .timeExpires(expiresAt)
                .build();

        CreatePreauthenticatedRequestRequest request = CreatePreauthenticatedRequestRequest.builder()
                .namespaceName(ociProperties.getNamespace())
                .bucketName(ociProperties.getBucket())
                .createPreauthenticatedRequestDetails(details)
                .build();

        CreatePreauthenticatedRequestResponse response = objectStorage.createPreauthenticatedRequest(request);

        String parUrl = ociProperties.preAuthenticatedRequestUrl(response.getPreauthenticatedRequest().getAccessUri());

        return new FileResponseDto(parUrl, requestDto.filename());
    }
}
