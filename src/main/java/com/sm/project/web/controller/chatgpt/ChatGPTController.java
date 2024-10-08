package com.sm.project.web.controller.chatgpt;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.domain.member.Member;
import com.sm.project.service.UtilService;
import com.sm.project.service.chatgpt.ChatGPTService;
import com.sm.project.web.dto.chatgpt.ChatGPTResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * ChatGPTController는 ChatGPT 관련 API 요청을 처리하는 컨트롤러 클래스입니다.
 * ChatGPT에게 질문을 보내고 응답을 받는 기능을 제공합니다.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "ChatGPT", description = "ChatGPT 관련 API")
@RequestMapping("/api/gpt")
public class ChatGPTController {

    private final ChatGPTService chatGPTService;
    private final UtilService utilService;

    /**
     * ChatGPT 질문 API
     * <p>
     * //* @param ChatGPT에게 보낼 질문을 포함한 요청 데이터
     *
     * @return ChatGPT의 응답을 포함한 응답 DTO
     */
    @GetMapping("/recipe")
    @Operation(summary = "ChatGPT 레시피 추천 API", description = "ChatGPT 버튼을 눌렀을 때 레시피를 추천받는 API입니다.(레시피 id, 레시피 이름, 재료, 설명)")
    public ResponseDTO<?> getGptRecipe(Authentication authentication) {

        Member member = utilService.getAuthenticatedMember(authentication);
        ChatGPTResponseDTO.RecipeResultDTO result = chatGPTService.getGptRecipe(member.getId());

        return ResponseDTO.of(SuccessStatus._OK, result);
    }

    /**
     * ChatGPT 레시피 목록 조회 API
     * @param authentication
     * @return
     */
    @GetMapping("/recipe-list")
    @Operation(summary = "ChatGPT 레시피 목록 조회 API", description = "ChatGPT로 추천받은 레시피들의 목록을 조회하는 API입니다.")
    public ResponseDTO<?> getGptRecipeList(Authentication authentication) {

        Member member = utilService.getAuthenticatedMember(authentication);
        ChatGPTResponseDTO.RecipeListResultDto result = chatGPTService.getGptRecipeList(member.getId());

        return ResponseDTO.of(SuccessStatus._OK, result);
    }

    /**
     * ChatGPT 레시피 초기화 API
     * @param authentication
     * @return
     */
    @DeleteMapping("/recipe")
    @Operation(summary = "ChatGPT 레시피 초기화 API", description = "ChatGPT 레시피 목록에서 뒤로가기 버튼을 눌렀을 때 초기화에 필요한 API입니다.")
    public ResponseDTO<?> deleteGptRecipe(Authentication authentication) {

        Member member = utilService.getAuthenticatedMember(authentication);
        chatGPTService.deleteGptRecipe(member.getId());

        return ResponseDTO.of(SuccessStatus._OK, "ChatGPT 레시피 초기화 성공");
    }
}
