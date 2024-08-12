package com.sm.project.converter.chatgpt;

import com.sm.project.web.dto.chatgpt.ChatGPTResponseDTO;

import java.util.List;


public class ChatGPTConverter {

    public static ChatGPTResponseDTO.RecipeResultDTO toRecipeResultDto(String name, String ingredient, String description) {

        return ChatGPTResponseDTO.RecipeResultDTO.builder()
                .recipeName(name)
                .ingredient(ingredient)
                .description(description)
                .build();
    }

    public static ChatGPTResponseDTO.RecipeListResultDto toRecipeListResultDto(int recipeCount, List<ChatGPTResponseDTO.RecipeDto> recipeDtoList) {

        return ChatGPTResponseDTO.RecipeListResultDto.builder()
                .recipeCount(recipeCount)
                .recipeDtoList(recipeDtoList)
                .build();
    }
}
