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
        message.setSubject("[FoodZero] 인증 코드 발송");
        // String content = "<div>"
        //         + "<p> 안녕하세요. 인증 코드가 발송되었습니다.<p>"
        //         + "<p> 인증코드: " + verificationCode + "<p>"
        //         + "</div>";
        String content = "<div style=\"width: 50%; margin: 0 auto;\"><td style=\"vertical-align: top; padding: 0\">\r\n" + //
                        "    <center>\r\n" + //
                        "        <table id=\"m_2994185794324935526body\" style=\"border: 0; border-collapse: collapse; margin: 0 auto; background: white; border-radius: 8px; margin-bottom: 16px\">\r\n" + //
                        "            <tbody>\r\n" + //
                        "                <tr>\r\n" + //
                        "                    <td style=\"width: 546px; vertical-align: top; padding-top: 32px\">\r\n" + //
                        "                        <div style=\"max-width: 600px; margin: 0 auto\">\r\n" + //
                        "                            <div style=\"margin-left: 50px; margin-right: 50px; margin-bottom: 72px; margin-bottom: 30px\" class=\"m_2994185794324935526lg_margin_left_right m_2994185794324935526xl_margin_bottom\">\r\n" + //
                        "                                <div style=\"margin-top: 18px\" class=\"m_2994185794324935526slack_logo_style\">\r\n" + //
                        "                                    <!-- <img\r\n" + //
                        "                                        width=\"120\"\r\n" + //
                        "                                        height=\"36\"\r\n" + //
                        "                                        style=\"margin-top: 0; margin-right: 0; margin-bottom: 32px; margin-left: 0px\"\r\n" + //
                        "                                        src=\"\"\r\n" + //
                        "                                        alt=\"Slack 로고\"\r\n" + //
                        "                                        class=\"CToWUd\"\r\n" + //
                        "                                        data-bit=\"iit\"\r\n" + //
                        "                                    /> -->\r\n" + //
                        "                                    <h2>FoodZero</h2>\r\n" + //
                        "                                </div>\r\n" + //
                        "                                <h1>인증 번호</h1>\r\n" + //
                        "                                <p style=\"font-size: 20px; line-height: 28px; letter-spacing: -0.2px; margin-bottom: 28px; word-break: break-word\" class=\"m_2994185794324935526hero_paragraph\">고객님의 인증 번호는 아래에 있습니다.</p>\r\n" + //
                        "                            </div>\r\n" + //
                        "                            <div style=\"background: #f5f4f5; border-radius: 4px; padding: 43px 23px; margin-left: 50px; margin-right: 50px; margin-bottom: 72px; margin-bottom: 30px\" class=\"m_2994185794324935526lg_margin_left_right m_2994185794324935526xl_margin_bottom m_2994185794324935526grey_box_container\">\r\n" + //
                        "                                <div style=\"text-align: center; vertical-align: middle; font-size: 30px\">" + verificationCode + "</div>\r\n" + //
                        "                            </div>\r\n" + //
                        "                            <div style=\"margin-left: 50px; margin-right: 50px; margin-bottom: 72px; margin-bottom: 30px\" class=\"m_2994185794324935526lg_margin_left_right m_2994185794324935526xl_margin_bottom\">\r\n" + //
                        "                                <p style=\"font-size: 16px; line-height: 24px; letter-spacing: -0.2px; margin-bottom: 28px\" class=\"m_2994185794324935526content_paragraph\"></p>\r\n" + //
                        "                            </div>\r\n" + //
                        "                        </div>\r\n" + //
                        "                    </td>\r\n" + //
                        "                </tr>\r\n" + //
                        "            </tbody>\r\n" + //
                        "        </table>\r\n" + //
                        "    </center>\r\n" + //
                        "</td></div>\r\n" + //
                        "";
        
        
        message.setFrom(new InternetAddress(fromMail, "서비스 이름"));
        message.setText(content, "utf-8", "html");
        mailSender.send(message);
    }
    
}
