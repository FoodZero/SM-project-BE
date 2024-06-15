package com.sm.project.apiPayload.code.status;

import com.sm.project.apiPayload.code.BaseErrorCode;
import com.sm.project.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    //Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "해당 회원을 찾을 수 없습니다."),
    MEMBER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4002", "해당 이메일이 존재하지 않습니다."),
    MEMBER_PASSWORD_ERROR(HttpStatus.NOT_FOUND, "MEMBER4003", "비밀번호가 틀렸습니다."),
    MEMBER_ALREADY_JOIN(HttpStatus.BAD_REQUEST, "MEMBER4004", "이미 가입된 회원입니다."),
    MEMBER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER4005", "재설정한 비밀번호가 서로 다릅니다."),
    MEMBER_VERIFY_FAILURE(HttpStatus.BAD_REQUEST,"MEMBER4006", "인증번호가 일치하지 않습니다."),
    MEMBER_WRONG_RESET_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER4007", "재설정 토큰이 올바르지 않습니다."),
    MEMBER_NICKNAME_DUPLICATE(HttpStatus.BAD_REQUEST, "MEMBER4008", "이미 존재하는 닉네임입니다."),

    //Refrigerator
    RERFIGERATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "REF4001", "냉장고를 찾을 수 없습니다."),

    //Food
    FOOD_NOT_FOUND(HttpStatus.NOT_FOUND, "FOOD4001","음식을 찾을 수 없습니다."),

    //JWT

    JWT_BAD_REQUEST(HttpStatus.UNAUTHORIZED, "JWT4001","잘못된 JWT 서명입니다."),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4002","액세스 토큰이 만료되었습니다."),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4003","리프레시 토큰이 만료되었습니다. 다시 로그인하시기 바랍니다."),
    JWT_UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT4004","지원하지 않는 JWT 토큰입니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT4005","유효한 JWT 토큰이 없습니다."),

    //FeignClient
    FEIGN_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5001", "Inter server Error in feign client"),

    //Fcm
    FCM_REQUEST_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM4001", "구글 request 토큰 오류"),

    //Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4001", "해당 포스트를 찾을 수 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4002", "해당 위치는 존재하지 않습니다."),

    //Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT4001", "해당 댓글을 찾을 수 없습니다."),
    COMMENT_NOT_OWNED(HttpStatus.BAD_REQUEST, "COMMENT4002", "자신이 작성한 댓글만 수정할 수 있습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
