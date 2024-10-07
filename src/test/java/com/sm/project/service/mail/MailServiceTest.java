package com.sm.project.service.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    private String email;
    private String certificationCode;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        certificationCode = "123456";
    }


    @Test
    public void testSendResetPwdEmail() throws MessagingException, UnsupportedEncodingException {
        //given
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        //when
        mailService.sendResetPwdEmail(email, certificationCode);

        //then
        verify(mailSender, times(1)).send(mimeMessage);
        Mockito.verify(mimeMessage, times(1)).setSubject("[냉장고 해결사] 비밀번호 찾기 인증 메일입니다.");
    }

    @Test
    public void testSendVerificationCode() throws MessagingException, UnsupportedEncodingException {
        //given
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        //when
        mailService.sendVerificationCode(email, certificationCode);

        //then
        verify(mailSender, times(1)).send(mimeMessage);
        Mockito.verify(mimeMessage, times(1)).setSubject("[FoodZero] 인증 코드 발송");
    }
}
