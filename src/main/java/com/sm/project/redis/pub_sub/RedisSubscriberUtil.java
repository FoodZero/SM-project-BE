package com.sm.project.redis.pub_sub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.project.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriberUtil implements MessageListener {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final MailService mailService;

    /**
     * 메시지 수신 시 호출되는 메서드
     * @param message message must not be {@literal null}. 수신된 redis 메시지
     * @param pattern pattern matching the channel (if specified) - can be {@literal null}. 메시지가 수신된 패턴
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());  //역직렬화
            MessageDto messageDto = objectMapper.readValue(publishMessage, MessageDto.class);  //JSON 역직렬화
            log.info("Redis SUB Message : {}", publishMessage);

            //메일 전송
            mailService.sendResetPwdEmail(messageDto.getEmail(), messageDto.getVerificationCode());

        } catch (JsonProcessingException | MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
