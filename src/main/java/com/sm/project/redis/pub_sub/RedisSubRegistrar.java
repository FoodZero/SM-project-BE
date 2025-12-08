package com.sm.project.redis.pub_sub;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubRegistrar { 
    private final RedisMessageListenerContainer container;
    private final RedisSubscriberUtil subscriber;

    @Value("${spring.redis.channel}")
    private String channel;
    
    //순환참조 때문에 따로 채널 구독
    @EventListener(ApplicationReadyEvent.class)
    public void register() {
        container.addMessageListener(subscriber, new ChannelTopic(channel));
    }
}
