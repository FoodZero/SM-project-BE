package com.sm.project.web.controller.recipe;

import org.springframework.data.domain.Page;
import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.elasticsearch.RecipeDocument;
import com.sm.project.service.recipe.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Recipe", description = "Recipe 관련 API")
@RequestMapping("/api/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    @Operation(summary = "레시피 조회 API", description = "레시피 추천순으로 5개씩 조회 가능한 api")
    @Parameter(name = "lastIndex", description = "lastIndex 첫 조회는 0이고 스크롤 내릴때마다 마지막 index 입력하시면 됩니다. 맨 처음인 경우 Null")
    public ResponseDTO<Page<RecipeDocument>> getRecipe(Authentication auth,
                                                       @RequestParam(value = "lastIndex", required = false, defaultValue = "0") int lastIndex){

        return ResponseDTO.onSuccess(recipeService.findTopRecipes(lastIndex,5));
    }

    @GetMapping("/ingredient")
    @Operation(summary = "레시피 조회 API", description = "레시피 추천순으로 5개씩 조회 가능한 api")
    @Parameter(name = "lastIndex", description = "lastIndex 첫 조회는 0이고 스크롤 내릴때마다 마지막 index 입력하시면 됩니다. 맨 처음인 경우 Null")
    @Parameter(name = "ingredient", description = "검색창에서 입력받은 값(음식 재료) 넣어주면 됩니다.")
    public ResponseDTO<Page<RecipeDocument>> getRecipeWithIngredient(Authentication auth,
                                                                     @RequestParam(value= "ingredient") String ingredient,
                                                       @RequestParam(value = "lastIndex", required = false, defaultValue = "0") int lastIndex){

        return ResponseDTO.onSuccess(recipeService.searchByIngredient(ingredient,lastIndex,5));
    }
}
