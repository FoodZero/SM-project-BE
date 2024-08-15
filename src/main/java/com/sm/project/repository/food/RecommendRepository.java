package com.sm.project.repository.food;

import com.sm.project.domain.food.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    Recommend findByMemberIdAndRecipeId(Long memberId, Long recipeId);

    boolean existsByMemberIdAndRecipeId(Long memberId, Long recipeId);
}
