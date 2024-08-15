package com.sm.project.web.controller.recipe;

import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.service.recipe.BookmarkService;
import com.sm.project.web.dto.recipe.RecipeResponseDTO;
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
import org.springframework.web.bind.annotation.*;

/**
 * RecipeController는 레시피 관련 API 요청을 처리하는 컨트롤러 클래스입니다.
 * 레시피 조회 및 검색 기능을 제공합니다.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Recipe", description = "Recipe 관련 API")
@RequestMapping("/api/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * 레시피를 추천순으로 5개씩 조회하는 API입니다.
     * 
     * @param auth 현재 인증된 사용자 정보
     * @param lastIndex 마지막 인덱스, 처음 조회 시 0을 사용하며, 이후 스크롤 시 마지막 인덱스를 입력합니다.
     * @return 추천순으로 정렬된 레시피 목록을 포함한 응답
     */
    @GetMapping
    @Operation(summary = "레시피 조회 API", description = "레시피 추천순으로 5개씩 조회 가능한 api")
    @Parameter(name = "lastIndex", description = "lastIndex 첫 조회는 0이고 스크롤 내릴때마다 마지막 index 입력하시면 됩니다. 맨 처음인 경우 Null")
    public ResponseDTO<Page<RecipeDocument>> getRecipe(Authentication auth,
                                                       @RequestParam(value = "lastIndex", required = false, defaultValue = "0") int lastIndex){

        return ResponseDTO.onSuccess(recipeService.findTopRecipes(lastIndex,5));
    }

    /**
     * 특정 재료를 기반으로 레시피를 추천순으로 5개씩 조회하는 API입니다.
     * 
     * @param auth 현재 인증된 사용자 정보
     * @param ingredient 검색할 음식 재료
     * @param lastIndex 마지막 인덱스, 처음 조회 시 0을 사용하며, 이후 스크롤 시 마지막 인덱스를 입력합니다.
     * @return 특정 재료를 포함한 추천순으로 정렬된 레시피 목록을 포함한 응답
     */
    @GetMapping("/ingredient")
    @Operation(summary = "레시피 조회 API", description = "레시피 추천순으로 5개씩 조회 가능한 api")
    @Parameter(name = "lastIndex", description = "lastIndex 첫 조회는 0이고 스크롤 내릴때마다 마지막 index 입력하시면 됩니다. 맨 처음인 경우 Null")
    @Parameter(name = "ingredient", description = "검색창에서 입력받은 값(음식 재료) 넣어주면 됩니다.")
    public ResponseDTO<Page<RecipeDocument>> getRecipeWithIngredient(Authentication auth,
                                                                     @RequestParam(value= "ingredient") String ingredient,
                                                       @RequestParam(value = "lastIndex", required = false, defaultValue = "0") int lastIndex){

        return ResponseDTO.onSuccess(recipeService.searchByIngredient(ingredient, lastIndex, 5));
    }

    @GetMapping("/{recipeId}")
    @Operation(summary = "레시피 상세 조회 API", description = "레시피 목록에서 레시피를 눌렀을 때 레시피 상세를 조회하는 api입니다. 상세 조회할 레시피 아이디를 입력하세요.")
    public ResponseDTO<?> getRecipeDetail(Authentication auth, @PathVariable(name = "recipeId")Long recipeId) {
        Long memberId = Long.valueOf(auth.getName().toString());
        RecipeResponseDTO.RecipeDetailDto recipeDetailDto = recipeService.findRecipe(memberId, recipeId);
        return ResponseDTO.of(SuccessStatus._OK, recipeDetailDto);
    }
}
