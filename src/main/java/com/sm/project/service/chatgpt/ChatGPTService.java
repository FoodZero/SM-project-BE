package com.sm.project.service.chatgpt;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.FoodHandler;
import com.sm.project.converter.chatgpt.ChatGPTConverter;
import com.sm.project.domain.food.Recipe;
import com.sm.project.repository.food.FoodGPTDto;
import com.sm.project.repository.food.FoodRepository;
import com.sm.project.repository.food.RecipeRepository;
import com.sm.project.web.dto.chatgpt.ChatGPTResponseDTO;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.theokanning.openai.service.OpenAiService.*;

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
    private final RecipeRepository recipeRepository;

    /**
     * ChatGPT의 응답을 생성하는 메서드입니다.
     *
     * @param request 사용자의 질문을 담고 있는 요청 객체
     * @return ChatGPT의 응답 객체
     */
    public String prompt(String request) {
        openAiService = new OpenAiService(gptKey, Duration.ofSeconds(60));  //타임아웃 시간 설정(60초)

        // 사용자 메시지를 생성하고 목록에 추가
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.USER.value(), request);
        messages.add(systemMessage);

        // ChatGPT 요청 객체 생성
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL) // GPT 모델 지정
                .messages(messages)
                .n(1) // GPT 응답 수 설정
                .maxTokens(4096) //토큰 제한(최대로)
                .build();

        // ChatGPT 응답 생성 및 반환
        return openAiService.createChatCompletion(chatCompletionRequest).getChoices().toString();
    }

    /**
     * ChatGPT에 질문할 내용을 생성하는 메서드입니다.
     *
     * @param memberId
     * @return ChatGPT에 질문할 내용을 담은 객체
     */
    public String createRequest(Long memberId, int recipeCount) {
        String request;
        String foodList = "";

        List<FoodGPTDto> foodGPTDtoList = foodRepository.findFoodGptDto(memberId);
        if (foodGPTDtoList.isEmpty()) { //음식 조회 결과가 없는 경우
            throw new FoodHandler(ErrorStatus.FOOD_NOT_FOUND);
        }

        for (int i = 0; i < foodGPTDtoList.size(); i++) {
            if (i >= 50) break;
            foodList += foodGPTDtoList.get(i).toString() + ", ";
        }

        request = foodList + "들 중에 선택해서 그 재료들이 들어간 레시피 " + recipeCount +"개를 다음과 똑같은 형식으로 자세하게 말해줘. " +
                "그리고 주어진 재료 외에 더 필요한 재료가 있으면 재료목록에 추가하고, 줄바꿈 잘 지켜서 다음 형식이랑 완전 똑같이 말해.:\n" +
                "레시피 이름: [이름]\n\n" +
                "재료: [재료 1], [재료 2], ...\n\n" +
                "설명: " +
                "1. [1 단계]\n" +
                "2. [2 단계]\n" +
                "3. [3 단계]\n" +
                "..." +
                "\n\n\n(레시피가 끝날 때마다 엔터 3번 해. 이 괄호에 있는 말은 응답에 쓰지마.)";

        System.out.println(request);
        return request;
    }

    /**
     *
     * @param response
     * @return
     */
    public List<ChatGPTResponseDTO.RecipeResultDTO> parseResponse(String response) {
        String[] recipeList = response.split("\n\n\n");;

        if (recipeList.length <= 1) {
                recipeList = response.split("\n\n\n\n\n");
        }
        List<String> nameList = new ArrayList<>();
        List<String> ingredientList = new ArrayList<>();
        List<String> descriptionList = new ArrayList<>();

        int recipeListLen = recipeList.length;
        if (recipeList[recipeList.length - 1].contains("finishReason=stop")) { //만약 이게 레시피에 붙어서 나오면 마지막 레시피는 제외돼서 4개나 2개가 나올 때가 있다.
            recipeListLen--;
        }

        for (int i = 0; i < recipeListLen; i++) {
            String[] parts = recipeList[i].split("\n\n");

            if (parts.length != 3) { //gpt 응답이 형식을 지키지 않은 경우, break. 3은 항목의 수(레시피 이름, 재료, 설명)이다.
                break;
            }

            if (i == 0) {
                nameList.add(parts[0].replace("[ChatCompletionChoice(index=0, message=ChatMessage(role=assistant, content=레시피 이름: ", "").trim());
            } else {
                nameList.add(parts[0].replace("레시피 이름: ", "").trim());
            }
            ingredientList.add(parts[1].replace("재료: ","").trim());
            String description = parts[2].replace("설명: ", "").trim();
            description = description.replace("설명:", "").trim();
            descriptionList.add(description.replace("), finishReason=stop)]", "").trim());
        }

        if (nameList.size() != ingredientList.size() || nameList.size() != descriptionList.size()) {
            return null; //레시피가 잘렸거나 gpt가 형식을 안 지켜서 데이터들의 사이즈가 다를 경우, null 반환
        }

        return ChatGPTConverter.toRecipeResultDto(nameList, ingredientList, descriptionList);
    }

    /**
     *
     * @param recipeResultDTOList
     */
    public void saveGptRecipe(List<ChatGPTResponseDTO.RecipeResultDTO> recipeResultDTOList) {

        for (ChatGPTResponseDTO.RecipeResultDTO recipeResultDTO : recipeResultDTOList) {
            Recipe recipe = Recipe.builder()
                    .name(recipeResultDTO.getRecipeName())
                    .ingredient(recipeResultDTO.getIngredient())
                    .description(recipeResultDTO.getDescription())
                    .build();

            //이미 존재하는 레시피가 아니라면 db에 저장
            if (!recipeRepository.existsByName(recipeResultDTO.getRecipeName())) {
                recipeRepository.save(recipe);
            }
        }
    }

    /**
     *
     */
    public List<ChatGPTResponseDTO.RecipeResultDTO> getGptRecipe(Long memberId) {
        int recipeCount = 5;
        List<ChatGPTResponseDTO.RecipeResultDTO> result = new ArrayList<>();

        while (result.size() != recipeCount+2) {
            String request = createRequest(memberId, recipeCount);
            String response = prompt(request);
            System.out.println("레시피 카운트: " + recipeCount +"\n" +response);
            result = parseResponse(response);
            saveGptRecipe(result); //생성된 gpt 레시피 저장
            System.out.println("========================");
            System.out.println("최종 응답에 담긴 레시피 수: " + result.size());
            System.out.println("========================");
            if (recipeCount > 1) {  //응답이 잘 못 왔을 때, 레시피의 개수를 2개씩 줄여서 다시 요청(5 -> 3 -> 1)
                recipeCount -= 2;
            }
            if (recipeCount == 1) { //테스트할 때 api 요청이 계속 될 수도 있어서 break
                break;
            }
        }

        return result;
    }
}
