package com.fooddistribution.foodpost.dto;

import com.fooddistribution.foodpost.FoodStatus;
import jakarta.validation.constraints.NotNull;

public record FoodPostStatusUpdateRequest(@NotNull FoodStatus status) {
}
