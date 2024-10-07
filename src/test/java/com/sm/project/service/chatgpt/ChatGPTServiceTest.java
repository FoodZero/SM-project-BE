package com.sm.project.service.chatgpt;

import com.sm.project.apiPayload.exception.handler.ChatGPTHandler;
import com.sm.project.apiPayload.exception.handler.FoodHandler;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.chatgpt.RecipeNameDto;
import com.sm.project.repository.food.BookmarkRepository;
import com.sm.project.repository.food.FoodGPTDto;
import com.sm.project.repository.food.FoodRepository;
import com.sm.project.repository.food.RecipeRepository;
import com.sm.project.repository.member.MemberRepository;
import com.sm.project.web.dto.chatgpt.ChatGPTResponseDTO;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatGPTServiceTest {


    @Mock
    private OpenAiService openAiService;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private ChatGPTService chatGPTService;

    private Long memberId;
    private ChatGPTResponseDTO.RecipeResultDTO recipeResultDTO;
    private Member member;
    private Recipe recipe;
    private List<Recipe> recipeList;
    private List<FoodGPTDto> foodGPTDtoList;
    private List<RecipeNameDto> existingRecipeNameDtoList;
    private ChatCompletionResult chatCompletionResult;
    private String recipeName;
    private String ingredient;
    private String description;

    @BeforeEach
    void init() {
        memberId = 1L;
        recipeResultDTO = ChatGPTResponseDTO.RecipeResultDTO.builder().build();
        member = Member.builder().build();
        recipe = Recipe.builder().build();
        Recipe recipe1 = Recipe.builder()
                .id(1L)
                .name("김치 볶음밥")
                .ingredient("김치, 햄")
                .recommendCount(0L)
                .build();
        Recipe recipe2 = Recipe.builder()
                .id(2L)
                .name("알리오 올리오")
                .ingredient("올리브유, 면")
                .recommendCount(0L)
                .build();
        recipeList = new ArrayList<>();
        recipeList.add(recipe1);
        recipeList.add(recipe2);

        chatGPTService = new ChatGPTService(foodRepository, recipeRepository, memberRepository, bookmarkRepository);
        chatGPTService.setOpenAiService(openAiService); // mock객체로 세팅

        foodGPTDtoList = new ArrayList<>();
        foodGPTDtoList.add(FoodGPTDto.builder()
                .name("김치")
                .build());
        foodGPTDtoList.add(FoodGPTDto.builder()
                .name("햄")
                .build());

        existingRecipeNameDtoList = new ArrayList<>();
        existingRecipeNameDtoList.add(new RecipeNameDto("알리오 올리오"));
        existingRecipeNameDtoList.add(new RecipeNameDto("연어 덮밥"));

        recipeName = "김치 볶음밥";
        ingredient = "김치, 햄, 밥";
        description = "김치, 햄, 밥을 볶는다.";
        chatCompletionResult = new ChatCompletionResult();
        List<ChatCompletionChoice> choices = new ArrayList<>();
        ChatCompletionChoice choice = new ChatCompletionChoice();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("assistant");
        chatMessage.setContent("레시피 이름: " + recipeName + "\n\n" +
                "재료: " + ingredient + "\n\n" +
                "설명: " + description + "\n\n");
        choice.setIndex(0);
        choice.setMessage(chatMessage);
        choices.add(choice);
        chatCompletionResult.setChoices(choices);
    }

    @Test
    @DisplayName("gpt에 요청 전송")
    void prompt() {
        //given
        when(recipeRepository.findRecipeName(memberId)).thenReturn(existingRecipeNameDtoList); // RecipeRepository에서 findRecipeName 메소드 모의
        String userRequest = "새로운 레시피를 추천해줘";
        when(openAiService.createChatCompletion(any())).thenReturn(chatCompletionResult);

        //when
        String result = chatGPTService.prompt(userRequest, memberId);

        //then
        assertTrue(result.contains("레시피 이름:")); // "레시피 이름:"이 포함되어 있는지 확인

        // 레포지토리와 서비스가 각각 몇 번 호출되었는지 검증
        verify(recipeRepository, times(1)).findRecipeName(memberId);
        verify(openAiService, times(1)).createChatCompletion(any());
    }

    @Test
    @DisplayName("요청 문자열 생성")
    void createRequest() {
        //given
        when(foodRepository.findFoodGptDto(any())).thenReturn(foodGPTDtoList);

        //when
        String result = chatGPTService.createRequest(memberId);

        //then
        assertThat(result).isEqualTo("김치, 햄 중에 선택해서 그 재료들이 들어간 레시피를 말해줘. 재료는 유통기한이 짧은 순서대로 나열되어 있어. 최대한 유통기한이 짧은 재료를 넣은 레시피를 우선적으로 말해." +
                "그리고 주어진 재료 외에 더 필요한 재료가 있으면 재료목록에 추가해. 다른 재료를 선택해서 새로운 종류의 레시피를 말해. 기존 레시피에 숫자 붙이거나 변형한 레시피는 새로운게 아니야");
    }

    @Test
    @DisplayName("gpt 요청 문자열 생성 - 예외")
    void createRequestWithException() {
        //given
        List<FoodGPTDto> foodGPTDtoList = new ArrayList<>();
        when(foodRepository.findFoodGptDto(any())).thenReturn(foodGPTDtoList);

        //when
        Throwable throwable = catchThrowable(() -> chatGPTService.createRequest(memberId));

        //then
        assertThat(throwable).isInstanceOf(FoodHandler.class);
    }

    @Test
    @DisplayName("gpt 응답 문자열 파싱")
    void parseResponse() {
        //given
        String response = "[ChatCompletionChoice(index=0, message=ChatMessage(role=assistant, content=레시피 이름: 김치 볶음밥\n\n" +
                "재료: 김치, 햄, 밥\n\n" +
                "설명: 김치, 햄, 밥을 볶는다.\n\n" +
                "), finishReason=stop)]";

        //when
        ChatGPTResponseDTO.RecipeResultDTO result = chatGPTService.parseResponse(response);

        //then
        assertThat(result.getRecipeName()).isEqualTo(recipeName);
        assertThat(result.getIngredient()).isEqualTo(ingredient);
        assertThat(result.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("gpt 응답 문자열 파싱 - 예외")
    void parseResponseWithException() {
        //given
        //잘못된 응답들
        String response1 = "";
        String response2 = "[ChatCompletionChoice(index=0, message=ChatMessage(role=assistant, content=레시피: 김치 볶음밥\n\n" +
                "재료: 김치, 햄, 밥\n\n" +
                "설명: 김치, 햄, 밥을 볶는다.\n\n" +
                "), finishReason=stop)]";
        String response3 = "[ChatCompletionChoice(index=0, message=ChatMessage(role=assistant, content=레시피 이름: 김치 볶음밥\n\n" +
                "음식: 김치, 햄, 밥\n\n" +
                "설명: 김치, 햄, 밥을 볶는다.\n\n" +
                "), finishReason=stop)]";
        String response4 = "[ChatCompletionChoice(index=0, message=ChatMessage(role=assistant, content=레시피 이름: 김치 볶음밥\n\n" +
                "재료: 김치, 햄, 밥\n\n" +
                "단계: 김치, 햄, 밥을 볶는다.\n\n" +
                "), finishReason=stop)]";

        //when
        Throwable throwable1 = catchThrowable(() -> chatGPTService.parseResponse(response1));
        Throwable throwable2 = catchThrowable(() -> chatGPTService.parseResponse(response2));
        Throwable throwable3 = catchThrowable(() -> chatGPTService.parseResponse(response3));
        Throwable throwable4 = catchThrowable(() -> chatGPTService.parseResponse(response4));

        //then
        assertThat(throwable1).isInstanceOf(ChatGPTHandler.class);
        assertThat(throwable2).isInstanceOf(ChatGPTHandler.class);
        assertThat(throwable3).isInstanceOf(ChatGPTHandler.class);
        assertThat(throwable4).isInstanceOf(ChatGPTHandler.class);
    }

    @Test
    @DisplayName("gpt 레시피 저장")
    @Transactional
    void saveGptRecipe() {
        //given
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(recipeRepository.save(any())).thenReturn(recipe);

        //when
        chatGPTService.saveGptRecipe(recipeResultDTO, memberId);

        //then
        verify(memberRepository, times(1)).findById(memberId);
        verify(recipeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("gpt 레시피 저장 - 예외")
    void saveGptRecipeWithException() {
        //given
        when(memberRepository.findById(any())).thenReturn(Optional.empty());


        //when & then
        assertThrows(MemberHandler.class, () -> chatGPTService.saveGptRecipe(recipeResultDTO, memberId));
    }

    @Test
    @DisplayName("gpt 레시피 추천")
    void getGptRecipe() {
        //given
        when(foodRepository.findFoodGptDto(any())).thenReturn(foodGPTDtoList);
        when(recipeRepository.findRecipeName(memberId)).thenReturn(existingRecipeNameDtoList); // RecipeRepository에서 findRecipeName 메소드 모의
        when(openAiService.createChatCompletion(any())).thenReturn(chatCompletionResult);
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(recipeRepository.save(any())).thenReturn(recipe);

        //when
        ChatGPTResponseDTO.RecipeResultDTO result = chatGPTService.getGptRecipe(memberId);

        //then
        assertThat(result.getRecipeName()).isEqualTo(recipeName);
        assertThat(result.getIngredient()).isEqualTo(ingredient);
        assertThat(result.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("gpt 레시피 목록 조회")
    void getGptRecipeList() {
        //given
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(recipeRepository.findByMemberIdAndIsDeleted(member)).thenReturn(recipeList);

        //when
        ChatGPTResponseDTO.RecipeListResultDto result = chatGPTService.getGptRecipeList(memberId);

        //then
        assertThat(result.getRecipeDtoList().get(0).getRecipeName()).isEqualTo(recipeList.get(0).getName());
    }

    @Test
    @DisplayName("gpt 레시피 목록 조회 - 예외")
    void getGptRecipeListWithException() {
        //given
        when(memberRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThrows(MemberHandler.class, () -> chatGPTService.getGptRecipeList(memberId));
    }

    @Test
    @DisplayName("gpt 레시피 초기화")
    void deleteGptRecipe() {
        //given
        when(recipeRepository.findByMemberId(any())).thenReturn(recipeList);
        when(recipeRepository.existsByNameAndIdNot(any(), any())).thenReturn(true);
        when(bookmarkRepository.existsByRecipe(any())).thenReturn(false);
        doNothing().when(recipeRepository).delete(any());

        //when
        chatGPTService.deleteGptRecipe(memberId);

        //then
        verify(recipeRepository, times(1)).findByMemberId(any());
        verify(recipeRepository, times(recipeList.size())).existsByNameAndIdNot(any(), any());
        verify(bookmarkRepository, times(recipeList.size())).existsByRecipe(any());
        verify(recipeRepository, times(recipeList.size())).delete(any());
    }
}