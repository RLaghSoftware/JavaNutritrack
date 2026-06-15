package com.nutritrack.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealResponse {

    private boolean success;
    private String message;

    public static MealResponse success() {
        return new MealResponse(true, "success");
    }

    public static MealResponse failure(String message) {
        return new MealResponse(false, message);
    }
}
