package com.sm.project.apiPayload.code.status;

import com.sm.project.apiPayload.code.BaseCode;
import com.sm.project.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),


    //FOOD
    FOOD_UPLOAD_SUCCESS(HttpStatus.OK, "FOOD200", "음식 업로드 성공"),
    FOOD_UPDATE_SUCCESS(HttpStatus.OK, "FOOD2001", "음식 업데이트 성공"),
    FOOD_GET_SUCCESS(HttpStatus.OK, "FOOD2002", "음식 조회 성공"),
    FOOD_DELETE_SUCCESS(HttpStatus.OK, "FOOD2003", "음식 삭제 성공"),
    //RECEIPT
    RECEIPT_UPLOAD_SUCCESS(HttpStatus.OK, "RECEIPT200", "영수증 저장 성공"),
    //MEMBER
    MEMBER_PUSH_SUCCESS(HttpStatus.OK, "MEMBER200", "앱 푸쉬 성공"),
    //POST
    POST_CREATE_SUCCESS(HttpStatus.OK, "POST200", "글 등록 성공"),
    POST_UPDATE_SUCCESS(HttpStatus.OK, "POST2001", "글 수정 성공"),
    POST_DELETE_SUCCESS(HttpStatus.OK, "POST2002", "글 삭제 성공"),


    //LOCATION
    LOCATION_POST_SUCCESS(HttpStatus.OK, "LOCATION200", "위치 저장 성공입니다."),

    //REFRIGERATOR
    REFRIGERATOR_UPLOAD_SUCCESS(HttpStatus.OK, "REF200", "냉장고 등록 성공");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}

