package com.sm.project.service.food;

import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.member.Member;
import com.sm.project.feignClient.lambda.LambdaFeignClient;
import com.sm.project.feignClient.naver.NaverOCRFeignClient;
import com.sm.project.repository.food.FoodRepository;
import com.sm.project.repository.food.ReceiptImageRepository;
import com.sm.project.repository.food.RefrigeratorRepository;
import com.sm.project.service.UtilService;
import com.sm.project.web.dto.food.FoodRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


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

    FoodService foodService;

    @BeforeEach
    void setup() {
        this.foodService = new FoodService(foodRepository,receiptImageRepository,utilService,naverOCRFeignClient,lambdaFeignClient,refrigeratorRepository);
    }
    @Test
    @DisplayName("냉장고 등록")
    void uploadRefrigerator() {
        //given
        Refrigerator refrigerator = Refrigerator.builder()
                .name("test 냉장고")
                .build();
        FoodRequestDTO.UploadRefrigeratorDTO request = new FoodRequestDTO.UploadRefrigeratorDTO("test 냉장고");
        Member member = Member.builder()
                .build();
        when(refrigeratorRepository.save(any(Refrigerator.class))).thenReturn(refrigerator);

        // when
        foodService.uploadRefrigerator(request,member);

        // then
        ArgumentCaptor<Refrigerator> refrigeratorCaptor = ArgumentCaptor.forClass(Refrigerator.class);
        verify(refrigeratorRepository, times(1)).save(refrigeratorCaptor.capture());

        Refrigerator savedRefrigerator = refrigeratorCaptor.getValue();
        assertNotNull(savedRefrigerator);
        assertEquals("test 냉장고", savedRefrigerator.getName());



    }
    @Test
    @DisplayName("")
    void uploadFood() {
    }

    @Test
    void getRefrigeratorList() {
    }

    @Test
    void getFoodList() {
    }

    @Test
    void updateFood() {
    }

    @Test
    void deleteFood() {
    }

}
