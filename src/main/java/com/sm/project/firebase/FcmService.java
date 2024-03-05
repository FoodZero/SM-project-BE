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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmService{

    private String fcmUrl = "https://fcm.googleapis.com/v1/projects/zipdabang-android/messages:send";

    private final ObjectMapper objectMapper;

    private final FcmFeignClient fcmFeignClient;

    Logger logger = LoggerFactory.getLogger(FcmService.class);


    @Transactional
    public void sendMessage(String targetToken, String title, String body, String targetView, String targetPK, String targetNotification) throws IOException {
        String aosMessage = makeMessage(targetToken, title, body);

        FcmResponseDTO fcmResponse = fcmFeignClient.getFCMResponse("Bearer " + getAccessToken(),aosMessage);
        logger.info("성공? : {}",fcmResponse);
        logger.info("보낸 메세지 : {}",aosMessage);
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonParseException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(
                        FcmMessage.Message.builder()
                                .token(targetToken)
                                .notification(FcmMessage.Notification.builder()
                                        .title(title)
                                        .body(body).build())
                                .build())
                                        .validateOnly(false).build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException{
        String fireBaseConfigPath = "firebase/sm-project-firebase.json";

        try{
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
