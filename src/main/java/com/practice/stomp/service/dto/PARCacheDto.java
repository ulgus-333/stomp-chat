package com.practice.stomp.service.dto;

import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest;

public record PARCacheDto (
        String parRequestId
) {
    public static PARCacheDto from(PreauthenticatedRequest request) {
        return new PARCacheDto(request.getId());
    }
}
