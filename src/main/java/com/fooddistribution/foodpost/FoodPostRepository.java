package com.fooddistribution.foodpost;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface FoodPostRepository extends MongoRepository<FoodPost, ObjectId> {

    Page<FoodPost> findByStatus(FoodStatus status, Pageable pageable);

    Page<FoodPost> findByRestaurantId(ObjectId restaurantId, Pageable pageable);

    List<FoodPost> findByStatusAndExpiryTimeBefore(FoodStatus status, Instant now);

    long countByStatus(FoodStatus status);
}
