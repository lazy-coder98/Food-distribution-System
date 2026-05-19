package com.fooddistribution.restaurant;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RestaurantProfileRepository extends MongoRepository<RestaurantProfile, ObjectId> {

    Optional<RestaurantProfile> findByUserId(ObjectId userId);

    boolean existsByUserId(ObjectId userId);
}
