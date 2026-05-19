package com.fooddistribution.restaurant;

import com.fooddistribution.restaurant.dto.RestaurantProfileResponse;
import com.fooddistribution.util.ObjectIdMapper;

public final class RestaurantProfileMapper {

    private RestaurantProfileMapper() {
    }

    public static RestaurantProfileResponse toResponse(RestaurantProfile profile) {
        return new RestaurantProfileResponse(
                ObjectIdMapper.toString(profile.getId()),
                profile.getRestaurantName(),
                profile.getAddress(),
                profile.getLatitude(),
                profile.getLongitude(),
                profile.getContactPerson(),
                ObjectIdMapper.toString(profile.getUserId()),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
