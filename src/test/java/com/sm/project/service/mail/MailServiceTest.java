package com.sm.project.service.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

import static org.mockito.Mockito.*;

class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendResetPwdEmail() throws MessagingException, UnsupportedEncodingException {
        // given
        String toEmail = "test@example.com";
        String certificationCode = "123456";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // when
        mailService.sendResetPwdEmail(toEmail, certificationCode);

        // then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage capturedMessage = mimeMessageCaptor.getValue();

        // Verify the recipient
        verify(capturedMessage).addRecipients(MimeMessage.RecipientType.TO, toEmail);
        // Verify the subject
        verify(capturedMessage).setSubject("[냉장고 해결사] 비밀번호 찾기 인증 메일입니다.");
        // Verify the content
        String expectedContent = "<div>"
                + "<p> 안녕하세요. 냉장고 해결사 입니다.<p>"
                + "<br>"
                + "<p> 아래 인증 코드를 앱에 입력하면 인증이 완료되고, " + toEmail + " 계정의 비밀번호를 재설정 할 수 있습니다.<p>"
                + "<p> 새로운 비밀번호로 재설정 해주세요. <p>"
                + "<br>"
                + "<p> 인증코드: " + certificationCode + "<P>"
                + "<br>"
                + "<p> 감사합니다. <p>"
                + "</div>";
        verify(capturedMessage).setText(expectedContent, "utf-8", "html");
        // Verify the sender
        verify(capturedMessage).setFrom(new InternetAddress("test@example.com", "냉장고 해결사"));
    }
}
