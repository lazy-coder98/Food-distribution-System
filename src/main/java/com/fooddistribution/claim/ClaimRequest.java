package com.fooddistribution.claim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "claim_requests")
public class ClaimRequest {

    @Id
    private ObjectId id;

    @Indexed
    private ClaimStatus status;

    @CreatedDate
    private Instant requestedAt;

    private Instant approvedAt;

    @Indexed
    private ObjectId foodPostId;

    @Indexed
    private ObjectId ngoId;

    @LastModifiedDate
    private Instant updatedAt;
}
