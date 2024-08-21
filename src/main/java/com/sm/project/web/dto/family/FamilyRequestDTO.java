package com.sm.project.web.dto.family;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * FamilyRequestDTO 클래스는 가족과 관련된 요청 데이터 전송 객체(DTO)를 정의합니다.
 */
public class FamilyRequestDTO {

    /**
     * EmailRequestDTO 클래스는 이메일 요청 데이터를 담고 있습니다.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailRequestDTO {
        /**
         * 이메일 주소 필드로, 유효한 이메일 형식이어야 하며 비어 있을 수 없습니다.
         */
        @Email
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String email;
    }

    /**
     * VerificationDTO 클래스는 이메일 인증 요청 데이터를 담고 있습니다.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VerificationDTO {
        /**
         * 이메일 주소 필드로, 유효한 이메일 형식이어야 하며 비어 있을 수 없습니다.
         */
        @Email
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String email;

        /**
         * 인증번호 필드로, 비어 있을 수 없습니다.
         */
        @NotBlank(message = "인증번호는 필수 입력값입니다.")
        private String verificationCode;
    }
}