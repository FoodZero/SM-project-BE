package com.sm.project.service.recipe;

import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.apiPayload.exception.handler.RecipeHandler;
import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.member.Member;
import com.sm.project.elasticsearch.RecipeDocument;
import com.sm.project.elasticsearch.repository.RecipeElasticRepository;
import com.sm.project.elasticsearch.repository.RecipeElasticRepositoryImpl;
import com.sm.project.repository.food.BookmarkRepository;
import com.sm.project.repository.food.RecipeRepository;
import com.sm.project.repository.food.RecommendRepository;
import com.sm.project.repository.member.MemberRepository;
import com.sm.project.web.dto.recipe.RecipeResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeElasticRepository recipeDocumentRepository;
    @Mock
    private RecipeElasticRepositoryImpl recipeElasticRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private RecommendRepository recommendRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RecipeService recipeService;

    private List<RecipeDocument> recipes;
    private Member testMember;


    @BeforeEach
    public void setUp() {

        testMember = Member.builder()
                .id(1L)
                .nickname("test")
                .email("test@gmail.com")
                .build();

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
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        Page<RecipeDocument> page = new PageImpl<>(recipes, pageable, recipes.size());
        when(recipeDocumentRepository.findAllByOrderByRecommendCountDesc(any(Pageable.class)))
                .thenReturn(page);

        // When
        Page<RecipeDocument> result = recipeService.findTopRecipes(0, 2);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Recipe Name1");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Recipe Name2");
        assertThat(result.getContent().get(2).getName()).isEqualTo("Recipe Name3");
    }

    @Test
    @DisplayName("특정 재료로 찾는 레시피 조회 테스트")
    void searchByIngredient() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        String ingredient = "Ingredient List1";
        List<RecipeDocument> filteredRecipes = Arrays.asList(recipes.get(0));
        Page<RecipeDocument> page = new PageImpl<>(filteredRecipes, pageable, filteredRecipes.size());
        when(recipeElasticRepository.findByIngredientContainingOrderByRecommendCountDesc(anyString(), any(Pageable.class)))
                .thenReturn(page);

        // When
        Page<RecipeDocument> result = recipeService.searchByIngredient(ingredient, 0, 2);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIngredient()).contains(ingredient);
    }

    @Test
    @DisplayName("레시피 상세 정보 조회 테스트")
    void findRecipe() {
        // Given
        Long recipeId = 1L;
        Recipe recipe = Recipe.builder()
                .id(recipeId)
                .name("Recipe Name")
                .ingredient("Ingredients")
                .description("Recipe Description")
                .recommendCount(5L)
                .build();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(bookmarkRepository.existsByMemberIdAndRecipeId(testMember.getId(), recipeId)).thenReturn(true);
        when(recommendRepository.existsByMemberIdAndRecipeId(testMember.getId(), recipeId)).thenReturn(false);

        // When
        RecipeResponseDTO.RecipeDetailDto result = recipeService.findRecipe(testMember.getId(), recipeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecipeId()).isEqualTo(recipeId);
        assertThat(result.getRecipeName()).isEqualTo(recipe.getName());
        assertThat(result.getIngredient()).isEqualTo(recipe.getIngredient());
        assertThat(result.getDescription()).isEqualTo(recipe.getDescription());
        assertThat(result.getRecommendCount()).isEqualTo(recipe.getRecommendCount());
    }

    @Test
    @DisplayName("레시피 상세 정보 조회 예외 테스트")
    void findRecipe_RecipeNotFound() {
        // Given
        Long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecipeHandler.class, () -> recipeService.findRecipe(testMember.getId(), recipeId));
    }

    @Test
    @DisplayName("저장된 레시피 목록 조회 테스트")
    void findBookmarkedRecipeList() {
        // Given
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(String.valueOf(testMember.getId()));

        Recipe recipe1 = Recipe.builder()
                .id(1L)
                .name("Recipe1")
                .ingredient("Ingredient1")
                .recommendCount(5L)
                .build();

        Recipe recipe2 = Recipe.builder()
                .id(2L)
                .name("Recipe2")
                .ingredient("Ingredient2")
                .recommendCount(10L)
                .build();

        List<Recipe> recipeList = Arrays.asList(recipe1, recipe2);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Slice<Recipe> recipeSlice = new SliceImpl<>(recipeList, pageRequest, true);

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(recipeRepository.findByMemberIdAndBookmark(testMember, pageRequest)).thenReturn(recipeSlice);

        // When
        RecipeResponseDTO.BookmarkedRecipeListDto result = recipeService.findBookmarkedRecipeList(auth, 0);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBookmarkedRecipeDtoList()).hasSize(2);


        RecipeResponseDTO.BookmarkedRecipeDto dto1 = result.getBookmarkedRecipeDtoList().get(0);
        assertThat(dto1.getRecipeId()).isEqualTo(recipe1.getId());

        RecipeResponseDTO.BookmarkedRecipeDto dto2 = result.getBookmarkedRecipeDtoList().get(1);
        assertThat(dto2.getRecipeId()).isEqualTo(recipe2.getId());
    }

    @Test
    @DisplayName("저장된 레시피 목록 조회 예외 테스트")
    void findBookmarkedRecipeList_MemberNotFound() {
        // Given
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(String.valueOf(testMember.getId()));
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MemberHandler.class, () -> recipeService.findBookmarkedRecipeList(auth, 0));
    }
}
