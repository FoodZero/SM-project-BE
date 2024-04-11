package com.sm.project.feignClient.fcm;

import com.sm.project.feignClient.config.FcmFeignConfiguration;
import com.sm.project.feignClient.dto.FcmResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "FcmFeignClient", url = "https://fcm.googleapis.com", configuration = FcmFeignConfiguration.class)
@Component
public interface FcmFeignClient {


    @PostMapping("/v1/projects/sm-project-ea2a9/messages:send")
    FcmResponseDTO getFCMResponse(@RequestHeader("Authorization") String token, @RequestBody String fcmMessage);
}
