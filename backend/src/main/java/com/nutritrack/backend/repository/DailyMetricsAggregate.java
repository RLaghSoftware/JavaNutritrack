package com.nutritrack.backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyMetricsAggregate {

    LocalDate getMealDate();

    BigDecimal getTotalProtein();

    BigDecimal getTotalCarbs();

    BigDecimal getTotalFat();

    BigDecimal getTotalCalories();
}
