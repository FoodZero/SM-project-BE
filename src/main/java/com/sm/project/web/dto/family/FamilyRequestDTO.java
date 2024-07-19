package com.sm.project.web.dto.family;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class FamilyRequestDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailRequestDTO {
        @Email
        @NotBlank
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VerificationDTO {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String verificationCode;
    }
}
