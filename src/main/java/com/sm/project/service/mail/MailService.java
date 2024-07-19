package com.sm.project.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * MailService는 이메일 전송 관련 기능을 제공하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Value("")
    private String url;

    /**
     * 비밀번호 재설정 인증 이메일을 전송하는 메서드입니다.
     * 
     * @param email 수신자 이메일 주소
     * @param certificationCode 인증 코드
     * @throws MessagingException 예외 발생 시
     * @throws UnsupportedEncodingException 예외 발생 시
     */
    public void sendResetPwdEmail(String email, String certificationCode)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();

        // 수신자 설정
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        // 이메일 제목 설정
        message.setSubject("[냉장고 해결사] 비밀번호 찾기 인증 메일입니다.");

        // 이메일 본문 내용 설정
        String content = "<div>"
                + "<p> 안녕하세요. 냉장고 해결사 입니다.<p>"
                + "<br>"
                + "<p> 아래 인증 코드를 앱에 입력하면 인증이 완료되고, " + email + " 계정의 비밀번호를 재설정 할 수 있습니다.<p>"
                + "<p> 새로운 비밀번호로 재설정 해주세요. <p>"
                + "<br>"
                + "<p> 인증코드: " + certificationCode + "<P>"
                + "<br>"
                + "<p> 감사합니다. <p>"
                + "</div>";

        // 발신자 설정
        message.setFrom(new InternetAddress(fromMail, "냉장고 해결사"));
        // 본문 내용 및 인코딩 설정
        message.setText(content, "utf-8", "html");

        // 이메일 전송
        mailSender.send(message);
    }
    
   // 인증 코드를 이메일로 발송하는 메서드
    public void sendVerificationCode(String email, String verificationCode) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("[서비스 이름] 인증 코드 발송");
        String content = "<div>"
                + "<p> 안녕하세요. 인증 코드가 발송되었습니다.<p>"
                + "<p> 인증코드: " + verificationCode + "<p>"
                + "</div>";
        message.setFrom(new InternetAddress(fromMail, "서비스 이름"));
        message.setText(content, "utf-8", "html");
        mailSender.send(message);
    }
    
}
