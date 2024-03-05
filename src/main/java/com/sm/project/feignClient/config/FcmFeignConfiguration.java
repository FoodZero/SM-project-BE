package com.sm.project.feignClient.config;

import com.sm.project.feignClient.FeignClientException;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class FcmFeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return template -> template.header("Content-Type", "application/json;charset=UTF-8");
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
