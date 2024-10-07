package com.sm.project.service.recipe;

import com.sm.project.apiPayload.exception.handler.BookmarkHandler;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.apiPayload.exception.handler.RecipeHandler;
import com.sm.project.domain.food.Bookmark;
import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.food.BookmarkRepository;
import com.sm.project.repository.food.RecipeRepository;
import com.sm.project.service.member.MemberQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Mock
    private MemberQueryService memberQueryService;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    private Member testMember;
    private Recipe testRecipe;

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
                .build();
    }

    @Test
    @DisplayName("북마크 저장 성공 테스트")
    void saveBookmark_Success() {
        // Given
        when(memberQueryService.findMemberById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(recipeRepository.findById(testRecipe.getId())).thenReturn(Optional.of(testRecipe));
        when(bookmarkRepository.existsByMemberIdAndRecipeId(testMember.getId(), testRecipe.getId())).thenReturn(false);

        // When
        bookmarkService.saveBookmark(testMember.getId(), testRecipe.getId());

        // Then
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("북마크 저장 실패 테스트 - 이미 존재하는 북마크")
    void saveBookmark_Failure_BookmarkExists() {
        // Given
        when(memberQueryService.findMemberById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(recipeRepository.findById(testRecipe.getId())).thenReturn(Optional.of(testRecipe));
        when(bookmarkRepository.existsByMemberIdAndRecipeId(testMember.getId(), testRecipe.getId())).thenReturn(true);

        // When & Then
        assertThrows(BookmarkHandler.class, () -> bookmarkService.saveBookmark(testMember.getId(), testRecipe.getId()));
    }

    @Test
    @DisplayName("북마크 저장 실패 테스트 - 회원 찾을 수 없음")
    void saveBookmark_Failure_MemberNotFound() {
        // Given
        when(memberQueryService.findMemberById(testMember.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MemberHandler.class, () -> bookmarkService.saveBookmark(testMember.getId(), testRecipe.getId()));
    }

    @Test
    @DisplayName("북마크 저장 실패 테스트 - 레시피 찾을 수 없음")
    void saveBookmark_Failure_RecipeNotFound() {
        // Given
        when(memberQueryService.findMemberById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(recipeRepository.findById(testRecipe.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecipeHandler.class, () -> bookmarkService.saveBookmark(testMember.getId(), testRecipe.getId()));
    }

    @Test
    @DisplayName("북마크 삭제 성공 테스트")
    void deleteBookmark_Success() {
        // Given
        Bookmark bookmark = Bookmark.builder()
                .id(1L)
                .member(testMember)
                .recipe(testRecipe)
                .build();

        when(bookmarkRepository.findByMemberIdAndRecipeId(testMember.getId(), testRecipe.getId())).thenReturn(bookmark);

        // When
        bookmarkService.deleteBookmark(testMember.getId(), testRecipe.getId());

        // Then
        verify(bookmarkRepository, times(1)).delete(bookmark);
    }

    @Test
    @DisplayName("북마크 삭제 실패 테스트 - 북마크 찾을 수 없음")
    void deleteBookmark_Failure_BookmarkNotFound() {
        // Given
        when(bookmarkRepository.findByMemberIdAndRecipeId(testMember.getId(), testRecipe.getId())).thenReturn(null);

        // When & Then
        assertThrows(BookmarkHandler.class, () -> bookmarkService.deleteBookmark(testMember.getId(), testRecipe.getId()));
    }
}
