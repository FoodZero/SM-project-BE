package com.sm.project.web.controller.chatgpt;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.service.chatgpt.ChatGPTService;
import com.sm.project.web.dto.chatgpt.ChatGPTRequestDTO;
import com.sm.project.web.dto.chatgpt.ChatGPTResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    /**
     * ChatGPT 질문 API
     * 
//     * @param ChatGPT에게 보낼 질문을 포함한 요청 데이터
     * @return ChatGPT의 응답을 포함한 응답 DTO
     */
    @GetMapping("/recipe")
    @Operation(summary = "ChatGPT 레시피 API", description = "ChatGPT를 이용해 레시피를 조회하는 API입니다.(레시피 이름, 재료, 설명)")
    public ResponseDTO<?> getGptRecipe(Authentication authentication) {
        Long memberId = Long.valueOf(authentication.getName().toString());
        List<ChatGPTResponseDTO.RecipeResultDTO> result = chatGPTService.getGptRecipe(memberId);
        return ResponseDTO.of(SuccessStatus._OK, result);
    }
}
