package com.sm.project.web.dto.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatGPTResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromptResultDTO {
        private String response; //지피티 대답.
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeResultDTO {

        private Long recipeId;
        private String recipeName;
        private String ingredient;
        private String description;

        public String toString() {
            return recipeName + " / " + ingredient + " / " + description;
        }

        public void setRecipeId(Long id) {
            this.recipeId = id;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeListResultDto {

        private int recipeCount;
        private List<RecipeDto> recipeDtoList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeDto {
        private Long recipeId;
        private String recipeName;
        private String ingredient;
        private Long recommendCount;
    }

}
