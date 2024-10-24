package com.sm.project.web.controller.food;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.converter.food.FoodConverter;
import com.sm.project.converter.member.MemberConverter;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.member.Member;
import com.sm.project.feignClient.dto.NaverOCRResponse;
import com.sm.project.service.food.FoodService;
import com.sm.project.service.member.MemberQueryService;
import com.sm.project.web.dto.food.FoodRequestDTO;
import com.sm.project.web.dto.food.FoodResponseDTO;
import com.sm.project.web.dto.member.MemberRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * FoodController는 음식 관련 API 요청을 처리하는 컨트롤러 클래스입니다.
 * 음식 추가, 조회, 수정, 삭제 및 영수증 OCR 기능을 제공합니다.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Food", description = "Food 관련 API")
@RequestMapping("/api")
public class FoodController {

    private final FoodService foodService;
    private final MemberQueryService memberQueryService;


    /**
     * 음식 추가 API
     * 
     * @param request 음식 추가 요청 데이터
     * @param refrigeratorId 냉장고 ID
     * @param authentication 인증 정보
     * @return 음식 추가 성공 응답
     */
    @PostMapping("/food/{refrigeratorId}")
    @Operation(summary = "음식 추가 API", description = "request: String 음식이름, 유통기한(2024-01-01), Integer 개수, 음식종류(COLD, FROZEN, OUTSIDE)")
    public ResponseDTO<?> uploadFood(@RequestBody FoodRequestDTO.UploadFoodDTO request,
                                     @PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                     Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.uploadFood(request, member, refrigeratorId);

        return ResponseDTO.of(SuccessStatus.FOOD_UPLOAD_SUCCESS, null);
    }

    /**
     * 냉장고 생성 API
     * 
     * @param authentication 인증 정보
     * @param request 냉장고 생성 요청 데이터
     * @return 냉장고 생성 성공 응답
     */
    @PostMapping("/refrigerator")
    @Operation(summary = "냉장고 생성 API", description = "name에 냉장고 이름 적고 냉장고 추가할 때 사용하면 됩니다.")
    public ResponseDTO<?> postRefrigerator(Authentication authentication,
                                           @RequestBody FoodRequestDTO.UploadRefrigeratorDTO request) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.uploadRefrigerator(request, member);

