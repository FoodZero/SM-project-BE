package com.sm.project.web.controller.recipe;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.domain.member.Member;
import com.sm.project.service.UtilService;
import com.sm.project.service.recipe.RecommendService;
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
@Tag(name = "Recommend", description = "Recommend 관련 API")
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;
    private final UtilService utilService;

    /**
     * 레시피에 추천을 누르는 api입니다.
     * @param auth
     * @param recipeId
     * @return
     */
    @PostMapping("/recipe/{recipeId}")
    @Operation(summary = "레시피 추천 API", description = "레시피 상세 화면에서 추천을 눌렀을 때 저장하는 API입니다.(gpt레시피 화면도 사용) 추천할 레시피의 아이디를 입력하세요.")
    public ResponseDTO<?> recommendRecipe(Authentication auth, @PathVariable(name = "recipeId") Long recipeId) {

        Member member = utilService.getAuthenticatedMember(auth);
        recommendService.saveRecommend(member.getId(), recipeId);

        return ResponseDTO.of(SuccessStatus._OK, "레시피 추천 저장 성공");
    }

    /**
     * 레시피에 추천을 해제하는 api입니다.
     * @param auth
     * @param recipeId
     * @return
     */
    @DeleteMapping("/recipe/{recipeId}")
    @Operation(summary = "레시피 추천 해제 API", description = "레시피 상세 화면에서 추천을 다시 눌렀을 때 해제하는 API입니다.(gpt레시피 화면도 사용) 추천 해제할 레시피의 아이디를 입력하세요. ")
    public ResponseDTO<?> unrecommendRecipe(Authentication auth, @PathVariable(name = "recipeId") Long recipeId) {

        Member member = utilService.getAuthenticatedMember(auth);
        recommendService.deleteRecommend(member.getId(), recipeId);

        return ResponseDTO.of(SuccessStatus._OK, "레시피 추천 해제 성공");
    }
}
