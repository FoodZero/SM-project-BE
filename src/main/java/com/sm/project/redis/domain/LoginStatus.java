package com.sm.project.redis.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * LoginStatus는 로그인 상태를 관리하는 Redis 엔티티 클래스입니다.
 * accessToken과 memberId를 저장하며, TTL(Time To Live)은 1830초로 설정됩니다.
 */
@RedisHash(value = "loginStatus", timeToLive = 1830)
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginStatus {

    @Id
    private String accessToken;

    private Long memberId;
}
