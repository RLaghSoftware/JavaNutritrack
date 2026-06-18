package com.nutritrack.backend.controller;

import com.nutritrack.backend.dto.AddMealRequest;
import com.nutritrack.backend.dto.MealDto;
import com.nutritrack.backend.dto.MealResponse;
import com.nutritrack.backend.security.UserPrincipal;
import com.nutritrack.backend.service.MealService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping
    public ResponseEntity<?> getMeals(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(MealResponse.failure("End date must be on or after start date."));
        }
        List<MealDto> meals = mealService.getMealsInRange(principal.getUser(), startDate, endDate);
        return ResponseEntity.ok(meals);
    }

    @PostMapping
    public ResponseEntity<MealResponse> addMeal(
            @RequestBody AddMealRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(mealService.addMeal(request, principal.getUser()));
    }
}
