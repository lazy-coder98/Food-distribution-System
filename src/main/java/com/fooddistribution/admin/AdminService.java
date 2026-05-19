package com.fooddistribution.admin;

import com.fooddistribution.admin.dto.DashboardStatsResponse;
import com.fooddistribution.claim.ClaimRequestRepository;
import com.fooddistribution.claim.ClaimStatus;
import com.fooddistribution.foodpost.FoodPostRepository;
import com.fooddistribution.foodpost.FoodStatus;
import com.fooddistribution.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final FoodPostRepository foodPostRepository;
    private final ClaimRequestRepository claimRequestRepository;

    public DashboardStatsResponse dashboardStats() {
        Map<String, Long> foodStatusCounts = Arrays.stream(FoodStatus.values())
                .collect(Collectors.toMap(Enum::name, foodPostRepository::countByStatus));
        Map<String, Long> claimStatusCounts = Arrays.stream(ClaimStatus.values())
                .collect(Collectors.toMap(Enum::name, claimRequestRepository::countByStatus));

        return new DashboardStatsResponse(
                userRepository.count(),
                foodPostRepository.count(),
                claimRequestRepository.count(),
                foodStatusCounts,
                claimStatusCounts
        );
    }
}
