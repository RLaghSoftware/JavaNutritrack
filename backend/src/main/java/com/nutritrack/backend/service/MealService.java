package com.nutritrack.backend.service;

import com.nutritrack.backend.dto.AddMealRequest;
import com.nutritrack.backend.dto.MealResponse;
import com.nutritrack.backend.entity.Meal;
import com.nutritrack.backend.entity.User;
import com.nutritrack.backend.repository.MealRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class MealService {

    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    @Transactional
    public MealResponse addMeal(AddMealRequest request, User user) {
        String validationError = validate(request);
        if (validationError != null) {
            return MealResponse.failure(validationError);
        }

        try {
            Meal mealToAdd = Meal.builder()
                    .user(user)
                    .mealName(request.getMealName().trim())
                    .mealDate(request.getDate())
                    .protein(BigDecimal.valueOf(request.getProtein()))
                    .carbs(BigDecimal.valueOf(request.getCarbs()))
                    .fat(BigDecimal.valueOf(request.getFat()))
                    .calories(BigDecimal.valueOf(request.getCalories()))
                    .build();

            mealRepository.save(mealToAdd);
        } catch (Exception e) {
            return MealResponse.failure("Something went wrong when adding meal");
        }
        return MealResponse.success();
    }

    private String validate(AddMealRequest request) {
        if (request.getMealName() == null || request.getMealName().isBlank()) {
            return "Meal name is required.";
        }
        if (request.getDate() == null) {
            return "Date is required.";
        }
        if (!isNonNegative(request.getProtein())) {
            return "Protein must be a non-negative number.";
        }
        if (!isNonNegative(request.getCarbs())) {
            return "Carbs must be a non-negative number.";
        }
        if (!isNonNegative(request.getFat())) {
            return "Fat must be a non-negative number.";
        }
        if (!isNonNegative(request.getCalories())) {
            return "Calories must be a non-negative number.";
        }
        return null;
    }

    private boolean isNonNegative(Double value) {
        return value != null && value >= 0;
    }
}
