package com.sm.project.service.recipe;


import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.apiPayload.exception.handler.RecipeHandler;
import com.sm.project.apiPayload.exception.handler.RecommendHandler;
import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.food.Recommend;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.food.RecipeRepository;
import com.sm.project.repository.food.RecommendRepository;
import com.sm.project.service.member.MemberQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendServiceTest {

    @Mock
    private RecommendRepository recommendRepository;
    @Mock
    private MemberQueryService memberQueryService;
    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecommendService recommendService;

    private Member testMember;
    private Recipe testRecipe;

    private Recommend recommend;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .nickname("Test Member")
                .email("test@gmail.com")
                .build();

        testRecipe = Recipe.builder()
                .id(1L)
                .name("Test Recipe")
                .recommendCount(0L)
                .build();

        recommend = Recommend.builder()
                .id(1L)
                .member(testMember)
                .recipe(testRecipe)
                .build();
    }

    @Test
    @DisplayName("레시피 추천 저장 성공 테스트")
    void saveRecommend_Success() {
        // Given
        // 테스트에 사용할 회원과 레시피 객체를 설정합니다.
        when(memberQueryService.findMemberById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(recipeRepository.findById(testRecipe.getId())).thenReturn(Optional.of(testRecipe));
        when(recommendRepository.existsByMemberIdAndRecipeId(testMember.getId(), testRecipe.getId())).thenReturn(false);
        // When
        recommendService.saveRecommend(testMember.getId(), testRecipe.getId());

        // Then
        verify(recommendRepository, times(1)).save(any(Recommend.class));
        assertThat(testRecipe.getRecommendCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("레시피 추천 저장 실패 테스트 - 이미 추천된 경우")
    void saveRecommend_Failure_RecommendExists() {
        // Given
        when(memberQueryService.findMemberById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(recipeRepository.findById(testRecipe.getId())).thenReturn(Optional.of(testRecipe));
        when(recommendRepository.existsByMemberIdAndRecipeId(testMember.getId(), testRecipe.getId())).thenReturn(true);

        // When & Then
        assertThrows(RecipeHandler.class, () -> recommendService.saveRecommend(testMember.getId(), testRecipe.getId()));
    }

    @Test
    @DisplayName("레시피 추천 저장 실패 테스트 - 회원 찾을 수 없음")
    void saveRecommend_Failure_MemberNotFound() {
        // Given
        when(memberQueryService.findMemberById(testMember.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MemberHandler.class, () -> recommendService.saveRecommend(testMember.getId(), testRecipe.getId()));
    }

    @Test
    @DisplayName("레시피 추천 저장 실패 테스트 - 레시피 찾을 수 없음")
    void saveRecommend_Failure_RecipeNotFound() {
        // Given
        when(memberQueryService.findMemberById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(recipeRepository.findById(testRecipe.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecipeHandler.class, () -> recommendService.saveRecommend(testMember.getId(), testRecipe.getId()));
    }

    @Test
    @DisplayName("레시피 추천 해제 성공 테스트")
    void deleteRecommend_Success() {
        // Given
        when(recommendRepository.findByMemberIdAndRecipeId(testMember.getId(), testRecipe.getId())).thenReturn(recommend);
        when(recipeRepository.findById(testRecipe.getId())).thenReturn(Optional.of(testRecipe));

        // When
        recommendService.deleteRecommend(testMember.getId(), testRecipe.getId());

        // Then
        verify(recommendRepository, times(1)).delete(recommend);
        assertThat(testRecipe.getRecommendCount()).isEqualTo(-1);
    }

    @Test
    @DisplayName("레시피 추천 해제 실패 테스트 - 추천 찾을 수 없음")
    void deleteRecommend_Failure_RecommendNotFound() {
        // Given
        when(recommendRepository.findByMemberIdAndRecipeId(testMember.getId(), testRecipe.getId())).thenReturn(null);

        // When & Then
        assertThrows(RecommendHandler.class, () -> recommendService.deleteRecommend(testMember.getId(), testRecipe.getId()));
    }

    @Test
    @DisplayName("레시피 추천 해제 실패 테스트 - 레시피 찾을 수 없음")
    void deleteRecommend_Failure_RecipeNotFound() {
        // Given
        when(recommendRepository.findByMemberIdAndRecipeId(testMember.getId(), testRecipe.getId())).thenReturn(recommend);
        when(recipeRepository.findById(testRecipe.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecipeHandler.class, () -> recommendService.deleteRecommend(testMember.getId(), testRecipe.getId()));
    }
}
