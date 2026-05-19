package com.fooddistribution.claim.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateClaimRequest(@NotBlank String foodPostId) {
}
