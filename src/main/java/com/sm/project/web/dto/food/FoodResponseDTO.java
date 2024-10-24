package com.sm.project.web.dto.food;

import com.sm.project.domain.enums.FoodType;
import lombok.*;

import java.util.Date;
import java.util.List;

public class FoodResponseDTO {



    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FoodDTO {
        Long id;
        String name;
        Date expire;
        Integer count;
        FoodType foodType;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FoodListDTO {
        List<FoodDTO> foodList;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RefrigeratorListDTO {
        List<RefrigeratorDTO> refrigeratorList;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RefrigeratorDTO {
        Long id;
        String name;

    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OCRResponseDTO{
        List<String> foodList;
    }

}
