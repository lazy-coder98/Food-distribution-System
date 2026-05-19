package com.fooddistribution.claim;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface ClaimRequestRepository extends MongoRepository<ClaimRequest, ObjectId> {

    Page<ClaimRequest> findByNgoId(ObjectId ngoId, Pageable pageable);

    Page<ClaimRequest> findByFoodPostId(ObjectId foodPostId, Pageable pageable);

    Page<ClaimRequest> findByStatus(ClaimStatus status, Pageable pageable);

    boolean existsByFoodPostIdAndStatusIn(ObjectId foodPostId, Collection<ClaimStatus> statuses);

    long countByStatus(ClaimStatus status);
}
