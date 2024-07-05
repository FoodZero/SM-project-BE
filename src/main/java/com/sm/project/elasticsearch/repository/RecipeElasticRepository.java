package com.sm.project.elasticsearch.repository;

import com.sm.project.elasticsearch.RecipeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("elasticSearchRepository")
public interface RecipeElasticRepository extends ElasticsearchRepository<RecipeDocument, String> {
    List<RecipeDocument> findByName(String name);

    // recommendCount가 높은 순으로 레시피를 조회하는 메서드
    Page<RecipeDocument> findAllByOrderByRecommendCountDesc(Pageable pageable);
    //재료를 이용해서 검색하는 메서드
    Page<RecipeDocument> findByIngredientContainingOrderByRecommendCountDesc(String ingredient, Pageable pageable);

}
