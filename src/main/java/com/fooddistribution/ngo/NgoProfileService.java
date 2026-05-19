package com.fooddistribution.ngo;

import com.fooddistribution.exception.ConflictException;
import com.fooddistribution.exception.ResourceNotFoundException;
import com.fooddistribution.ngo.dto.NgoProfileRequest;
import com.fooddistribution.ngo.dto.NgoProfileResponse;
import com.fooddistribution.security.SecurityUtils;
import com.fooddistribution.util.ObjectIdMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NgoProfileService {

    private final NgoProfileRepository repository;

    public NgoProfileResponse create(NgoProfileRequest request) {
        ObjectId userId = SecurityUtils.currentUserId();
        if (repository.existsByUserId(userId)) {
            throw new ConflictException("NGO profile already exists for this user");
        }
        NgoProfile profile = NgoProfile.builder()
                .ngoName(request.ngoName())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .contactPerson(request.contactPerson())
                .userId(userId)
                .build();
        return NgoProfileMapper.toResponse(repository.save(profile));
    }

    public NgoProfileResponse update(NgoProfileRequest request) {
        NgoProfile profile = getByCurrentUserEntity();
        profile.setNgoName(request.ngoName());
        profile.setAddress(request.address());
        profile.setLatitude(request.latitude());
        profile.setLongitude(request.longitude());
        profile.setContactPerson(request.contactPerson());
        return NgoProfileMapper.toResponse(repository.save(profile));
    }

    public NgoProfileResponse mine() {
        return NgoProfileMapper.toResponse(getByCurrentUserEntity());
    }

    public NgoProfile getByCurrentUserEntity() {
        return repository.findByUserId(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));
    }

    public NgoProfile getEntity(String id) {
        return repository.findById(ObjectIdMapper.toObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("NGO profile not found"));
    }
}
