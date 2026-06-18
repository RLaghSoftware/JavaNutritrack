package com.nutritrack.backend.dto;

import com.nutritrack.backend.entity.Meal;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MealDto {

    private Long id;
    private String mealName;
    private LocalDate date;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
    private BigDecimal calories;

    public static MealDto createMealDto(Meal meal) {
        return MealDto.builder()
                .id(meal.getId())
                .mealName(meal.getMealName())
                .date(meal.getMealDate())
                .protein(meal.getProtein())
                .carbs(meal.getCarbs())
                .fat(meal.getFat())
                .calories(meal.getCalories())
                .build();
    }
}
