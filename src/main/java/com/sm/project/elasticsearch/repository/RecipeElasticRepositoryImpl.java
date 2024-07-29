package com.sm.project.elasticsearch.repository;

import com.sm.project.elasticsearch.RecipeDocument;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class RecipeElasticRepositoryImpl{

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    public RecipeElasticRepositoryImpl(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    public Page<RecipeDocument> findByIngredientContainingOrderByRecommendCountDesc(String ingredient, Pageable pageable) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("ingredient", ingredient));

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(pageable)
                .withSort(SortBuilders.fieldSort("recommendCount").order(SortOrder.DESC))
                .build();

        SearchHits<RecipeDocument> searchHits = elasticsearchRestTemplate.search(searchQuery, RecipeDocument.class);

        return new PageImpl<>(searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList()), pageable, searchHits.getTotalHits());
    }

    // 다른 메서드도 필요에 따라 구현합니다.
}
