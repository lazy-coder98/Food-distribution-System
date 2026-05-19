package com.fooddistribution.foodpost.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record FoodPostRequest(
        @NotBlank @Size(max = 120) String foodName,
        @NotBlank @Size(max = 1000) String description,
        @NotNull @Min(1) Integer quantity,
        @NotNull @Future Instant expiryTime,
        @NotBlank @Size(max = 80) String foodType,
        @Size(max = 500) String imageUrl,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {
}
