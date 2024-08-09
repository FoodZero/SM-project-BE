package com.sm.project.service.recipe;

import com.sm.project.elasticsearch.RecipeDocument;
import com.sm.project.elasticsearch.repository.RecipeElasticRepository;
import com.sm.project.elasticsearch.repository.RecipeElasticRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeElasticRepository recipeDocumentRepository;

    @Mock
    private RecipeElasticRepositoryImpl recipeElasticRepository;

    @InjectMocks
    private RecipeService recipeService;

    private List<RecipeDocument> recipes;

    @BeforeEach
    public void setUp() {

        RecipeDocument recipe1 = RecipeDocument.builder()
                .id("1")
                .name("Recipe Name1")
                .ingredient("Ingredient List1")
                .build();
        RecipeDocument recipe2 =RecipeDocument.builder()
                .id("2")
                .name("Recipe Name2")
                .ingredient("Ingredient List2")
                .build();
        RecipeDocument recipe3 = RecipeDocument.builder()
                .id("3")
                .name("Recipe Name3")
                .ingredient("Ingredient List3")
                .build();
        recipes = Arrays.asList(recipe1, recipe2, recipe3);
    }

    @Test
    @DisplayName("레시피 조회 테스트")
    void findTopRecipes() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<RecipeDocument> page = new PageImpl<>(recipes, pageable, recipes.size());

        when(recipeDocumentRepository.findAllByOrderByRecommendCountDesc(any(Pageable.class)))
                .thenReturn(page);

        Page<RecipeDocument> result = recipeService.findTopRecipes(0, 2);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Recipe Name1");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Recipe Name2");
        assertThat(result.getContent().get(2).getName()).isEqualTo("Recipe Name3");

    }

    @Test
    @DisplayName("특정 재료로 찾는 레시피 조회 테스트 ")
    void searchByIngredient() {
        Pageable pageable = PageRequest.of(0, 2);
        String ingredient = "Ingredient List1";
        List<RecipeDocument> filteredRecipes = Arrays.asList(recipes.get(0));
        Page<RecipeDocument> page = new PageImpl<>(filteredRecipes, pageable, filteredRecipes.size());

        when(recipeElasticRepository.findByIngredientContainingOrderByRecommendCountDesc(anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<RecipeDocument> result = recipeService.searchByIngredient(ingredient, 0, 2);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIngredient()).contains(ingredient);

    }
}
