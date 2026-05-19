package com.fooddistribution.security;

import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static ObjectId currentUserId() {
        return currentUser().getId();
    }

    public static SecurityUserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (SecurityUserDetails) authentication.getPrincipal();
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
}
