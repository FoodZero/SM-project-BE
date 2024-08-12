package com.sm.project.converter.chatgpt;

import com.sm.project.web.dto.chatgpt.ChatGPTResponseDTO;


public class ChatGPTConverter {

    public static ChatGPTResponseDTO.RecipeResultDTO toRecipeResultDto(String name, String ingredient, String description) {

        return ChatGPTResponseDTO.RecipeResultDTO.builder()
                .recipeName(name)
                .ingredient(ingredient)
                .description(description)
                .build();
    }
}
