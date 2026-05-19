package com.fooddistribution.foodpost;

import com.fooddistribution.foodpost.dto.FoodPostRequest;
import com.fooddistribution.foodpost.dto.FoodPostResponse;
import com.fooddistribution.foodpost.dto.FoodPostStatusUpdateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@Validated
@Tag(name = "Food Posts")
@RestController
@RequestMapping("/api/food-posts")
@RequiredArgsConstructor
public class FoodPostController {

    private final FoodPostService service;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<FoodPostResponse> create(@Valid @RequestBody FoodPostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<FoodPostResponse>> list(
            @RequestParam(required = false) FoodStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(service.list(status, pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<Page<FoodPostResponse>> mine(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(service.mine(pageable));
    }

    @GetMapping("/nearby")
    public ResponseEntity<Page<FoodPostResponse>> nearby(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam(defaultValue = "10") @Positive Double radiusKm,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(service.nearby(latitude, longitude, radiusKm, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodPostResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<FoodPostResponse> update(@PathVariable String id, @Valid @RequestBody FoodPostRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT')")
    public ResponseEntity<FoodPostResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody FoodPostStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(service.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
