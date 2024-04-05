package com.sm.project.feignClient.config;

import com.sm.project.feignClient.FeignClientException;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class NaverOCRFeignConfiguration {

    @Value("${naver.ocr.secret}")
    private String secret;
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-OCR-SECRET", secret);
            requestTemplate.header("Content-Type", "application/json");
        };
    }
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientException();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
