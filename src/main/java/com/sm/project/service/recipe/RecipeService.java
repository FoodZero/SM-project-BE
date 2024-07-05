package com.sm.project.service.recipe;

import com.sm.project.elasticsearch.RecipeDocument;
import com.sm.project.elasticsearch.repository.RecipeElasticRepository;
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
        return recipeDocumentRepository.findByIngredientContainingOrderByRecommendCountDesc(ingredient, pageable);
    }
}
