package com.sm.project.service.chatgpt;

import com.sm.project.web.dto.chatgpt.ChatGPTRequestDTO;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatGPTService는 ChatGPT 관련 기능을 제공하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private OpenAiService openAiService;

    @Value("${chatgpt.key}")
    private String gptKey;

    private static final String MODEL = "gpt-3.5-turbo";

    /**
     * 사용자의 질문에 대해 ChatGPT의 응답을 생성하는 메서드입니다.
     *
     * @param request 사용자의 질문을 담고 있는 요청 객체
     * @return ChatGPT의 응답 객체
     */
    public Object prompt(ChatGPTRequestDTO.PromptDTO request) {
        openAiService = new OpenAiService(gptKey);

        // 사용자 메시지를 생성하고 목록에 추가
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.USER.value(), request.getPrompt());
        messages.add(systemMessage);

        // ChatGPT 요청 객체 생성
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL) // GPT 모델 지정
                .messages(messages)
                .n(1) // GPT 응답 수 설정
                .build();

        // ChatGPT 응답 생성 및 반환
        return openAiService.createChatCompletion(chatCompletionRequest).getChoices();
    }
}
