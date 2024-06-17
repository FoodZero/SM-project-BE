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

@Service
@AllArgsConstructor
@Transactional
public class RecipeService {

    private final RecipeElasticRepository recipeDocumentRepository;


    // recommendCount가 높은 순으로 페이징된 레시피를 조회하는 메서드
    public Page<RecipeDocument> findTopRecipes(int lastIndex, int limit) {
        Pageable pageable = PageRequest.of(lastIndex / limit, limit, Sort.by(Sort.Direction.DESC, "recommendCount"));
        return recipeDocumentRepository.findAllByOrderByRecommendCountDesc(pageable);
    }

    public Page<RecipeDocument> searchByIngredient(String ingredient, int lastIndex, int limit) {
        Pageable pageable = PageRequest.of(lastIndex / limit, limit, Sort.by(Sort.Direction.DESC, "recommendCount"));
        return recipeDocumentRepository.findByIngredientContainingOrderByRecommendCountDesc(ingredient, pageable);
    }
}
