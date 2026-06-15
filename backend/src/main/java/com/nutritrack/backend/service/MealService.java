package com.nutritrack.backend.service;

import com.nutritrack.backend.dto.AddMealRequest;
import com.nutritrack.backend.dto.MealResponse;
import com.nutritrack.backend.entity.User;
import org.springframework.stereotype.Service;

@Service
public class MealService {

    public MealResponse addMeal(AddMealRequest request, User user) {
        String validationError = validate(request);
        if (validationError != null) {
            return MealResponse.failure(validationError);
        }

        // TODO: persist meal for user
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
