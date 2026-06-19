package com.nutritrack.backend.repository;

import com.nutritrack.backend.entity.Meal;
import com.nutritrack.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUserAndMealDateBetweenOrderByMealDateDescCreatedAtDesc(
            User user,
            LocalDate startDate,
            LocalDate endDate);

    @Query("""
            SELECT m.mealDate AS mealDate,
                   SUM(m.protein) AS totalProtein,
                   SUM(m.carbs) AS totalCarbs,
                   SUM(m.fat) AS totalFat,
                   SUM(m.calories) AS totalCalories
            FROM Meal m
            WHERE m.user = :user
              AND m.mealDate BETWEEN :startDate AND :endDate
            GROUP BY m.mealDate
            ORDER BY m.mealDate ASC
            """)
    List<DailyMetricsAggregate> sumMacrosByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
