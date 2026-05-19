package com.fooddistribution.foodpost;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FoodExpiryScheduler {

    private final FoodPostRepository repository;

    @Scheduled(cron = "${app.scheduler.expire-food-posts-cron}")
    public void expireOldFoodPosts() {
        List<FoodPost> expired = repository.findByStatusAndExpiryTimeBefore(FoodStatus.AVAILABLE, Instant.now());
        expired.forEach(foodPost -> foodPost.setStatus(FoodStatus.EXPIRED));
        if (!expired.isEmpty()) {
            repository.saveAll(expired);
            log.info("Expired {} food posts", expired.size());
        }
    }
}
