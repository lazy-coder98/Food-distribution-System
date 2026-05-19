package com.fooddistribution.auth.dto;

import com.fooddistribution.user.dto.UserResponse;

public record AuthResponse(String token, String tokenType, UserResponse user) {
}
