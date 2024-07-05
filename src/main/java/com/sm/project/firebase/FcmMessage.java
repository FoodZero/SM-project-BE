package com.sm.project.firebase;

import lombok.*;

/**
 * FcmMessage는 FCM(Firebase Cloud Messaging) 메시지 전송에 사용되는 클래스입니다.
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmMessage {

    private boolean validateOnly;
    private Message message;

    /**
     * Message 클래스는 FCM 메시지의 내용을 나타냅니다.
     */
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Message {
        private Notification notification;
        private String token;
    }

    /**
     * Notification 클래스는 FCM 메시지의 알림 내용을 나타냅니다.
     */
    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Notification {
        private String title;
        private String body;
    }
}
