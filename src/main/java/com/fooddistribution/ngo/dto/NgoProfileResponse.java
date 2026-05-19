package com.fooddistribution.ngo.dto;

import java.time.Instant;

public record NgoProfileResponse(
        String id,
        String ngoName,
        String address,
        Double latitude,
        Double longitude,
        String contactPerson,
        String userId,
        Instant createdAt,
        Instant updatedAt
) {
}
