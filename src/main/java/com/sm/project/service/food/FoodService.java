package com.sm.project.service.food;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.FoodHandler;
import com.sm.project.apiPayload.exception.handler.RefrigeratorHandler;
import com.sm.project.config.PerformanceLoggingUtil;
import com.sm.project.converter.food.FoodConverter;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.mapping.MemberRefrigerator;
import com.sm.project.domain.member.Member;
import com.sm.project.feignClient.dto.LambdaRequest;
import com.sm.project.feignClient.dto.LambdaResponse;
import com.sm.project.feignClient.dto.NaverOCRResponse;
import com.sm.project.feignClient.lambda.LambdaFeignClient;
import com.sm.project.feignClient.naver.NaverOCRFeignClient;
import com.sm.project.repository.food.FoodRepository;
import com.sm.project.repository.food.ReceiptImageRepository;
import com.sm.project.repository.food.RefrigeratorRepository;
import com.sm.project.repository.member.MemberRefrigeratorRepository;
import com.sm.project.service.UtilService;
import com.sm.project.service.member.MemberQueryService;
import com.sm.project.web.dto.food.FoodRequestDTO;
import com.sm.project.web.dto.member.MemberRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * FoodService는 음식 관련 기능을 제공하는 서비스 클래스입니다.
 * 음식 추가, 조회, 수정, 삭제 및 영수증 OCR 기능을 담당합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FoodService {

    private final FoodRepository foodRepository;
    private final ReceiptImageRepository receiptImageRepository;
    private final UtilService utilService;
    private final NaverOCRFeignClient naverOCRFeignClient;
    private final LambdaFeignClient lambdaFeignClient;
    private final RefrigeratorRepository refrigeratorRepository;
    private final MemberRefrigeratorRepository memberRefrigeratorRepository;
    private final MemberQueryService memberQueryService;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 음식을 추가하는 메서드입니다.
     *
     * @param request 음식 추가 요청 데이터
     * @param member 회원 객체
     * @param refrigeratorId 냉장고 ID
     */
    public void uploadFood(FoodRequestDTO.UploadFoodDTO request, Member member, Long refrigeratorId) {

        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
            .orElseThrow(() -> new FoodHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));

        foodRepository.save(FoodConverter.toFoodDTO(request, refrigerator));
    }

    /**
     * 냉장고를 추가하는 메서드입니다.
     *
     * @param request 냉장고 추가 요청 데이터
     * @param member 회원 객체
     */
    public void uploadRefrigerator(FoodRequestDTO.UploadRefrigeratorDTO request, Member member) {

        Refrigerator refrigerator = Refrigerator.builder()
            .name(request.getName())
            .build();

        MemberRefrigerator memberRefrigerator =MemberRefrigerator.builder()
                .refrigerator(refrigerator)
                .member(member)
                .build();
        memberRefrigeratorRepository.save(memberRefrigerator);
        refrigeratorRepository.save(refrigerator);
    }

    /**
     * 회원의 모든 냉장고 목록을 조회하는 메서드입니다.
     *
     * @param member 회원 객체
     * @return 냉장고 목록
     */
    public List<Refrigerator> getRefrigeratorList(Member member) {

        List<MemberRefrigerator> memberRefrigerator = memberRefrigeratorRepository.findByMember(member);

        return memberRefrigerator.stream()
                .map(MemberRefrigerator::getRefrigerator)  // 각 MemberRefrigerator에서 Refrigerator 추출
                .collect(Collectors.toList());
    }

    /**
     * 냉장고 삭제 메서드
     * @param refrigeratorId
     * @param member
     */
    public void deleteRefrigerator(Long refrigeratorId,Member member){

        refrigeratorRepository.findById(refrigeratorId)
                .orElseThrow(() -> new FoodHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));
        refrigeratorRepository.deleteById(refrigeratorId);
    }

    public void updateRefrigeratorName(FoodRequestDTO.UpdateRefrigeratorDTO request, Long refrigeratorId){

        refrigeratorRepository.updateRefrigeratorName(refrigeratorId,request.getName());
    }

    /**
     * 특정 냉장고의 음식 목록을 조회하는 메서드입니다.
     *
     * @param member 회원 객체
     * @param refrigeratorId 냉장고 ID
     * @return 음식 목록
     */
    public List<Food> getFoodList(Member member, Long refrigeratorId) {
        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
            .orElseThrow(() -> new FoodHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));

        return foodRepository.findAllByRefrigerator(refrigerator);
    }

    /**
     * 음식을 수정하는 메서드입니다.
     *
     * @param request 음식 수정 요청 데이터
     * @param foodId 음식 ID
     * @param refrigeratorId 냉장고 ID
     */
    public void updateFood(FoodRequestDTO.UpdateFoodDTO request, Long foodId, Long refrigeratorId) {

        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
            .orElseThrow(() -> new FoodHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));

        foodRepository.changeFood(request.getName(), request.getCount(), request.getExpire(), request.getFoodType(), foodId, refrigerator);
    }

    /**
     * 음식을 삭제하는 메서드입니다.
     *
     * @param foodId 음식 ID
     * @param refrigeratorId 냉장고 ID
     */
    public void deleteFood(Long foodId, Long refrigeratorId) {

        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
            .orElseThrow(() -> new FoodHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));

        Food deleteFood = foodRepository.findByRefrigeratorAndId(refrigerator, foodId)
            .orElseThrow(() -> new FoodHandler(ErrorStatus.FOOD_NOT_FOUND));

        foodRepository.delete(deleteFood);
    }

    /**
     * 영수증 사진을 업로드하는 메서드입니다.
     *
     * @param member 회원 객체
     * @param receipt 영수증 사진 파일
     * @return 영수증 URL
     */
    public String uploadReceipt(Member member, MultipartFile receipt) {

        String receiptUrl = utilService.uploadS3Img("receipt", receipt);

        receiptImageRepository.save(FoodConverter.toReceiptImage(member,utilService.uploadS3Img("receipt", receipt)));

        return receiptUrl;
    }


    /**
     * 영수증 데이터를 OCR 처리하는 메서드입니다.
     *
     * @param receiptUrl 영수증 URL
     * @return OCR 결과
     */
    @Transactional
    public NaverOCRResponse uploadReceiptData(String receiptUrl) {

        lock.lock();

        try{
            PerformanceLoggingUtil.logPerformanceInfo(FoodService.class,"");

            NaverOCRResponse naverOCRResponse = naverOCRFeignClient.generateText(FoodConverter.toNaverOCRRequestDTO(receiptUrl));

            PerformanceLoggingUtil.logPerformanceInfo(FoodService.class,naverOCRResponse.toString());

        return naverOCRResponse;

    } catch (Exception e) {
        PerformanceLoggingUtil.logPerformanceError(FoodService.class, "Feign Client 오류 발생: " , e);

        throw e;

    }finally {
            lock.unlock();
        }

    }


    /**
     * OCR 결과에서 유효한 음식 이름만 필터링하는 메서드입니다.
     *
     * @param naverOCRResponse OCR 결과
     * @return 유효한 음식 이름 목록
     */
    public List<String> filterReceipt(NaverOCRResponse naverOCRResponse) {

        List<String> foodList = new ArrayList<>();

        // OCR 결과에서 텍스트 추출하여 foodList에 추가
        if (naverOCRResponse != null && naverOCRResponse.getImages() != null) {

            for (NaverOCRResponse.Image image : naverOCRResponse.getImages()) {

                if (image.getFields() != null) {

                    for (NaverOCRResponse.Field field : image.getFields()) {

                        if (field != null && field.getInferText() != null) {

                            foodList.add(field.getInferText());
                        }
                    }
                }
            }
        }

        return foodListRegularExpressionFiltering(foodList);
    }

    /**
     * 필터링된 음식 목록을 식품 분류 모델을 사용하여 분류하는 메서드입니다.
     *
     * @param foodList 필터링된 음식 목록
     * @return 식품으로 분류된 목록
     * @throws Exception 예외 발생 시
     */
    public List<String> classifyFood(List<String> foodList) throws Exception{


        //request로는 영수증에서 추출하고 1차적으로 필터링한 상품들
        LambdaResponse classifyFoodList = lambdaFeignClient.getFood(LambdaRequest.builder()
                .food(foodList)
                .build());

        String s = classifyFoodList.getBody();
        //결과가  "{\"result \": [true, true, true, true, true, true]}"} 이런식으로 넘어 오는데 boolean 값만 뽑아오기 위함
        String resultString = s.substring(s.indexOf('[') + 1, s.indexOf(']'));
        String[] result = resultString.split(", ");

        //식품인지 아닌지 filtering된 상품들만 배열에 담습니다.
        List<String> newFoodList = new ArrayList<>();

        for (int i = 0; i < foodList.size(); i++){
            if(result[i].equals("true")){
                newFoodList.add(foodList.get(i));
            }
        }

        return newFoodList;


    }

    public List<String> foodListRegularExpressionFiltering(List<String> foodList){

        // 영어로만 된 문자열 필터링 패턴
        Pattern patternEnglishOnly = Pattern.compile("^[a-zA-Z]+$");
        // 특수 문자가 포함된 문자열 중 유효한 패턴을 제외한 필터링 패턴
        Pattern patternSpecialChars = Pattern.compile(".*[^a-zA-Z0-9가-힣\\s\\*\\.].*");

        // 양 끝 숫자 문자 filter
        Pattern pattern1 = Pattern.compile("^\\d.*\\d$");
        // 원으로 끝나는 문자열 filter
        Pattern pattern3 = Pattern.compile(".*원$");
        // 숫자만 있는 문자열 filter
        Pattern pattern5 = Pattern.compile("^\\d+$");
        // g로 끝나는 문자열 filter
        Pattern pattern6 = Pattern.compile(".*g$");
        // 전화번호 패턴 filter
        Pattern pattern7 = Pattern.compile(".*Tel.*");
        // 날짜 패턴 filter
        Pattern pattern8 = Pattern.compile(".*[0-9]{2}:[0-9]{2}.*");
        // 주소 패턴 filter
        Pattern pattern9 = Pattern.compile(".*[점|로|길].*");
        // 할인,쿠폰 패턴 filter
        Pattern pattern10 = Pattern.compile(".*[할인|쿠폰|합계|면세품목|상품|매장|카드|가능].*");
        // 자사, IRC 패턴 filter
        Pattern pattern11 = Pattern.compile(".*자사.*|.*IRC.*");
        // 쉼표, 온점, 콜론으로 끝나는 문자열 filter
        Pattern pattern12 = Pattern.compile(".*[.,:]$");

        Matcher matcher1, matcher3, matcher5, matcher6, matcher7, matcher8, matcher9, matcher10, matcher11, matcher12, matcherEnglishOnly, matcherSpecialChars;


        // 패턴에 해당하는 문자열을 foodList에서 제거
        Iterator<String> iterator = foodList.iterator();
        while (iterator.hasNext()) {
            String food = iterator.next();
            matcher1 = pattern1.matcher(food);
            if (matcher1.matches()) {
                iterator.remove();
                continue;
            }
            matcherSpecialChars = patternSpecialChars.matcher(food);
            if (matcherSpecialChars.matches()) {
                iterator.remove();
                continue;
            }

            matcher3 = pattern3.matcher(food);
            if (matcher3.matches()) {
                iterator.remove();
                continue;
            }

            matcher5 = pattern5.matcher(food);
            if (matcher5.matches()) {
                iterator.remove();
                continue;
            }

            matcher6 = pattern6.matcher(food);
            if (matcher6.matches() && food.length() <= 4) { // "g"로 끝나는 두 글자 이하 문자열만 필터링
                iterator.remove();
                continue;
            }

            matcher7 = pattern7.matcher(food);
            if (matcher7.matches()) {
                iterator.remove();
                continue;
            }

            matcher8 = pattern8.matcher(food);
            if (matcher8.matches()) {
                iterator.remove();
                continue;
            }

            matcher9 = pattern9.matcher(food);
            if (matcher9.matches()) {
                iterator.remove();
                continue;
            }

            matcher10 = pattern10.matcher(food);
            if (matcher10.matches()) {
                iterator.remove();
                continue;
            }

            matcher11 = pattern11.matcher(food);
            if (matcher11.matches()) {
                iterator.remove();
                continue;
            }

            matcher12 = pattern12.matcher(food);
            if (matcher12.matches()) {
                iterator.remove();
                continue;
            }

            matcherEnglishOnly = patternEnglishOnly.matcher(food);
            if (matcherEnglishOnly.matches()) {
                iterator.remove();
                continue;
            }

            if (food.equals("상품명") || food.equals("단가") || food.equals("수량") || food.equals("금 액") ||
                    food.equals("합계") || food.equals("총액") || food.equals("부가세") ||
                    food.equals("교환") || food.equals("환불") || food.equals("결제변경") || food.equals("점포")||
                    food.equals("영수증") || food.equals("T") || food.equals("****")) {
                iterator.remove();
                continue;
            }

            // 길이가 1 또는 2인 문자열 필터링
            if (food.length() <= 1) {
                iterator.remove();
            }

        }
        return foodList;
    }


    /**
     * 냉장고 사용자 추가 메소드
     *
     * 사용자가 이메일을 입력하면 해당 이메일의 사용자를 냉장고를 공유할 수 있습니다.
     *
     * @param request 공유를 위한 사용자 이메일
     */
    public void shareRefrigerator(MemberRequestDTO.ShareDTO request){
        Member member = memberQueryService.findByEmail(request.getEamil());
        Refrigerator refrigerator = refrigeratorRepository.findById(request.getRefrigeratorId()).orElseThrow(() -> new RefrigeratorHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));

        MemberRefrigerator memberRefrigerator = MemberRefrigerator.builder()
                .member(member)
                .refrigerator(refrigerator)
                .build();
        memberRefrigeratorRepository.save(memberRefrigerator);
    }

    /**
     * 냉장고에 등록된 사용자들을 조회할 수 있습니다.
     */
    public List<Member> getShare(Long refrigeratorId){

        return memberRefrigeratorRepository.findMembersByRefrigeratorId(refrigeratorId);

    }

    /**
     * 냉장고에 등록된 사용자를 삭제할 수 있습니다.
     */
    public void deleteShare(Long memberId, Long refrigeratorId){

        memberRefrigeratorRepository.deleteByMemberId(memberId, refrigeratorId);
    }

    public void deleteAllShare(Long refrigeratorId, Long memberId){
        memberRefrigeratorRepository.deleteByRefrigeratorId(refrigeratorId, memberId);
    }

}
