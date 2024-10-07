package com.sm.project.web.dto.member;

import lombok.*;

import javax.validation.constraints.*;

public class MemberRequestDTO {

    @Getter
    @Setter
    public static class IssueTokenDTO {
        String refreshToken;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    public static class JoinDTO {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문, 숫자, 특수문자를 사용하세요.")
        String password;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(min = 2, max = 15)  //2~15자리
        String nickname;

        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        String phone;

        Boolean infoAgree;
        Boolean messageAgree;

        @NotBlank
        private String fcmToken;

        //인증번호
        @NotBlank(message = "인증번호는 필수 입력값입니다.")
        String certificationCode;
    }

    @Getter
    public static class NicknameDTO {
        @Size(min = 2, max = 15)  //2~15자리
        String nickname;
    }

    @Getter
    public static class FindEmailDTO {
        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        String phone;
        @NotBlank(message = "인증번호는 필수 입력값입니다.")
        String certificationCode;
    }

    @Getter
    @Builder
    public static class SmsDTO {
        private String phone;
    }

    @Getter
    @Builder
    public static class SendEmailDTO {
        @Email
        @NotEmpty(message = "이메일은 필수 입력값입니다.")
        private String email;
    }

    @Getter
    public static class FindPassword {
        @Email
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String email;

        @NotBlank(message = "인증코드는 필수 입력값입니다.")
        private String certificationCode;
    }

    @Getter
    @Builder
    public static class PasswordDTO {
        @Email
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String email;

        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문, 숫자, 특수문자를 사용하세요.")
        private String newPassword;
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문, 숫자, 특수문자를 사용하세요.")
        private String passwordCheck;
    }

    @Getter
    public static class ShareDTO{

        Long refrigeratorId;
        @Email
        private String eamil;
    }

}
