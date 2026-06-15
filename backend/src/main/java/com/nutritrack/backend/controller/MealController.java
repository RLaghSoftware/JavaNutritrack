package com.nutritrack.backend.controller;

import com.nutritrack.backend.dto.AddMealRequest;
import com.nutritrack.backend.dto.MealResponse;
import com.nutritrack.backend.security.UserPrincipal;
import com.nutritrack.backend.service.MealService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<MealResponse> addMeal(
            @RequestBody AddMealRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(mealService.addMeal(request, principal.getUser()));
    }
}
