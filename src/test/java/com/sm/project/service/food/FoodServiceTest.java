package com.sm.project.service.food;

import com.sm.project.domain.enums.FoodType;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.mapping.MemberRefrigerator;
import com.sm.project.domain.member.Member;
import com.sm.project.feignClient.lambda.LambdaFeignClient;
import com.sm.project.feignClient.naver.NaverOCRFeignClient;
import com.sm.project.repository.food.FoodRepository;
import com.sm.project.repository.food.ReceiptImageRepository;
import com.sm.project.repository.food.RefrigeratorRepository;
import com.sm.project.repository.member.MemberRefrigeratorRepository;
import com.sm.project.service.UtilService;
import com.sm.project.service.member.MemberQueryService;
import com.sm.project.web.dto.food.FoodRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private RefrigeratorRepository refrigeratorRepository;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private ReceiptImageRepository receiptImageRepository;
    @Mock
    private UtilService utilService;
    @Mock
    private NaverOCRFeignClient naverOCRFeignClient;
    @Mock
    private LambdaFeignClient lambdaFeignClient;
    @Mock
    private MemberQueryService memberQueryService;
    private final ReentrantLock lock = new ReentrantLock();
    @Mock
    private MemberRefrigeratorRepository memberRefrigeratorRepository;

    @Mock
    private MemberQueryService memberQueryService;

    private final ReentrantLock lock = new ReentrantLock();
    @Mock
    private MemberRefrigeratorRepository memberRefrigeratorRepository;

    FoodService foodService;
    Member testMember;
    Refrigerator testRefrigerator;
    FoodRequestDTO.UploadFoodDTO testFoodRequestDTO;

    @BeforeEach
    void setup() {
        // FoodService 객체 생성
        this.foodService = new FoodService(
                foodRepository,
                receiptImageRepository,
                utilService,
                naverOCRFeignClient,
                lambdaFeignClient,
                refrigeratorRepository,
                memberRefrigeratorRepository,
                memberQueryService
        );

        // 테스트용 공통 객체 초기화
        testMember = Member.builder()
                .id(1L)
                .build();

        testRefrigerator = Refrigerator.builder()
                .id(1L)
                .name("test 냉장고")
                .build();

        Date date = new Date(); // 현재 날짜를 사용하여 테스트
        testFoodRequestDTO = new FoodRequestDTO.UploadFoodDTO("사과", date, 2, FoodType.COLD);
    }

    @Test
    @DisplayName("음식 등록")
    void uploadFood() {
        // given
        when(refrigeratorRepository.findById(1L)).thenReturn(java.util.Optional.of(testRefrigerator));

        // when
        foodService.uploadFood(testFoodRequestDTO, testMember, 1L);

        // then
        ArgumentCaptor<Food> foodCaptor = ArgumentCaptor.forClass(Food.class);
        verify(foodRepository, times(1)).save(foodCaptor.capture());
        Food savedFood = foodCaptor.getValue();
        assertNotNull(savedFood);
        assertEquals("사과", savedFood.getName());
        assertEquals(2, savedFood.getCount());
        assertEquals(testFoodRequestDTO.getExpire(), savedFood.getExpire());
    }

    @Test
    @DisplayName("음식 수정")
    void updateFood() {
        // given
        Date date = new Date();
        FoodRequestDTO.UpdateFoodDTO request = new FoodRequestDTO.UpdateFoodDTO("바나나", date, 2, FoodType.COLD);
        when(refrigeratorRepository.findById(1L)).thenReturn(java.util.Optional.of(testRefrigerator));

        // when
        foodService.updateFood(request, 1L, 1L);

        // then
        verify(foodRepository, times(1)).changeFood(
                request.getName(),
                request.getCount(),
                request.getExpire(),
                request.getFoodType(),
                1L,
                testRefrigerator
        );
    }

    @Test
    @DisplayName("음식 삭제")
    void deleteFood() {
        // given
        Food food = Food.builder()
                .id(1L)
                .name("사과")
                .refrigerator(testRefrigerator)
                .build();

        when(refrigeratorRepository.findById(1L)).thenReturn(java.util.Optional.of(testRefrigerator));
        when(foodRepository.findByRefrigeratorAndId(testRefrigerator, 1L)).thenReturn(java.util.Optional.of(food));

        // when
        foodService.deleteFood(1L, 1L);

        // then
        verify(foodRepository, times(1)).delete(food);
    }

    @Test
    @DisplayName("냉장고 등록")
    void uploadRefrigerator() {
        // given
        FoodRequestDTO.UploadRefrigeratorDTO request = new FoodRequestDTO.UploadRefrigeratorDTO("test 냉장고");
        when(refrigeratorRepository.save(any(Refrigerator.class))).thenReturn(testRefrigerator);

        // when
        foodService.uploadRefrigerator(request, testMember);

        // then
        ArgumentCaptor<Refrigerator> refrigeratorCaptor = ArgumentCaptor.forClass(Refrigerator.class);
        verify(refrigeratorRepository, times(1)).save(refrigeratorCaptor.capture());
        Refrigerator savedRefrigerator = refrigeratorCaptor.getValue();
        assertNotNull(savedRefrigerator);
        assertEquals("test 냉장고", savedRefrigerator.getName());
    }

    @Test
    @DisplayName("회원의 냉장고 목록 조회")
    void getRefrigeratorList() {
        // given
        Refrigerator refrigerator1 = Refrigerator.builder()
                .id(1L)
                .name("냉장고 1")
                .build();

        Refrigerator refrigerator2 = Refrigerator.builder()
                .id(2L)
                .name("냉장고 2")
                .build();

        MemberRefrigerator memberRefrigerator1 = MemberRefrigerator.builder()
                .member(testMember)
                .refrigerator(refrigerator1)
                .build();

        MemberRefrigerator memberRefrigerator2 = MemberRefrigerator.builder()
                .member(testMember)
                .refrigerator(refrigerator2)
                .build();

        List<MemberRefrigerator> memberRefrigerators = List.of(memberRefrigerator1, memberRefrigerator2);
        when(memberRefrigeratorRepository.findByMember(testMember)).thenReturn(memberRefrigerators);

        // when
        List<Refrigerator> result = foodService.getRefrigeratorList(testMember);

        // then
        assertEquals(2, result.size());
        assertEquals("냉장고 1", result.get(0).getName());
        assertEquals("냉장고 2", result.get(1).getName());
    }

    @Test
    @DisplayName("냉장고 음식 목록 조회")
    void getFoodList() {
        // given
        Food food1 = Food.builder()
                .name("사과")
                .refrigerator(testRefrigerator)
                .build();

        Food food2 = Food.builder()
                .name("바나나")
                .refrigerator(testRefrigerator)
                .build();

        List<Food> foods = List.of(food1, food2);

        when(refrigeratorRepository.findById(1L)).thenReturn(java.util.Optional.of(testRefrigerator));
        when(foodRepository.findAllByRefrigerator(testRefrigerator)).thenReturn(foods);

        // when
        List<Food> result = foodService.getFoodList(testMember, 1L);

        // then
        assertEquals(2, result.size());
        assertEquals("사과", result.get(0).getName());
        assertEquals("바나나", result.get(1).getName());
    }

    @Test
    @DisplayName("냉장고 삭제")
    void deleteRefrigerator() {
        // given
        when(refrigeratorRepository.findById(1L)).thenReturn(java.util.Optional.of(testRefrigerator));

        // when
        foodService.deleteRefrigerator(1L, testMember);

        // then
        verify(refrigeratorRepository, times(1)).deleteById(1L);
    }
}
