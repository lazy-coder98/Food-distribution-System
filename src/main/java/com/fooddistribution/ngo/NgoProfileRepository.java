package com.fooddistribution.ngo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NgoProfileRepository extends MongoRepository<NgoProfile, ObjectId> {

    Optional<NgoProfile> findByUserId(ObjectId userId);

    boolean existsByUserId(ObjectId userId);
}
