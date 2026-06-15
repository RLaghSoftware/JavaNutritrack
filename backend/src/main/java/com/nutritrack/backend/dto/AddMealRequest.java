package com.nutritrack.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddMealRequest {

    private String mealName;
    private LocalDate date;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double calories;
}
