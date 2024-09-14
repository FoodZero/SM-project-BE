package com.sm.project.converter.food;

import com.sm.project.domain.community.Comment;
import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.member.Member;
import com.sm.project.web.dto.community.CommentResponseDTO;
import com.sm.project.web.dto.recipe.RecipeResponseDTO;
import org.springframework.data.domain.Slice;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipeConverter {

    public static RecipeResponseDTO.BookmarkedRecipeListDto toBookmarkedRecipeListDto(Slice<Recipe> recipeList) {

        List<RecipeResponseDTO.BookmarkedRecipeDto> result = recipeList.stream()
                .map(recipe -> RecipeResponseDTO.BookmarkedRecipeDto.builder()
                        .recipeId(recipe.getId())
                        .recipeName(recipe.getName())
                        .ingredient(recipe.getIngredient())
                        .recommendCount(recipe.getRecommendCount())
                        .build()).collect(Collectors.toList());

        return RecipeResponseDTO.BookmarkedRecipeListDto.builder()
                .bookmarkedRecipeDtoList(result)
                .isLast(recipeList.isLast())
                .build();
    }
}
