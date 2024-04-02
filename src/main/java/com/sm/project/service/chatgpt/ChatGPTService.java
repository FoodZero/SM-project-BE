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

@Service
@RequiredArgsConstructor
public class ChatGPTService {
    private OpenAiService openAiService;
    @Value("${GPT_KEY}")
    private String gptKey;
    private static final String MODEL = "gpt-3.5-turbo";

    public Object prompt(ChatGPTRequestDTO.PromptDTO request) {
        openAiService = new OpenAiService(gptKey);

        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.USER.value(), request.getPrompt());
        messages.add(systemMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL) //지피티 모델 지정
                .messages(messages)
                .n(1) //지피티 응답을 몇 개 생성할 건지
                .build();

        return openAiService.createChatCompletion(chatCompletionRequest).getChoices();
    }
}
