package com.sm.project.web.dto.food;

import com.sm.project.domain.enums.FoodType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

public class FoodRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadFoodDTO {
        String name;
        Date expire;
        Integer count;
        FoodType foodType;
    }
}
