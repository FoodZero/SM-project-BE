package com.sm.project.converter.chatgpt;

import com.sm.project.web.dto.chatgpt.ChatGPTResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class ChatGPTConverter {

    public static List<ChatGPTResponseDTO.RecipeResultDTO> toRecipeResultDto(List<String> nameList, List<String> ingredientList, List<String> descriptionList) {
        List<ChatGPTResponseDTO.RecipeResultDTO> recipeResultDTOList = new ArrayList<>();

        for (int i = 0; i < nameList.size(); i++) {
            recipeResultDTOList.add(
                    ChatGPTResponseDTO.RecipeResultDTO.builder()
                            .recipeName(nameList.get(i))
                            .ingredient(ingredientList.get(i))
                            .description(descriptionList.get(i))
                            .build()
            );
        }

        return recipeResultDTOList;
    }
}
