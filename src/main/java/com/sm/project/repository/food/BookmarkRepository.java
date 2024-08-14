package com.sm.project.repository.food;

import com.sm.project.domain.food.Bookmark;
import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByRecipe(Recipe recipe);

    boolean existsByMemberIdAndRecipeId(Long memberId, Long RecipeId);

    Bookmark findByMemberIdAndRecipeId(Long memberId, Long recipeId);
}