        return ResponseDTO.of(SuccessStatus.REFRIGERATOR_UPLOAD_SUCCESS, null);
    }

    /**
     * 냉장고 삭제 API
     * @param authentication 인증정보
     * @return 냉장고 삭제 성공 응답
     */
    @DeleteMapping("/refrigerator/{refrigeratorId}")
    @Operation(summary = "냉장고 삭제 api", description = "냉장고 번호 request param으로 넘겨주면 됩니다.")
    public ResponseDTO<?> deleteRefrigerator(Authentication authentication,
                                             @PathVariable(name = "refrigeratorId") Long refrigeratorId){
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString()))
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.deleteRefrigerator(refrigeratorId, member);
        return ResponseDTO.of(SuccessStatus.REFRIGERATOR_DELETE_SUCCESS, null);
    }

    /**
     * 냉장고 조회 API
     * 
     * @param authentication 인증 정보
     * @return 냉장고 목록 조회 응답
     */
    @GetMapping("/refrigerator")
    @Operation(summary = "냉장고 조회 API", description = "냉장고 조회 api")
    public ResponseDTO<FoodResponseDTO.RefrigeratorListDTO> getRefrigerator(Authentication authentication) {
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Refrigerator> refrigeratorList = foodService.getRefrigeratorList(member);

        return ResponseDTO.onSuccess(FoodConverter.toGetRefrigeratorListResultDTO(refrigeratorList));
    }

    @PutMapping("/refrigerator/{refrigeratorId}")
    @Operation(summary = "냉장고 이름 수정 API", description = "냉장고 이름 수정 API")
    public ResponseDTO<?> updateRefrigeratorName(Authentication authentication,
                                                 @PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                                 @RequestBody FoodRequestDTO.UpdateRefrigeratorDTO request){
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString()))
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        foodService.updateRefrigeratorName(request,refrigeratorId);

        return ResponseDTO.of(SuccessStatus.NAME_UPDATE_SUCCESS,null);
    }

    /**
     * 음식 조회 API
     * 
     * @param refrigeratorId 냉장고 ID
     * @param authentication 인증 정보
     * @return 음식 목록 조회 응답
     */
    @GetMapping("/food/{refrigeratorId}")
    @Operation(summary = "음식 조회 API", description = "request parameter에 냉장고 번호 입력하면 해당 냉장고 음식 조회 가능")
    public ResponseDTO<FoodResponseDTO.FoodListDTO> getFood(@PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                                            Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Food> foodList = foodService.getFoodList(member, refrigeratorId);

        return ResponseDTO.of(SuccessStatus.FOOD_GET_SUCCESS, FoodConverter.toGetFoodListResultDTO(foodList));
    }

    /**
     * 음식 수정 API
     * 
     * @param request 음식 수정 요청 데이터
     * @param refrigeratorId 냉장고 ID
     * @param foodId 음식 ID
     * @param authentication 인증 정보
     * @return 음식 수정 성공 응답
     */
    @PutMapping("/food/{foodId}/{refrigeratorId}")
    @Operation(summary = "음식 수정 API", description = "음식 번호와 냉장고 번호를 request param으로 담고 request body에 수정해서 사용하면 수정됩니다.")
    public ResponseDTO<?> updateFood(@RequestBody FoodRequestDTO.UpdateFoodDTO request,
                                     @PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                     @PathVariable(name = "foodId") Long foodId,
                                     Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.updateFood(request, foodId, refrigeratorId);
        return ResponseDTO.of(SuccessStatus.FOOD_UPDATE_SUCCESS, null);
    }

    /**
     * 음식 삭제 API
     * 
     * @param foodId 음식 ID
     * @param refrigeratorId 냉장고 ID
     * @param authentication 인증 정보
     * @return 음식 삭제 성공 응답
     */
    @DeleteMapping("/food/{foodId}/{refrigeratorId}")
    @Operation(summary = "음식 삭제 API", description = "음식 번호와 냉장고 번호를 request param으로 담아서 사용하면 삭제됩니다.")
    public ResponseDTO<?> deleteFood(@PathVariable(name = "foodId") Long foodId,
                                     @PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                     Authentication authentication) {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.deleteFood(foodId, refrigeratorId);

        return ResponseDTO.of(SuccessStatus.FOOD_DELETE_SUCCESS, null);
    }

    /**
     * 영수증 사진 등록 API
     * 
     * @param receipt 영수증 사진 파일
     * @param authentication 인증 정보
     * @return 영수증 사진 등록 및 OCR 결과 응답
     * @throws Exception 예외 발생 시
     */
    @PostMapping(value = "/food/receipt", consumes = "multipart/form-data")
    @Operation(summary = "영수증 사진 등록 API", description = "영수증 사진을 담아서 호출하면 사진이 저장됩니다.")
    public ResponseDTO<?> uploadReceipt(@RequestParam("receipt") MultipartFile receipt, Authentication authentication) throws Exception {

        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // S3에 영수증 사진 업로드
        String receiptUrl = foodService.uploadReceipt(member, receipt);

        // 저장된 사진 가져와서 OCR로 텍스트 데이터 가져오기
        NaverOCRResponse naverOCRResponse = foodService.uploadReceiptData(receiptUrl);

        // 영수증에서 가져온 JSON 형태 데이터를 식품 이름 아닌 것 필터링해서 List로 저장
        List<String> foodList = foodService.filterReceipt(naverOCRResponse);

        // 식품 분류 모델을 이용해서 식품인지 아닌지 구분해서 식품만 가져오기
        List<String> classifyFoodList = foodService.classifyFood(foodList);

        return ResponseDTO.of(SuccessStatus.RECEIPT_UPLOAD_SUCCESS, FoodConverter.toOCRResponseDTO(classifyFoodList));
    }

    /**
     * 냉장고 공유를 위해 사용자 추가 API입니다.
     * @param authentication 인증 정보
     * @param request 이메일, 냉장고 Id
     * @return 성공 메세지 반환합니다.
     */
    @PostMapping("/share")
    @Operation(summary = "냉장고 공유 API", description = "이메일과 냉장고 Id를 입력하면 해당 이메일의 사용자를 냉장고에 등록하는 API입니다.")
    public ResponseDTO<?> shareRefrigerator(Authentication authentication,
                                            @RequestBody @Valid MemberRequestDTO.ShareDTO request){

        memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.shareRefrigerator(request);
        return ResponseDTO.onSuccess("냉장고 사용자 추가 성공입니다.");
    }

    /**
     * 냉장고에 등록된 사용자 조회 API입니다.
     * @param authentication 인증 정보
     * @param refrigeratorId 냉장고 id
     * @return 사용자 정보 반홥
     */
    @GetMapping("/share/{refrigeratorId}")
    @Operation(summary = "냉장고 등록된 사용자 조회 API", description = "냉장고 Id를 입력하면 등록된 사용자를 조회하는 API입니다.")
    public ResponseDTO<?> getShareRefrigerator(Authentication authentication,
                                               @PathVariable(name = "refrigeratorId") Long refrigeratorId){

        memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Member> memberList = foodService.getShare(refrigeratorId);

        return ResponseDTO.of(SuccessStatus.SHARE_GET_SUCCESS, MemberConverter.toShare(memberList));
    }

    /**
     * 냉장고에 등록된 사용자 삭제 API입니다.
     * @param authentication 인증 정보
     * @param memberId 사용자 id
     *
     * @return 성공 메세지 반환
     */
    @DeleteMapping("/share/{refrigeratorId}/{memberId}")
    @Operation(summary = "냉장고에 등록된 사용자 삭제 API", description = "사용자 id를 입력하면 냉장고를 공유한 사용자가 삭제됩니다.")
    public ResponseDTO<?> deleteShareRefrigerator(Authentication authentication,
                                                  @PathVariable(name = "refrigeratorId") Long refrigeratorId,
                                                  @PathVariable(name = "memberId") Long memberId){

        memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.deleteShare(memberId,refrigeratorId);

        return ResponseDTO.onSuccess("공유된 사용자 삭제 성공");
    }

    @DeleteMapping("share/{refrigeratorId}")
    @Operation(summary = "냉장고에 등록된 모든 사용자 삭제 API", description = "냉장고에 공유된 사용자 삭제됩니다.")
    public ResponseDTO<?> deleteAllShareRefrigerator(Authentication authentication,
                                                     @PathVariable(name = "refrigeratorId") Long refrigeratorId){
        Member member = memberQueryService.findMemberById(Long.valueOf(authentication.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        foodService.deleteAllShare(refrigeratorId, member.getId());

        return ResponseDTO.onSuccess("공유된 모든 사용자 삭제 성공");
    }
}
