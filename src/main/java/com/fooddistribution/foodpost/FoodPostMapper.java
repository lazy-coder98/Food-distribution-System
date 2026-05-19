package com.fooddistribution.foodpost;

import com.fooddistribution.foodpost.dto.FoodPostResponse;
import com.fooddistribution.util.ObjectIdMapper;

public final class FoodPostMapper {

    private FoodPostMapper() {
    }

    public static FoodPostResponse toResponse(FoodPost foodPost) {
        return toResponse(foodPost, null);
    }

    public static FoodPostResponse toResponse(FoodPost foodPost, Double distanceMeters) {
        return new FoodPostResponse(
                ObjectIdMapper.toString(foodPost.getId()),
                foodPost.getFoodName(),
                foodPost.getDescription(),
                foodPost.getQuantity(),
                foodPost.getExpiryTime(),
                foodPost.getFoodType(),
                foodPost.getImageUrl(),
                foodPost.getLatitude(),
                foodPost.getLongitude(),
                foodPost.getStatus(),
                foodPost.getCreatedAt(),
                foodPost.getUpdatedAt(),
                ObjectIdMapper.toString(foodPost.getRestaurantId()),
                distanceMeters
        );
    }
}
