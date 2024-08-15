package com.sm.project.repository.food;

import com.sm.project.domain.food.Recipe;
import com.sm.project.repository.chatgpt.RecipeNameDto;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    boolean existsByNameAndIdNot(String name, Long recipeId);

    @Query("select new com.sm.project.repository.chatgpt.RecipeNameDto(r.name) from Recipe r " +
            "join Member m on r.member = m " +
            "where m.id = :memberId")
    List<RecipeNameDto> findRecipeName(@Param("memberId") Long memberId);

    List<Recipe> findByMemberId(Long memberId);
}
