package com.practice.stomp.service.infra;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeletePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.practice.stomp.config.properties.OciProperties;
import com.practice.stomp.domain.dto.CustomOAuth2User;
import com.practice.stomp.domain.request.file.PARRequestDto;
import com.practice.stomp.domain.response.file.FileResponseDto;
import com.practice.stomp.service.dto.CacheKey;
import com.practice.stomp.service.dto.PARCacheDto;
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

    private final RedisCacheService cacheService;

    public FileResponseDto generatePARUrl(CustomOAuth2User requestUser, PARRequestDto requestDto) {
        Date expiresAt = Date.from(Instant.now().plus(OciProperties.WRITE_EXPIRE_DURATION));
        String filePath = requestDto.generateFilePath(String.valueOf(requestUser.userIdx()));

        CreatePreauthenticatedRequestResponse response = generatePar(filePath, CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite, expiresAt);

        String parUrl = ociProperties.preAuthenticatedRequestUrl(response.getPreauthenticatedRequest().getAccessUri());

        String cacheKey = CacheKey.OCI_PAR_KEY.generateKey(requestDto.pathType().name(), String.valueOf(requestUser.userIdx()));
        cacheService.set(cacheKey, PARCacheDto.from(response.getPreauthenticatedRequest()), CacheKey.OCI_PAR_KEY.expire());

        return new FileResponseDto(parUrl, filePath, requestDto.filename());
    }

    public void expireRemainPAR(CustomOAuth2User requestUser, PARRequestDto requestDto) {
        String cacheKey = CacheKey.OCI_PAR_KEY.generateKey(requestDto.pathType().name(), String.valueOf(requestUser.userIdx()));

        cacheService.get(cacheKey, PARCacheDto.class)
                .ifPresent(ociId -> {
                    DeletePreauthenticatedRequestRequest request = DeletePreauthenticatedRequestRequest.builder()
                            .bucketName(ociProperties.getBucket())
                            .namespaceName(ociProperties.getNamespace())
                            .parId(ociId.parRequestId())
                            .build();

                    objectStorage.deletePreauthenticatedRequest(request);
                    cacheService.delete(cacheKey);
                });
    }

    public String generateParReadUrl(String filePath) {
        Date expiresAt = Date.from(Instant.now().plus(OciProperties.READ_EXPIRE_DURATION));
        CreatePreauthenticatedRequestResponse response = generatePar(filePath, CreatePreauthenticatedRequestDetails.AccessType.ObjectRead, expiresAt);

        return ociProperties.preAuthenticatedRequestUrl(response.getPreauthenticatedRequest().getAccessUri());
    }

    private CreatePreauthenticatedRequestResponse generatePar(String filePath, CreatePreauthenticatedRequestDetails.AccessType accessType, Date expireAt) {
        CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                .name("par-" + UUID.randomUUID())
                .objectName(filePath)
                .accessType(accessType)
                .timeExpires(expireAt)
                .build();

        CreatePreauthenticatedRequestRequest request = CreatePreauthenticatedRequestRequest.builder()
                .namespaceName(ociProperties.getNamespace())
                .bucketName(ociProperties.getBucket())
                .createPreauthenticatedRequestDetails(details)
                .build();

        return objectStorage.createPreauthenticatedRequest(request);
    }
}
