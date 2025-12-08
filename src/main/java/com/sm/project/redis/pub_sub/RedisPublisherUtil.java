package com.sm.project.redis.pub_sub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublisherUtil {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 메시지 발행
     * @param topic 메시지를 발행할 redis 채널
     * @param messageDto 발행할 메시지
     */
    public void publish(ChannelTopic topic, MessageDto messageDto) throws JsonProcessingException {
        //redis 채널에 메시지 발행
        String json = objectMapper.writeValueAsString(messageDto);  //dto를 직렬화
        redisTemplate.convertAndSend(topic.getTopic(), json);  //topic은 메시지를 보내고 받을 채널
        log.info("publish message 성공");
    }
}
