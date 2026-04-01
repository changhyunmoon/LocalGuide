package com.team6.module.chat.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration // 주석을 해제하여 빈 설정을 활성화합니다.
public class RedisConfig {

    // 채팅 메시지를 주고받을 공통 채널명
    public static final String CHAT_CHANNEL = "chat_room_topic";

    /**
     * 에러의 원인이었던 RedisTemplate 빈을 등록합니다.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // JSON 직렬화 설정 (객체를 JSON 형태로 저장)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    /**
     * Redis Pub/Sub 메시지를 처리하는 리스너 컨테이너
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            //MessageListenerAdapter listenerAdapter,
            ChannelTopic channelTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //container.addMessageListener(listenerAdapter, channelTopic);
        return container;
    }

    /**
     * 메시지가 왔을 때 실행될 리스너 어댑터 설정
     */
//    @Bean
//    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
//        // RedisSubscriber 클래스의 "onMessage" 메서드를 호출하도록 설정
//        return new MessageListenerAdapter(subscriber, "onMessage");
//    }

    /**
     * Pub/Sub에서 사용할 토픽(채널) 정의
     */
    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(CHAT_CHANNEL);
    }
}
