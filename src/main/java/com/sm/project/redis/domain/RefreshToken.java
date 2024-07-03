package com.sm.project.redis.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

/**
 * RefreshToken은 리프레시 토큰을 관리하는 Redis 엔티티 클래스입니다.
 * token, memberId, expireTime을 저장하며, TTL(Time To Live)은 180000초로 설정됩니다.
 */
@RedisHash(value = "refreshToken", timeToLive = 180000)
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private String token;

    private Long memberId;

    private LocalDateTime expireTime;
}
