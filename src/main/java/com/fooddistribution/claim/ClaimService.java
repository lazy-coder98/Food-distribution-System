package com.fooddistribution.claim;

import com.fooddistribution.claim.dto.ClaimResponse;
import com.fooddistribution.claim.dto.CreateClaimRequest;
import com.fooddistribution.exception.BadRequestException;
import com.fooddistribution.exception.ConflictException;
import com.fooddistribution.exception.ResourceNotFoundException;
import com.fooddistribution.foodpost.FoodPost;
import com.fooddistribution.foodpost.FoodPostService;
import com.fooddistribution.foodpost.FoodStatus;
import com.fooddistribution.ngo.NgoProfile;
import com.fooddistribution.ngo.NgoProfileService;
import com.fooddistribution.restaurant.RestaurantProfile;
import com.fooddistribution.restaurant.RestaurantProfileService;
import com.fooddistribution.security.SecurityUtils;
import com.fooddistribution.util.ObjectIdMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRequestRepository repository;
    private final FoodPostService foodPostService;
    private final NgoProfileService ngoProfileService;
    private final RestaurantProfileService restaurantProfileService;
    private final NotificationService notificationService;

    public ClaimResponse create(CreateClaimRequest request) {
        NgoProfile ngo = ngoProfileService.getByCurrentUserEntity();
        FoodPost foodPost = foodPostService.getEntity(request.foodPostId());

        if (foodPost.getStatus() != FoodStatus.AVAILABLE || foodPost.getExpiryTime().isBefore(Instant.now())) {
            throw new BadRequestException("Food post is not available for claim");
        }
        if (repository.existsByFoodPostIdAndStatusIn(foodPost.getId(), List.of(ClaimStatus.PENDING, ClaimStatus.APPROVED))) {
            throw new ConflictException("Food post already has an active claim");
        }

        ClaimRequest claim = ClaimRequest.builder()
                .status(ClaimStatus.PENDING)
                .foodPostId(foodPost.getId())
                .ngoId(ngo.getId())
                .build();
        ClaimRequest saved = repository.save(claim);
        notificationService.notifyClaimStatusChanged(saved);
        return ClaimMapper.toResponse(saved);
    }

    public Page<ClaimResponse> list(ClaimStatus status, Pageable pageable) {
        Page<ClaimRequest> page = status == null ? repository.findAll(pageable) : repository.findByStatus(status, pageable);
        return page.map(ClaimMapper::toResponse);
    }

    public Page<ClaimResponse> mine(Pageable pageable) {
        NgoProfile ngo = ngoProfileService.getByCurrentUserEntity();
        return repository.findByNgoId(ngo.getId(), pageable).map(ClaimMapper::toResponse);
    }

    public ClaimResponse getById(String id) {
        return ClaimMapper.toResponse(getEntity(id));
    }

    public ClaimResponse approve(String id) {
        ClaimRequest claim = getEntity(id);
        FoodPost foodPost = foodPostService.getEntity(claim.getFoodPostId().toHexString());
        ensureAdminOrOwningRestaurant(foodPost);
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new BadRequestException("Only pending claims can be approved");
        }
        if (foodPost.getStatus() != FoodStatus.AVAILABLE) {
            throw new BadRequestException("Food post is no longer available");
        }
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setApprovedAt(Instant.now());
        foodPost.setStatus(FoodStatus.CLAIMED);
        foodPostService.save(foodPost);
        ClaimRequest saved = repository.save(claim);
        notificationService.notifyClaimStatusChanged(saved);
        return ClaimMapper.toResponse(saved);
    }

    public ClaimResponse reject(String id) {
        ClaimRequest claim = getEntity(id);
        FoodPost foodPost = foodPostService.getEntity(claim.getFoodPostId().toHexString());
        ensureAdminOrOwningRestaurant(foodPost);
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new BadRequestException("Only pending claims can be rejected");
        }
        claim.setStatus(ClaimStatus.REJECTED);
        ClaimRequest saved = repository.save(claim);
        notificationService.notifyClaimStatusChanged(saved);
        return ClaimMapper.toResponse(saved);
    }

    public ClaimResponse complete(String id) {
        ClaimRequest claim = getEntity(id);
        FoodPost foodPost = foodPostService.getEntity(claim.getFoodPostId().toHexString());
        ensureAllowedToComplete(claim, foodPost);
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new BadRequestException("Only approved claims can be completed");
        }
        claim.setStatus(ClaimStatus.COMPLETED);
        foodPost.setStatus(FoodStatus.COMPLETED);
        foodPostService.save(foodPost);
        ClaimRequest saved = repository.save(claim);
        notificationService.notifyClaimStatusChanged(saved);
        return ClaimMapper.toResponse(saved);
    }

    public void delete(String id) {
        repository.delete(getEntity(id));
    }

    private ClaimRequest getEntity(String id) {
        return repository.findById(ObjectIdMapper.toObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Claim request not found"));
    }

    private void ensureAdminOrOwningRestaurant(FoodPost foodPost) {
        if (SecurityUtils.hasRole("ADMIN")) {
            return;
        }
        RestaurantProfile restaurant = restaurantProfileService.getByCurrentUserEntity();
        if (!foodPost.getRestaurantId().equals(restaurant.getId())) {
            throw new BadRequestException("Claim does not belong to the current restaurant");
        }
    }

    private void ensureAllowedToComplete(ClaimRequest claim, FoodPost foodPost) {
        if (SecurityUtils.hasRole("ADMIN")) {
            return;
        }
        if (SecurityUtils.hasRole("RESTAURANT")) {
            ensureAdminOrOwningRestaurant(foodPost);
            return;
        }
        NgoProfile ngo = ngoProfileService.getByCurrentUserEntity();
        if (!claim.getNgoId().equals(ngo.getId())) {
            throw new BadRequestException("Claim does not belong to the current NGO");
        }
    }
}
