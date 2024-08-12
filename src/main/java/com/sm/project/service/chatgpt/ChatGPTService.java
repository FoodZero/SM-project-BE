package com.sm.project.service.chatgpt;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.ChatGPTHandler;
import com.sm.project.apiPayload.exception.handler.FoodHandler;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.converter.chatgpt.ChatGPTConverter;
import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.chatgpt.RecipeNameDto;
import com.sm.project.repository.food.FoodGPTDto;
import com.sm.project.repository.food.FoodRepository;
import com.sm.project.repository.food.RecipeRepository;
import com.sm.project.repository.member.MemberRepository;
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
import java.util.stream.Collectors;


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
    private static final int FOODLISTCOUNT = 50;  //조회할 재료의 수를 제한
    private final FoodRepository foodRepository;
    private final RecipeRepository recipeRepository;
    private final MemberRepository memberRepository;

    /**
     * ChatGPT의 응답을 생성하는 메서드입니다.
     *
     * @param userRequest,systemRequest 사용자의 질문을 담고 있는 요청 객체
     * @return ChatGPT의 응답 객체
     */
    public String prompt(String userRequest, Long memberId) {
        openAiService = new OpenAiService(gptKey, Duration.ofSeconds(60));  //타임아웃 시간 설정(60초)

        // 사용자 메시지를 생성하고 목록에 추가
        final List<ChatMessage> messages = new ArrayList<>();

        String systemRequest = "모든 대답을 다음 형식과 똑같이 말하고, 줄바꿈도 형식과 똑같이 해. 레시피는 딱 1개만 말해.:\n" +
                "레시피 이름: [이름]\n" +
                "\n" +
                "재료: [재료 1], [재료 2], ...\n" +
                "\n" +
                "설명: \n" +
                "1. [1단계]\n" +
                "2. [2단계]\n" +
                "3. [3단계]\n" +
                "4. [4단계] ...";

        String existingRecipeList = "";
        List<RecipeNameDto> existingRecipeNameDtoList = recipeRepository.findRecipeName(memberId);
        for (RecipeNameDto recipeNameDto : existingRecipeNameDtoList) {
            existingRecipeList += recipeNameDto.getName() + ", ";  //지연로딩
        }
        if (!existingRecipeList.isEmpty()) {
            existingRecipeList = existingRecipeList.substring(0, existingRecipeList.length() - 2);
        }

        String systemRequest2 = "이미 있는 레시피는 또 말하지말고, 절대 레시피를 변형해서 대답하지마. 레시피 이름에 숫자 붙여서 변형하지마! 다른 재료를 선택해서 완전 다른 요리를 소개해. 다음은 이미 있는 레시피야: " + existingRecipeList;
        System.out.println(systemRequest2);

        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemRequest);
        messages.add(systemMessage);
        final ChatMessage systemMessage2 = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemRequest2);
        messages.add(systemMessage2);
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userRequest);
        messages.add(userMessage);

        // ChatGPT 요청 객체 생성
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL) // GPT 모델 지정
                .messages(messages)
                .n(1) // GPT 응답 수 설정
                .maxTokens(4096) //토큰 제한(최대로)
                .temperature(1.1) //0에 가까울수록 창의적인 대답(기본값1)
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
    public String createRequest(Long memberId) {
        String request;
        String foodList = "";

        List<FoodGPTDto> foodGPTDtoList = foodRepository.findFoodGptDto(memberId);
        if (foodGPTDtoList.isEmpty()) { //음식 조회 결과가 없는 경우
            throw new FoodHandler(ErrorStatus.FOOD_NOT_FOUND);
        }

        for (int i = 0; i < foodGPTDtoList.size(); i++) {
            if (i >= FOODLISTCOUNT) break;
            foodList += foodGPTDtoList.get(i).toString() + ", ";
        }
        if(!foodList.isEmpty()) {
            foodList = foodList.substring(0, foodList.length() - 2);
        }

        request = foodList + " 중에 선택해서 그 재료들이 들어간 레시피를 말해줘. " +
                "그리고 주어진 재료 외에 더 필요한 재료가 있으면 재료목록에 추가해. 다른 재료를 선택해서 새로운 종류의 레시피를 말해. 기존 레시피에 숫자 붙이거나 변형한 레시피는 새로운게 아니야";

        System.out.println(request);
        return request;
    }

    /**
     * gpt의 응답을 잘라서 dto에 담는 메소드입니다.
     * @param response
     * @return
     */
    public ChatGPTResponseDTO.RecipeResultDTO parseResponse(String response) {

        String name;
        String ingredient;
        String description;

        String[] parts = response.split("\n\n");

        if (parts.length < 3 || !parts[0].contains("레시피 이름") || !parts[1].contains("재료") || !parts[2].contains("설명")) { //gpt 응답이 형식을 지키지 않은 경우, 예외처리. 3은 항목의 수(레시피 이름, 재료, 설명)이다.
            throw new ChatGPTHandler(ErrorStatus.GPT_RESPONSE_ERROR);
        }

        name = parts[0].replace("[ChatCompletionChoice(index=0, message=ChatMessage(role=assistant, content=레시피 이름: ", "").trim();
        ingredient= parts[1].replace("재료: ","").trim();
        description = parts[2].replace("설명: ", "").trim();
        description = description.replace("설명:", "").trim();  //가끔 gpt가 띄어쓰기를 안 넣기도 해서 한번 더 replace
        description = description.replace("), finishReason=stop)]", "").trim();

        return ChatGPTConverter.toRecipeResultDto(name, ingredient, description);
    }

    /**
     * gpt로 생성된 레시피를 저장하는 메소드입니다.
     * @param recipeResultDTO
     */
    public Long saveGptRecipe(ChatGPTResponseDTO.RecipeResultDTO recipeResultDTO, Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Recipe recipe = Recipe.builder()
                .name(recipeResultDTO.getRecipeName())
                .ingredient(recipeResultDTO.getIngredient())
                .description(recipeResultDTO.getDescription())
                .member(member)
                .build();

        //이미 존재하는 레시피가 아니라면 db에 저장
        //if (!recipeRepository.existsByName(recipeResultDTO.getRecipeName()))
        recipeRepository.save(recipe);
        return recipe.getId();
    }

    /**
     * gpt 레시피를 추천받는데 필요한 메소드들을 통합한 메소드입니다.
     * @param memberId
     * @return
     */
    public ChatGPTResponseDTO.RecipeResultDTO getGptRecipe(Long memberId) {
        ChatGPTResponseDTO.RecipeResultDTO result;

        String request = createRequest(memberId);
        String response = prompt(request, memberId);
        System.out.println("response = " + response);

        result = parseResponse(response);
        result.setRecipeId(saveGptRecipe(result, memberId)); //생성된 gpt 레시피 저장

        System.out.println("===============================");
        System.out.println(result.toString());
        System.out.println("===============================");

        return result;
    }

    /**
     * gpt 레시피 목록을 조회하여 dto에 담아 반환하는 메소드입니다.
     * @param memberId
     * @return
     */
    public ChatGPTResponseDTO.RecipeListResultDto getGptRecipeList(Long memberId) {
        List<Recipe> recipeList = recipeRepository.findByMemberId(memberId);

        List<ChatGPTResponseDTO.RecipeDto> recipeDtoList = recipeList.stream().map(r -> ChatGPTResponseDTO.RecipeDto.builder()
                .recipeId(r.getId())
                .recipeName(r.getName())
                .ingredient(r.getIngredient())
                .recommendCount(r.getRecommendCount())
                .build())
                .collect(Collectors.toList());

        return ChatGPTConverter.toRecipeListResultDto(recipeList.size(), recipeDtoList);
    }

    /**
     * gpt 레시피가 북마크 되어있지 않고, 이미 존재한다면 초기화(삭제)하는 메소드입니다.
     * @param memberId
     */
    public void deleteGptRecipe(Long memberId) {
        List<Recipe> recipeList = recipeRepository.findByMemberId(memberId);

        recipeList.stream()
                .filter(recipe -> recipeRepository.existsByNameAndIdNot(recipe.getName(), recipe.getId()) /*&& !recipe.getBookmark()*/)  //북마크가 0이고, 동일한 이름의 이름이 존재하는 레시피만 남겨서 삭제 -> 북마크 테이블 따로 만들면 수정 필요
                .forEach(recipe -> recipeRepository.delete(recipe));
    }
}
