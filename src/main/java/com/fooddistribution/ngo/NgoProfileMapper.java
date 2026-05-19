package com.fooddistribution.ngo;

import com.fooddistribution.ngo.dto.NgoProfileResponse;
import com.fooddistribution.util.ObjectIdMapper;

public final class NgoProfileMapper {

    private NgoProfileMapper() {
    }

    public static NgoProfileResponse toResponse(NgoProfile profile) {
        return new NgoProfileResponse(
                ObjectIdMapper.toString(profile.getId()),
                profile.getNgoName(),
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
