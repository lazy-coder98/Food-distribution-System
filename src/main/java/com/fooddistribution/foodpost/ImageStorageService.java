package com.fooddistribution.foodpost;

import org.springframework.stereotype.Service;

@Service
public class ImageStorageService {

    public String normalizeImageUrl(String imageUrl) {
        return imageUrl == null || imageUrl.isBlank() ? null : imageUrl.trim();
    }
}
