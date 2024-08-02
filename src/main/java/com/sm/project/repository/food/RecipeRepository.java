package com.sm.project.repository.food;

import com.sm.project.domain.food.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    boolean existsByName(String name);
}
