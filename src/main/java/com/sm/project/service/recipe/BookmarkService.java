package com.sm.project.service.recipe;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.apiPayload.exception.handler.RecipeHandler;
import com.sm.project.domain.food.Bookmark;
import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.food.BookmarkRepository;
import com.sm.project.repository.food.RecipeRepository;
import com.sm.project.service.member.MemberQueryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class BookmarkService {

    private final MemberQueryService memberQueryService;
    private final RecipeRepository recipeRepository;
    private final BookmarkRepository bookmarkRepository;

    public void saveBookmark(Long memberId, Long recipeId) {
        Member member = memberQueryService.findMemberById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeHandler(ErrorStatus.RECIPE_NOT_FOUND));

        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .recipe(recipe)
                .build();
        bookmarkRepository.save(bookmark);
    }

    public void deleteBookmark(Long memberId, Long recipeId) {
        Bookmark bookmark = bookmarkRepository.findByMemberIdAndRecipeId(memberId, recipeId);
        bookmarkRepository.delete(bookmark);
    }
}
