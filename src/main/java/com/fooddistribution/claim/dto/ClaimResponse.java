package com.fooddistribution.claim.dto;

import com.fooddistribution.claim.ClaimStatus;

import java.time.Instant;

public record ClaimResponse(
        String id,
        ClaimStatus status,
        Instant requestedAt,
        Instant approvedAt,
        Instant updatedAt,
        String foodPostId,
        String ngoId
) {
}
