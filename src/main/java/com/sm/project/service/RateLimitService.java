package com.sm.project.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RateLimitService는 API 요청에 대한 속도 제한을 관리하는 서비스 클래스입니다.
 * 클라이언트별로 요청을 제한하여 서비스의 안정성을 유지합니다.
 */
@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * HttpServletRequest에서 Host 헤더 값을 가져오는 메서드입니다.
     * 
     * @param httpServletRequest HttpServletRequest 객체
     * @return Host 헤더 값
     */
    private String getHost(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("Host");
    }

    /**
     * 클라이언트의 요청에 대한 Bucket을 반환하는 메서드입니다.
     * 
     * @param httpServletRequest HttpServletRequest 객체
     * @return 클라이언트의 Bucket 객체
     */
    public Bucket resolveBucket(HttpServletRequest httpServletRequest) {
        return cache.computeIfAbsent(getHost(httpServletRequest), this::newBucket);
    }

    /**
     * 새로운 Bucket을 생성하는 메서드입니다.
     * 
     * @param apiKey API 키
     * @return 새로 생성된 Bucket 객체
     */
    private Bucket newBucket(String apiKey) {
        return Bucket4j.builder()
                // 10개의 클라이언트가 10초에 10개씩 보낼 수 있는 대역폭
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(10))))
                .build();
    }
}
