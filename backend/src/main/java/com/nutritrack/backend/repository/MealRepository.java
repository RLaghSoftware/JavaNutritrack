package com.nutritrack.backend.repository;

import com.nutritrack.backend.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUserIdOrderByMealDateDescCreatedAtDesc(Long userId);
}
