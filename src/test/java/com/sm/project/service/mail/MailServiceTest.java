package com.sm.project.service.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        // MimeMessage 객체를 Mock 객체로 설정
        mimeMessage = mock(MimeMessage.class);
        // JavaMailSender가 MimeMessage 객체를 생성할 때, Mock된 mimeMessage를 반환하도록 설정
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 전송 테스트")
    void sendResetPwdEmail_Success() throws MessagingException, UnsupportedEncodingException {
        // Given
        String email = "test@example.com";
        String certificationCode = "123456";

        // When
        mailService.sendResetPwdEmail(email, certificationCode);

        // Then
        // MimeMessage가 올바르게 설정되었는지 확인
        verify(mimeMessage, times(1)).addRecipients(eq(MimeMessage.RecipientType.TO), eq(email));
        verify(mimeMessage, times(1)).setSubject(eq("[냉장고 해결사] 비밀번호 찾기 인증 메일입니다."));
        verify(mimeMessage, times(1)).setFrom(any(InternetAddress.class));
        verify(mimeMessage, times(1)).setText(anyString(), eq("utf-8"), eq("html"));

        // 메일이 전송되었는지 확인
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("인증 코드 이메일 전송 테스트")
    void sendVerificationCode_Success() throws MessagingException, UnsupportedEncodingException {
        // Given
        String email = "test@example.com";
        String verificationCode = "654321";

        // When
        mailService.sendVerificationCode(email, verificationCode);

        // Then
        // MimeMessage가 올바르게 설정되었는지 확인
        verify(mimeMessage, times(1)).addRecipients(eq(MimeMessage.RecipientType.TO), eq(email));
        verify(mimeMessage, times(1)).setSubject(eq("[FoodZero] 인증 코드 발송"));
        verify(mimeMessage, times(1)).setFrom(any(InternetAddress.class));
        verify(mimeMessage, times(1)).setText(anyString(), eq("utf-8"), eq("html"));

        // 메일이 전송되었는지 확인
        verify(mailSender, times(1)).send(mimeMessage);
    }
}
