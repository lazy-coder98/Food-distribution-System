package com.fooddistribution.restaurant;

import com.fooddistribution.restaurant.dto.RestaurantProfileRequest;
import com.fooddistribution.restaurant.dto.RestaurantProfileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Restaurant Profiles")
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantProfileController {

    private final RestaurantProfileService service;

    @PostMapping("/me")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<RestaurantProfileResponse> create(@Valid @RequestBody RestaurantProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<RestaurantProfileResponse> update(@Valid @RequestBody RestaurantProfileRequest request) {
        return ResponseEntity.ok(service.update(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<RestaurantProfileResponse> mine() {
        return ResponseEntity.ok(service.mine());
    }
}
