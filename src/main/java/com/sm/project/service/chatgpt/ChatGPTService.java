package com.sm.project.service.chatgpt;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.ChatGPTHandler;
import com.sm.project.apiPayload.exception.handler.FoodHandler;
import com.sm.project.repository.food.FoodGPTDto;
import com.sm.project.repository.food.FoodRepository;
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
    private final FoodRepository foodRepository;

    /**
     * ChatGPT의 응답을 생성하는 메서드입니다.
     *
     * @param request 사용자의 질문을 담고 있는 요청 객체
     * @return ChatGPT의 응답 객체
     */
    public Object prompt(String request) {
        openAiService = new OpenAiService(gptKey);

        // 사용자 메시지를 생성하고 목록에 추가
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.USER.value(), request);
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

    /**
     * ChatGPT에 질문할 내용을 생성하는 메서드입니다.
     *
     * @param memberId
     * @return ChatGPT에 질문할 내용을 담은 객체
     */
    public String createRequest(Long memberId) {
        String request;
        String foodList = "";

        List<FoodGPTDto> foodGPTDtoList = foodRepository.findFoodGptDto(memberId);
        if (foodGPTDtoList.isEmpty()) { //음식 조회 결과가 없는 경우
            throw new ChatGPTHandler(ErrorStatus.FOOD_EMPTY);
        }

        for (int i = 0; i < foodGPTDtoList.size(); i++) {
            if(i >= 100) break;
            foodList += foodGPTDtoList.get(i).toString() + ", ";
        }

        request = foodList + "들 중에서 최대한 많이 선택해서 그 재료들이 들어간 요리의 레시피를 단계별로 설명해주고, 그 전에 필요한 재료들을 말해줘.";
        System.out.println(request);
        return request;
    }
}
