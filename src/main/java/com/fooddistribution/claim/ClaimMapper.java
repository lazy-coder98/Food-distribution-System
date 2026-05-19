package com.fooddistribution.claim;

import com.fooddistribution.claim.dto.ClaimResponse;
import com.fooddistribution.util.ObjectIdMapper;

public final class ClaimMapper {

    private ClaimMapper() {
    }

    public static ClaimResponse toResponse(ClaimRequest claim) {
        return new ClaimResponse(
                ObjectIdMapper.toString(claim.getId()),
                claim.getStatus(),
                claim.getRequestedAt(),
                claim.getApprovedAt(),
                claim.getUpdatedAt(),
                ObjectIdMapper.toString(claim.getFoodPostId()),
                ObjectIdMapper.toString(claim.getNgoId())
        );
    }
}
