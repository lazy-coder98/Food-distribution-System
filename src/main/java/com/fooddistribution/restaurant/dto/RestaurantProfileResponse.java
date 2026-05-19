package com.fooddistribution.restaurant.dto;

import java.time.Instant;

public record RestaurantProfileResponse(
        String id,
        String restaurantName,
        String address,
        Double latitude,
        Double longitude,
        String contactPerson,
        String userId,
        Instant createdAt,
        Instant updatedAt
) {
}
