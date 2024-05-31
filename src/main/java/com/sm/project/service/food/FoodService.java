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

    public void uploadFood(FoodRequestDTO.UploadFoodDTO request, Member member, Integer refrigeratorId){


        Food newFood = FoodConverter.toFoodDTO(request, member, refrigeratorId);
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

    public List<String> filterReceipt(NaverOCRResponse naverOCRResponse){

        List<String> foodList = new ArrayList<>();
        // 양 끝 숫자 문자 filter
        Pattern pattern1 = Pattern.compile("^\\d.*\\d$");
        // 특수 문자로된 문자열 filter
        Pattern pattern2 = Pattern.compile("[^a-zA-Z0-9]");
        // 원으로 끝나는 문자열 filter
        Pattern pattern3 = Pattern.compile(".*원$");
        // 0~9 숫자 filter
        Pattern pattern4 = Pattern.compile("[0-9]");
        Matcher matcher1, matcher2, matcher3, matcher4;

        // json으로 넘어오는 데이터 List로 변환
        if(naverOCRResponse != null && naverOCRResponse.getImages() != null){
            for(NaverOCRResponse.Image image : naverOCRResponse.getImages()){
                if (image.getFields() != null) {
                    for (NaverOCRResponse.Field field : image.getFields()) {
                        if (field != null && field.getInferText() != null) {
                            foodList.add(field.getInferText());
                        }
                    }
                }
            }
        }

        //위에서 작성한 pattern에 해당하는 문자열 List에서 제거
        Iterator<String> iterator = foodList.iterator();
        while(iterator.hasNext()){
            String food = iterator.next();
            matcher1 = pattern1.matcher(food);
            if(matcher1.matches()){
                iterator.remove();
                continue;
            }
            matcher2 = pattern2.matcher(food);
            if(matcher2.matches()){
                iterator.remove();
                continue;
            }

            matcher3 = pattern3.matcher(food);
            if(matcher3.matches()){
                iterator.remove();
                continue;
            }

            matcher4 = pattern4.matcher(food);
            if(matcher4.matches()){
                iterator.remove();
                continue;
            }

            if(food.equals("상품명") || food.equals("단가") || food.equals("수량") || food.equals("금 액")){
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
