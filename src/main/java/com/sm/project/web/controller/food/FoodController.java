package com.sm.project.web.controller.food;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.converter.food.FoodConverter;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.member.Member;
import com.sm.project.feignClient.dto.NaverOCRResponse;
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
                                     @PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                     Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.uploadFood(request, member, refrigeratorId);

        return ResponseDTO.of(SuccessStatus.FOOD_UPLOAD_SUCCESS,null);

    }

    @PostMapping("/refrigerator")
    @Operation(summary = "냉장고 생성 API", description = "name에 냉장고 이름 적고 냉장고 추가할 때 사용하면 됩니다.")
    public ResponseDTO<?> postRefrigerator(Authentication authentication,
                                           @RequestBody FoodRequestDTO.UploadRefrigeratorDTO request){
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.uploadRefrigerator(request,member);

        return ResponseDTO.of(SuccessStatus.REFRIGERATOR_UPLOAD_SUCCESS,null);
    }

    @GetMapping("/refrigerator")
    @Operation(summary = "냉장고 조회 API", description = "냉장고 조회 api")
    public ResponseDTO<FoodResponseDTO.RefrigeratorListDTO> getRefrigerator(Authentication authentication){
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Refrigerator> refrigeratorList = foodService.getRefrigeratorList(member);

        return ResponseDTO.onSuccess(FoodConverter.toGetRefrigeratorListResultDTO(refrigeratorList));
    }

    @GetMapping("/food/{refrigeratorId}")
    @Operation(summary = "음식 조회 API", description = "request parmeter에 냉장고 번호 입력하면 해당 냉장고 음식 조회 가능")
    public ResponseDTO<FoodResponseDTO.FoodListDTO> getFood(@PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                                            Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Food> foodList = foodService.getFoodList(member,refrigeratorId);

        return ResponseDTO.of(SuccessStatus.FOOD_GET_SUCCESS,FoodConverter.toGetFoodListResultDTO(foodList));
    }

    @PutMapping("/food/{foodId}/{refrigeratorId}")
    @Operation(summary = "음식 수정 api", description = "음식 번호와 냉장고 번호를 request param으로 담고 request body에 수정해서 사용하면 수정됩니다.")
    public ResponseDTO<?> updateFood(@RequestBody FoodRequestDTO.UpdateFoodDTO request,
                                     @PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                     @PathVariable(name = "foodId") Long foodId,
                                     Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.updateFood(request,foodId,refrigeratorId);
        return ResponseDTO.of(SuccessStatus.FOOD_UPDATE_SUCCESS, null);
    }

    @DeleteMapping("/food/{foodId}/{refrigeratorId}")
    @Operation(summary = "음식 삭제 api", description = "음식 번호와 냉장고 번호를  request param으로 담아서 사용하면 삭제됩니다.")
    public ResponseDTO<?> deleteFood(@PathVariable(name = "foodId") Long foodId,
                                     @PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                     Authentication authentication){

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.deleteFood(foodId,refrigeratorId);

        return ResponseDTO.of(SuccessStatus.FOOD_DELETE_SUCCESS,null);
    }

    @PostMapping(value = "/food/receipt", consumes = "multipart/form-data")
    @Operation(summary = "영수증 사진 등록 api", description = "영수증 사진을 담아서 호출하면 사진이 저장됩니다.")
    public ResponseDTO<?> uploadReceipt(@RequestParam("receipt") MultipartFile receipt, Authentication authentication) throws Exception{

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        //S3에 영수증 사진 업로드
        String receiptUrl = foodService.uploadReceipt(member,receipt);

        //저장된 사진 가져와서 OCR로 텍스트 데이터 가져오기
        NaverOCRResponse naverOCRResponse = foodService.uploadReceiptData(receiptUrl);

        // 영수증에서 가져온 json 형태 데이터 식품 이름 아닌 것 필터링해서 List로 저장
        List<String> foodList = foodService.filterReceipt(naverOCRResponse);

        //식품 분류해주는 모델을 이용해서 식품인지 아닌지 구분해서 식품만 가져오기
        List<String> classifyFoodList = foodService.classifyFood(foodList);

        return ResponseDTO.of(SuccessStatus.RECEIPT_UPLOAD_SUCCESS, FoodConverter.toOCRResponseDTO(classifyFoodList));


    }


}
