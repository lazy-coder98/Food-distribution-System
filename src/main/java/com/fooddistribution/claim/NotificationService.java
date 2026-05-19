package com.fooddistribution.claim;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void notifyClaimStatusChanged(ClaimRequest claimRequest) {
        log.info("Notification placeholder: claim {} moved to {}", claimRequest.getId(), claimRequest.getStatus());
    }
}
