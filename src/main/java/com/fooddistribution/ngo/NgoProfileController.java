package com.fooddistribution.ngo;

import com.fooddistribution.ngo.dto.NgoProfileRequest;
import com.fooddistribution.ngo.dto.NgoProfileResponse;
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

@Tag(name = "NGO Profiles")
@RestController
@RequestMapping("/api/ngos")
@RequiredArgsConstructor
public class NgoProfileController {

    private final NgoProfileService service;

    @PostMapping("/me")
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<NgoProfileResponse> create(@Valid @RequestBody NgoProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<NgoProfileResponse> update(@Valid @RequestBody NgoProfileRequest request) {
        return ResponseEntity.ok(service.update(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<NgoProfileResponse> mine() {
        return ResponseEntity.ok(service.mine());
    }
}
