package com.sm.project.service.food;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.FoodHandler;
import com.sm.project.converter.food.FoodConverter;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.image.ReceiptImage;
import com.sm.project.domain.member.Member;
import com.sm.project.feignClient.dto.LambdaRequest;
import com.sm.project.feignClient.dto.LambdaResponse;
import com.sm.project.feignClient.dto.NaverOCRResponse;
import com.sm.project.feignClient.lambda.LambdaFeignClient;
import com.sm.project.feignClient.naver.NaverOCRFeignClient;
import com.sm.project.repository.food.FoodRepository;
import com.sm.project.repository.food.ReceiptImageRepository;
import com.sm.project.repository.food.RefrigeratorRepository;
import com.sm.project.service.UtilService;
import com.sm.project.web.dto.food.FoodRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public void uploadFood(FoodRequestDTO.UploadFoodDTO request, Member member, Long refrigeratorId){

        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId).orElseThrow(()-> new FoodHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));
        Food newFood = FoodConverter.toFoodDTO(request, refrigerator);
        foodRepository.save(newFood);
        return;
    }

    public void uploadRefrigerator(FoodRequestDTO.UploadRefrigeratorDTO request, Member member){
        Refrigerator refrigerator = Refrigerator.builder()
                .member(member)
                .name(request.getName())
                .build();
        refrigeratorRepository.save(refrigerator);
    }

    public List<Refrigerator> getRefrigeratorList(Member member){

        return refrigeratorRepository.findAllByMember(member);

    }

    public List<Food> getFoodList(Member member, Long refigeratorId){

        Refrigerator refrigerator = refrigeratorRepository.findByIdAndMember(refigeratorId,member).orElseThrow(() -> new FoodHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));



        return foodRepository.findAllByRefrigerator(refrigerator);

    }

    public void updateFood(FoodRequestDTO.UpdateFoodDTO request, Long foodId, Long refrigeratorId){

        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId).orElseThrow(() -> new FoodHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));
        foodRepository.changeFood(request.getName(),request.getCount(),request.getExpire(),request.getFoodType(),foodId, refrigerator);

    }

    public void deleteFood(Long foodId, Long refrigeratorId){

        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId).orElseThrow(() -> new FoodHandler(ErrorStatus.RERFIGERATOR_NOT_FOUND));
        Food deleteFood = foodRepository.findByRefrigeratorAndId(refrigerator, foodId).orElseThrow(() -> new FoodHandler(ErrorStatus.FOOD_NOT_FOUND));
        foodRepository.delete(deleteFood);

    }

    public String uploadReceipt(Member member, MultipartFile receipt){

        String receiptUrl = utilService.uploadS3Img("receipt", receipt);
        ReceiptImage receiptImage = ReceiptImage.builder()
                .url(receiptUrl)
                .member(member)
                .build();
        receiptImageRepository.save(receiptImage);
        return receiptUrl;
    }

    @Transactional
    public NaverOCRResponse uploadReceiptData(String receiptUrl){


        NaverOCRResponse naverOCRResponse = naverOCRFeignClient.generateText(FoodConverter.toNaverOCRRequestDTO(receiptUrl));

        return naverOCRResponse;
    }

    // 영어로만 된 문자열 필터링 패턴
    private static final Pattern patternEnglishOnly = Pattern.compile("^[a-zA-Z]+$");
    // 특수 문자가 포함된 문자열 중 유효한 패턴을 제외한 필터링 패턴
    private static final Pattern patternSpecialChars = Pattern.compile(".*[^a-zA-Z0-9가-힣\\s\\*\\.].*");

    public List<String> filterReceipt(NaverOCRResponse naverOCRResponse) {

        List<String> foodList = new ArrayList<>();
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

        // json으로 넘어오는 데이터 List로 변환
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

        // 위에서 작성한 pattern에 해당하는 문자열 List에서 제거
        Iterator<String> iterator = foodList.iterator();
        while (iterator.hasNext()) {
            String food = iterator.next();
            System.out.println(food);
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
            if (food.length() <= 2) {
                iterator.remove();
            }

        }

        return foodList;
    }




    public List<String> classifyFood(List<String> foodList) throws Exception{
        //구현한 모델을 lambda 함수를 이용해서 만든 api를 호출합니다.
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

}
