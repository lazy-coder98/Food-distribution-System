package com.fooddistribution.foodpost.dto;

import com.fooddistribution.foodpost.FoodStatus;

import java.time.Instant;

public record FoodPostResponse(
        String id,
        String foodName,
        String description,
        Integer quantity,
        Instant expiryTime,
        String foodType,
        String imageUrl,
        Double latitude,
        Double longitude,
        FoodStatus status,
        Instant createdAt,
        Instant updatedAt,
        String restaurantId,
        Double distanceMeters
) {
}
