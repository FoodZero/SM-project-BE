package com.sm.project.web.controller.chatgpt;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.service.chatgpt.ChatGPTService;
import com.sm.project.web.dto.chatgpt.ChatGPTRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
     * @param request ChatGPT에게 보낼 질문을 포함한 요청 데이터
     * @return ChatGPT의 응답을 포함한 응답 DTO
     */
    @PostMapping("/prompt")
    @Operation(summary = "ChatGPT 질문 API", description = "ChatGPT에게 레시피를 물어보는 API입니다. prompt에 질문을 입력해주세요.")
    public ResponseDTO<?> prompt(@RequestBody @Valid ChatGPTRequestDTO.PromptDTO request) {
        return ResponseDTO.of(SuccessStatus._OK, chatGPTService.prompt(request));
    }
}
