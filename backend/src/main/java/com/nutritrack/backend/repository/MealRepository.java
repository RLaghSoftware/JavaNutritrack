package com.nutritrack.backend.repository;

import com.nutritrack.backend.entity.Meal;
import com.nutritrack.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUserAndMealDateBetweenOrderByMealDateDescCreatedAtDesc(
            User user,
            LocalDate startDate,
            LocalDate endDate);
}
