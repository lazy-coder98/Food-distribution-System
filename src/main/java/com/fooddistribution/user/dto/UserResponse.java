package com.fooddistribution.user.dto;

import com.fooddistribution.user.Role;

import java.time.Instant;

public record UserResponse(
        String id,
        String fullName,
        String email,
        String phoneNumber,
        Role role,
        Instant createdAt
) {
}
