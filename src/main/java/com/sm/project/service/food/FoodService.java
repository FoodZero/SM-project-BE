package com.sm.project.service.food;

import com.sm.project.converter.food.FoodConverter;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.image.ReceiptImage;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.food.FoodRepository;
import com.sm.project.repository.food.ReceiptImageRepository;
import com.sm.project.service.UtilService;
import com.sm.project.web.dto.food.FoodRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FoodService {

    private final FoodRepository foodRepository;
    private final ReceiptImageRepository receiptImageRepository;
    private final UtilService utilService;

    public void uploadFood(FoodRequestDTO.UploadFoodDTO request, Member member, Integer refrigeratorId){


        Food newFood = FoodConverter.toFoodDTO(request, member, refrigeratorId);
        foodRepository.save(newFood);
        return;
    }

    public List<Food> getFoodList(Member member, Integer refigeratorId){

        List<Food> foodList = foodRepository.findAllByMemberAndRefrigeratorId(member,refigeratorId);

        return foodList;
    }

    public void updateFood(FoodRequestDTO.UpdateFoodDTO request, Member member, Integer refrigeratorId, Long foodId){

        foodRepository.changeFood(request.getName(),request.getCount(),request.getExpire(),request.getFoodType(),member,refrigeratorId,foodId);
        return;
    }

    public void deleteFood(Member member, Integer refrigeratorId, Long foodId){

        Food deleteFood = foodRepository.findByMemberAndIdAndRefrigeratorId(member, foodId, refrigeratorId);
        foodRepository.delete(deleteFood);
        return;
    }

    public void uploadReceipt(Member member, MultipartFile receipt){

        String receiptUrl = utilService.uploadS3Img("receipt", receipt);
        ReceiptImage receiptImage = ReceiptImage.builder()
                .url(receiptUrl)
                .member(member)
                .build();
        receiptImageRepository.save(receiptImage);
    }

}
