package com.sm.project.feignClient.config;

import com.sm.project.feignClient.FeignClientException;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class LambdaFeignConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientException();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
