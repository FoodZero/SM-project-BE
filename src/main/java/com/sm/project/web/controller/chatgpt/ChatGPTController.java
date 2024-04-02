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

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "ChatGPT", description = "ChatGPT 관련 API")
@RequestMapping("/api/gpt")
public class ChatGPTController {
    private final ChatGPTService chatGPTService;

    @PostMapping("/prompt")
    @Operation(summary = "ChatGPT 대화 api", description = "ChatGPT에게 레시피를 물어보는 api입니다. prompt에 입력해주세요." )
    public ResponseDTO<?> prompt(@RequestBody @Valid ChatGPTRequestDTO.PromptDTO request) {
        return ResponseDTO.of(SuccessStatus._OK, chatGPTService.prompt(request));
    }
}
