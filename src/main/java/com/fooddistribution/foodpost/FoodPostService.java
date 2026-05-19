package com.fooddistribution.foodpost;

import com.fooddistribution.exception.BadRequestException;
import com.fooddistribution.exception.ResourceNotFoundException;
import com.fooddistribution.foodpost.dto.FoodPostRequest;
import com.fooddistribution.foodpost.dto.FoodPostResponse;
import com.fooddistribution.foodpost.dto.FoodPostStatusUpdateRequest;
import com.fooddistribution.restaurant.RestaurantProfile;
import com.fooddistribution.restaurant.RestaurantProfileService;
import com.fooddistribution.security.SecurityUtils;
import com.fooddistribution.util.ObjectIdMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodPostService {

    private final FoodPostRepository repository;
    private final RestaurantProfileService restaurantProfileService;
    private final ImageStorageService imageStorageService;
    private final MongoTemplate mongoTemplate;

    public FoodPostResponse create(FoodPostRequest request) {
        RestaurantProfile restaurant = restaurantProfileService.getByCurrentUserEntity();
        FoodPost foodPost = FoodPost.builder()
                .foodName(request.foodName())
                .description(request.description())
                .quantity(request.quantity())
                .expiryTime(request.expiryTime())
                .foodType(request.foodType())
                .imageUrl(imageStorageService.normalizeImageUrl(request.imageUrl()))
                .latitude(request.latitude())
                .longitude(request.longitude())
                .location(new GeoJsonPoint(request.longitude(), request.latitude()))
                .status(FoodStatus.AVAILABLE)
                .restaurantId(restaurant.getId())
                .build();
        return FoodPostMapper.toResponse(repository.save(foodPost));
    }

    public Page<FoodPostResponse> list(FoodStatus status, Pageable pageable) {
        Page<FoodPost> page = status == null ? repository.findAll(pageable) : repository.findByStatus(status, pageable);
        return page.map(FoodPostMapper::toResponse);
    }

    public Page<FoodPostResponse> mine(Pageable pageable) {
        RestaurantProfile restaurant = restaurantProfileService.getByCurrentUserEntity();
        return repository.findByRestaurantId(restaurant.getId(), pageable).map(FoodPostMapper::toResponse);
    }

    public FoodPostResponse getById(String id) {
        return FoodPostMapper.toResponse(getEntity(id));
    }

    public FoodPostResponse update(String id, FoodPostRequest request) {
        FoodPost foodPost = getEntity(id);
        ensureOwnedByCurrentRestaurant(foodPost);
        foodPost.setFoodName(request.foodName());
        foodPost.setDescription(request.description());
        foodPost.setQuantity(request.quantity());
        foodPost.setExpiryTime(request.expiryTime());
        foodPost.setFoodType(request.foodType());
        foodPost.setImageUrl(imageStorageService.normalizeImageUrl(request.imageUrl()));
        foodPost.setLatitude(request.latitude());
        foodPost.setLongitude(request.longitude());
        foodPost.setLocation(new GeoJsonPoint(request.longitude(), request.latitude()));
        return FoodPostMapper.toResponse(repository.save(foodPost));
    }

    public FoodPostResponse updateStatus(String id, FoodPostStatusUpdateRequest request) {
        FoodPost foodPost = getEntity(id);
        ensureAdminOrOwnedByCurrentRestaurant(foodPost);
        if (request.status() == FoodStatus.CLAIMED) {
            throw new BadRequestException("Use the claim workflow to mark food as claimed");
        }
        foodPost.setStatus(request.status());
        return FoodPostMapper.toResponse(repository.save(foodPost));
    }

    public void delete(String id) {
        FoodPost foodPost = getEntity(id);
        ensureAdminOrOwnedByCurrentRestaurant(foodPost);
        repository.delete(foodPost);
    }

    public Page<FoodPostResponse> nearby(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
        NearQuery nearQuery = NearQuery.near(new Point(longitude, latitude), Metrics.KILOMETERS)
                .maxDistance(new Distance(radiusKm, Metrics.KILOMETERS))
                .query(org.springframework.data.mongodb.core.query.Query.query(
                        Criteria.where("status").is(FoodStatus.AVAILABLE)
                                .and("expiryTime").gt(Instant.now())
                ))
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize());

        List<FoodPostResponse> responses = mongoTemplate.geoNear(nearQuery, FoodPost.class)
                .getContent()
                .stream()
                .map(result -> FoodPostMapper.toResponse(result.getContent(), result.getDistance().getValue() * 1000))
                .toList();
        return new PageImpl<>(responses, pageable, responses.size());
    }

    public FoodPost getEntity(String id) {
        return repository.findById(ObjectIdMapper.toObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Food post not found"));
    }

    public FoodPost save(FoodPost foodPost) {
        return repository.save(foodPost);
    }

    private void ensureOwnedByCurrentRestaurant(FoodPost foodPost) {
        RestaurantProfile restaurant = restaurantProfileService.getByCurrentUserEntity();
        if (!foodPost.getRestaurantId().equals(restaurant.getId())) {
            throw new BadRequestException("Food post does not belong to the current restaurant");
        }
    }

    private void ensureAdminOrOwnedByCurrentRestaurant(FoodPost foodPost) {
        if (SecurityUtils.hasRole("ADMIN")) {
            return;
        }
        ensureOwnedByCurrentRestaurant(foodPost);
    }
}
