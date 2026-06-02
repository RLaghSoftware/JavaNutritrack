package com.nutritrack.backend.controller;

import com.nutritrack.backend.dto.UserResponse;
import com.nutritrack.backend.security.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
//on login endpoint
    @GetMapping
    public Map<String, Object> dashboard(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of(
                "message", "Welcome to your NutriTrack dashboard",
                "user", UserResponse.from(principal.getUser())
        );
    }

    /** Sample admin-only route — foundation for role-based access. */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminOnly() {
        return Map.of("message", "Admin access granted");
    }
}
