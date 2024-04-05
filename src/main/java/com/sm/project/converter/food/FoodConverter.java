package com.sm.project.converter.food;

import com.sm.project.domain.food.Food;
import com.sm.project.domain.member.Member;
import com.sm.project.feignClient.dto.Image;
import com.sm.project.feignClient.dto.NaverOCRRequest;
import com.sm.project.web.dto.food.FoodRequestDTO;
import com.sm.project.web.dto.food.FoodResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FoodConverter {

    public static Food toFoodDTO(FoodRequestDTO.UploadFoodDTO request, Member member, Integer refrigeratorId) {

        return Food.builder()
                .name(request.getName())
                .refrigeratorId(refrigeratorId)
                .count(request.getCount())
                .expire(request.getExpire())
                .foodType(request.getFoodType())
                .member(member)
                .build();
    }


    public static FoodResponseDTO.FoodListDTO toGetFoodListResultDTO(List<Food> foodList){
        List<FoodResponseDTO.FoodDTO> foodListDTO = foodList.stream().map(food -> FoodResponseDTO.FoodDTO.builder()
                                                                                                    .name(food.getName())
                                                                                                    .count(food.getCount())
                                                                                                    .expire(food.getExpire())
                                                                                                    .foodType(food.getFoodType())
                                                                                                    .build()).collect(Collectors.toList());
        return FoodResponseDTO.FoodListDTO.builder()
                .foodList(foodListDTO)
                .build();
    }




    public static NaverOCRRequest toNaverOCRRequestDTO(String receiptUrl){

        List<Long> template = new ArrayList<>();
        template.add(28357L);

        Image image = Image.builder()
                .format("png")
                .url(receiptUrl)
                .templateIds(template)
                .name("이마트")
                .build();

        List<Image> imageList = new ArrayList<>();
        imageList.add(image);

        return NaverOCRRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .lang("ko")
                .images(imageList)
                .timestamp(System.currentTimeMillis())
                .version("V2")
                .enableTableDetection(false)
                .build();
    }

    public static FoodResponseDTO.OCRResponseDTO toOCRResponseDTO(List<String> foodList){


        return FoodResponseDTO.OCRResponseDTO.builder()
                .foodList(foodList)
                .build();
    }
}
