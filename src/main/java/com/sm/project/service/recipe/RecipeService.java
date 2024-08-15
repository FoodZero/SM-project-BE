package com.sm.project.service.recipe;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.RecipeHandler;
import com.sm.project.domain.food.Recipe;
import com.sm.project.elasticsearch.RecipeDocument;
import com.sm.project.elasticsearch.repository.RecipeElasticRepository;
import com.sm.project.elasticsearch.repository.RecipeElasticRepositoryImpl;
import com.sm.project.repository.food.BookmarkRepository;
import com.sm.project.repository.food.RecipeRepository;
import com.sm.project.repository.food.RecommendRepository;
import com.sm.project.web.dto.recipe.RecipeResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RecipeService는 레시피 관련 기능을 제공하는 서비스 클래스입니다.
 * 레시피 조회 및 검색 기능을 담당합니다.
 */
@Service
@AllArgsConstructor
@Transactional
public class RecipeService {

    private final RecipeElasticRepository recipeDocumentRepository;
    private final RecipeElasticRepositoryImpl recipeElasticRepository;
    private final RecipeRepository recipeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RecommendRepository recommendRepository;

    /**
     * 추천 수가 높은 순으로 페이징된 레시피를 조회하는 메서드입니다.
     * 
     * @param lastIndex 마지막 인덱스
     * @param limit 페이지당 레시피 수
     * @return 페이징된 레시피 목록
     */
    public Page<RecipeDocument> findTopRecipes(int lastIndex, int limit) {
        Pageable pageable = PageRequest.of(lastIndex / limit, limit, Sort.by(Sort.Direction.DESC, "recommendCount"));
        return recipeDocumentRepository.findAllByOrderByRecommendCountDesc(pageable);
    }

    /**
     * 특정 재료가 포함된 레시피를 추천 수가 높은 순으로 페이징하여 조회하는 메서드입니다.
     * 
     * @param ingredient 검색할 재료
     * @param lastIndex 마지막 인덱스
     * @param limit 페이지당 레시피 수
     * @return 페이징된 레시피 목록
     */
    public Page<RecipeDocument> searchByIngredient(String ingredient, int lastIndex, int limit) {
        Pageable pageable = PageRequest.of(lastIndex / limit, limit, Sort.by(Sort.Direction.DESC, "recommendCount"));
        return recipeElasticRepository.findByIngredientContainingOrderByRecommendCountDesc(ingredient, pageable);
    }

    /**
     * 레시피 상세 정보를 찾아서 반환하는 메소드입니다.
     * @param memberId
     * @param recipeId
     * @return
     */
    public RecipeResponseDTO.RecipeDetailDto findRecipe(Long memberId, Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeHandler(ErrorStatus.RECIPE_NOT_FOUND));

        return RecipeResponseDTO.RecipeDetailDto.builder()
                .recipeId(recipeId)
                .recipeName(recipe.getName())
                .ingredient(recipe.getIngredient())
                .description(recipe.getDescription())
                .isBookmark(bookmarkRepository.existsByMemberIdAndRecipeId(memberId, recipeId))
                .isRecommend(recommendRepository.existsByMemberIdAndRecipeId(memberId,recipeId))
                .recommendCount(recipe.getRecommendCount())
                .build();
    }
}
