package com.sm.project.firebase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.FcmHandler;
import com.sm.project.feignClient.dto.FcmResponseDTO;
import com.sm.project.feignClient.fcm.FcmFeignClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * FcmService는 Firebase Cloud Messaging(FCM) 서비스를 통해 푸시 알림을 전송하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmService {

    private final ObjectMapper objectMapper;
    private final FcmFeignClient fcmFeignClient;
    private final Logger logger = LoggerFactory.getLogger(FcmService.class);

    /**
     * 푸시 알림 메시지를 생성하고 전송하는 메서드입니다.
     *
     * @param targetToken 대상 디바이스의 FCM 토큰
     * @param title 메시지 제목
     * @param body 메시지 내용
     * @throws IOException 예외 발생 시
     */
    @Transactional
    public void sendMessage(String targetToken, String title, String body) throws IOException {
        String aosMessage = makeMessage(targetToken, title, body);
        FcmResponseDTO fcmResponse = fcmFeignClient.getFCMResponse("Bearer " + getAccessToken(), aosMessage);
        logger.info("성공? : {}", fcmResponse);
        logger.info("보낸 메시지 : {}", aosMessage);
    }

    /**
     * FCM 메시지를 생성하는 메서드입니다.
     *
     * @param targetToken 대상 디바이스의 FCM 토큰
     * @param title 메시지 제목
     * @param body 메시지 내용
     * @return 생성된 FCM 메시지
     * @throws JsonProcessingException JSON 처리 예외 발생 시
     */
    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
            .message(FcmMessage.Message.builder()
                .token(targetToken)
                .notification(FcmMessage.Notification.builder()
                    .title(title)
                    .body(body)
                    .build())
                .build())
            .validateOnly(false)
            .build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    /**
     * Firebase 서비스 계정으로부터 액세스 토큰을 가져오는 메서드입니다.
     *
     * @return 액세스 토큰
     * @throws IOException 예외 발생 시
     */
    private String getAccessToken() throws IOException {
        String fireBaseConfigPath = "firebase/sm-project-firebase.json";
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(fireBaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new FcmHandler(ErrorStatus.FCM_REQUEST_TOKEN_ERROR);
        }
    }
}
