package com.fooddistribution.admin.dto;

import java.util.Map;

public record DashboardStatsResponse(
        long totalUsers,
        long totalFoodPosts,
        long totalClaims,
        Map<String, Long> foodPostsByStatus,
        Map<String, Long> claimsByStatus
) {
}
