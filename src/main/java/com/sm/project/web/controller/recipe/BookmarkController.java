package com.sm.project.web.controller.recipe;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.domain.member.Member;
import com.sm.project.service.UtilService;
import com.sm.project.service.recipe.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Bookmark", description = "Bookmark 관련 API")
@RequestMapping("/api/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UtilService utilService;

    /**
     * 레시피에 북마크하는 api입니다.
     * @param auth
     * @param recipeId
     * @return
     */
    @PostMapping("/recipe/{recipeId}")
    @Operation(summary = "레시피 북마크 저장 API", description = "레시피 상세보기 화면에서 북마크를 눌렀을 때 저장하는 api입니다.(gpt 레시피 화면에서도 사용) 북마크할 레시피 아이디를 입력하세요.")
    public ResponseDTO<?> bookmarkRecipe(Authentication auth, @PathVariable(name = "recipeId")Long recipeId) {

        Member member = utilService.getAuthenticatedMember(auth);
        bookmarkService.saveBookmark(member.getId(), recipeId);

        return ResponseDTO.of(SuccessStatus._OK, "북마크 저장 성공");
    }

    /**
     * 레시피에 북마크를 해제하는 api입니다.
     * @param auth
     * @param recipeId
     * @return
     */
    @DeleteMapping("/recipe/{recipeId}")
    @Operation(summary = "레시피 북마크 해제 API", description = "레시피 상세보기 화면에서 북마크를 눌렀을 때 해제하는 api입니다.(gpt 레시피 화면에서도 사용) 북마크된 레시피 아이디를 입력하세요.")
    public ResponseDTO<?> unbookmarkRecipe(Authentication auth, @PathVariable(name = "recipeId")Long recipeId) {

        Member member = utilService.getAuthenticatedMember(auth);
        bookmarkService.deleteBookmark(member.getId(), recipeId);

        return ResponseDTO.of(SuccessStatus._OK, "북마크 해제 성공");
    }
}
