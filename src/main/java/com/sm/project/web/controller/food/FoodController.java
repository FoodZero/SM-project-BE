package com.sm.project.web.controller.food;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.converter.food.FoodConverter;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.member.Member;
import com.sm.project.service.food.FoodService;
import com.sm.project.service.member.MemberQueryService;
import com.sm.project.web.dto.food.FoodRequestDTO;
import com.sm.project.web.dto.food.FoodResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Food", description = "Food 관련 API")
@RequestMapping("/api")
public class FoodController {

    private final FoodService foodService;
    private final MemberQueryService memberQueryService;


    @PostMapping("/food/{refrigeratorId}")
    @Operation(summary = "음식 추가 API", description = "request: String 음식이름, 유통기한(2024-01-01), Integer 개수, 음식종류(COLD, FROZEN, OUTSIDE) ")
    public ResponseDTO<?> uploadFood(@RequestBody FoodRequestDTO.UploadFoodDTO request,
                                                                       @PathVariable(name = "refrigeratorId") Integer refrigeratorId,
                                                                       Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.uploadFood(request, member, refrigeratorId);

        return ResponseDTO.of(SuccessStatus.FOOD_UPLOAD_SUCCESS,null);

    }

    @GetMapping("/food/{refrigeratorId}")
    @Operation(summary = "음식 조회 API", description = "request parmeter에 냉장고 번호 입력하면 해당 냉장고 음식 조회 가능")
    public ResponseDTO<FoodResponseDTO.FoodListDTO> getFood(@PathVariable(name = "refrigeratorId") Integer refrigeratorId,
                                                            Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Food> foodList = foodService.getFoodList(member,refrigeratorId);

        return ResponseDTO.of(SuccessStatus.FOOD_GET_SUCCESS,FoodConverter.toGetFoodListResultDTO(foodList));
    }

    @PutMapping("/food/{refrigeratorId}/{foodId}")
    @Operation(summary = "음식 수정 api", description = "냉장고 번호와 음식 번호 request param으로 담고 request body에 수정해서 사용하면 수정됩니다.")
    public ResponseDTO<?> updateFood(@RequestBody FoodRequestDTO.UpdateFoodDTO request,
                                                                        @PathVariable(name = "refrigeratorId") Integer refrigeratorId,
                                                                       @PathVariable(name = "foodId") Long foodId,
                                                                       Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.updateFood(request,member,refrigeratorId,foodId);
        return ResponseDTO.of(SuccessStatus.FOOD_UPDATE_SUCCESS, null);
    }

    @DeleteMapping("/food/{refrigeratorId}/{foodId}")
    @Operation(summary = "음식 삭제 api", description = "냉장고 번호와 음식 번호 request param으로 담아서 사용하면 삭제됩니다.")
    public ResponseDTO<?> deleteFood(@PathVariable(name = "refrigeratorId") Integer refrigeratorId,
                                                                       @PathVariable(name = "foodId") Long foodId,
                                                                       Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.deleteFood(member,refrigeratorId,foodId);

        return ResponseDTO.of(SuccessStatus.FOOD_DELETE_SUCCESS,null);
    }

    @PostMapping(value = "/food/receipt", consumes = "multipart/form-data")
    @Operation(summary = "영수증 사진 등록 api", description = "영수증 사진을 담아서 호출하면 사진이 저장됩니다.")
    public ResponseDTO<?> uploadReceipt(@RequestParam("receipt") MultipartFile receipt, Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.uploadReceipt(member,receipt);
        return ResponseDTO.of(SuccessStatus.RECEIPT_UPLOAD_SUCCESS, null);
    }
}
