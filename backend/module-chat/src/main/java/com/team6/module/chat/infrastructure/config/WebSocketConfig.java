package com.team6.module.chat.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 메시징을 활성화합니다.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. 클라이언트(React)가 최초 연결을 시도할 엔드포인트
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*") // 테스트 시 모든 도메인 허용 (운영 시 제한 필요)
                .withSockJS(); // SockJS 지원 (낮은 버전의 브라우저 대응)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 2. 메시지를 구독(수신)하는 경로 설정
        // 예: /sub/chat/room/1 -> 1번 방 메시지를 기다림
        registry.enableSimpleBroker("/sub");

        // 3. 메시지를 발행(전송)하는 경로 설정
        // 예: /pub/chat/message -> 서버의 @MessageMapping으로 전달됨
        registry.setApplicationDestinationPrefixes("/pub");
    }


}
