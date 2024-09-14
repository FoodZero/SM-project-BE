package com.sm.project.web.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RecipeResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeDetailDto { //레시피 상세 조회에서 보이는 정보
        private Long recipeId;
        private String recipeName;
        private String ingredient;
        private String description;
        private boolean isBookmark;  //사용자의 북마크 여부
        private boolean isRecommend; //사용자의 추천 여부
        private Long recommendCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkedRecipeListDto {

        private List<BookmarkedRecipeDto> bookmarkedRecipeDtoList;
        boolean isLast; //마지막 페이지인지 여부
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkedRecipeDto {

        private Long recipeId;
        private String recipeName;
        private String ingredient;
        private Long recommendCount;
    }
}
