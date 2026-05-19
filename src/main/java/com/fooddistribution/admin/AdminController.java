package com.fooddistribution.admin;

import com.fooddistribution.admin.dto.DashboardStatsResponse;
import com.fooddistribution.claim.ClaimService;
import com.fooddistribution.claim.ClaimStatus;
import com.fooddistribution.claim.dto.ClaimResponse;
import com.fooddistribution.foodpost.FoodPostService;
import com.fooddistribution.user.UserService;
import com.fooddistribution.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final FoodPostService foodPostService;
    private final ClaimService claimService;
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> users(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(userService.list(pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> user(@PathVariable String id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/food-posts/{id}")
    public ResponseEntity<Void> removeFoodPost(@PathVariable String id) {
        foodPostService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/claims")
    public ResponseEntity<Page<ClaimResponse>> claims(
            @RequestParam(required = false) ClaimStatus status,
            @PageableDefault(size = 20, sort = "requestedAt") Pageable pageable
    ) {
        return ResponseEntity.ok(claimService.list(status, pageable));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> dashboard() {
        return ResponseEntity.ok(adminService.dashboardStats());
    }
}
