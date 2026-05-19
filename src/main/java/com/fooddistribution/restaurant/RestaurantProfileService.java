package com.fooddistribution.restaurant;

import com.fooddistribution.exception.ConflictException;
import com.fooddistribution.exception.ResourceNotFoundException;
import com.fooddistribution.restaurant.dto.RestaurantProfileRequest;
import com.fooddistribution.restaurant.dto.RestaurantProfileResponse;
import com.fooddistribution.security.SecurityUtils;
import com.fooddistribution.util.ObjectIdMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantProfileService {

    private final RestaurantProfileRepository repository;

    public RestaurantProfileResponse create(RestaurantProfileRequest request) {
        ObjectId userId = SecurityUtils.currentUserId();
        if (repository.existsByUserId(userId)) {
            throw new ConflictException("Restaurant profile already exists for this user");
        }
        RestaurantProfile profile = RestaurantProfile.builder()
                .restaurantName(request.restaurantName())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .contactPerson(request.contactPerson())
                .userId(userId)
                .build();
        return RestaurantProfileMapper.toResponse(repository.save(profile));
    }

    public RestaurantProfileResponse update(RestaurantProfileRequest request) {
        RestaurantProfile profile = getByCurrentUserEntity();
        profile.setRestaurantName(request.restaurantName());
        profile.setAddress(request.address());
        profile.setLatitude(request.latitude());
        profile.setLongitude(request.longitude());
        profile.setContactPerson(request.contactPerson());
        return RestaurantProfileMapper.toResponse(repository.save(profile));
    }

    public RestaurantProfileResponse mine() {
        return RestaurantProfileMapper.toResponse(getByCurrentUserEntity());
    }

    public RestaurantProfile getByCurrentUserEntity() {
        return repository.findByUserId(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant profile not found"));
    }

    public RestaurantProfile getEntity(String id) {
        return repository.findById(ObjectIdMapper.toObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant profile not found"));
    }
}
