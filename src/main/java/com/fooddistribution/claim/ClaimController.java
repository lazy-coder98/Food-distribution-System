package com.fooddistribution.claim;

import com.fooddistribution.claim.dto.ClaimResponse;
import com.fooddistribution.claim.dto.CreateClaimRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Claims")
@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService service;

    @PostMapping
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<ClaimResponse> create(@Valid @RequestBody CreateClaimRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT')")
    public ResponseEntity<Page<ClaimResponse>> list(
            @RequestParam(required = false) ClaimStatus status,
            @PageableDefault(size = 20, sort = "requestedAt") Pageable pageable
    ) {
        return ResponseEntity.ok(service.list(status, pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<Page<ClaimResponse>> mine(@PageableDefault(size = 20, sort = "requestedAt") Pageable pageable) {
        return ResponseEntity.ok(service.mine(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT')")
    public ResponseEntity<ClaimResponse> approve(@PathVariable String id) {
        return ResponseEntity.ok(service.approve(id));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT')")
    public ResponseEntity<ClaimResponse> reject(@PathVariable String id) {
        return ResponseEntity.ok(service.reject(id));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT','NGO')")
    public ResponseEntity<ClaimResponse> complete(@PathVariable String id) {
        return ResponseEntity.ok(service.complete(id));
    }
}
