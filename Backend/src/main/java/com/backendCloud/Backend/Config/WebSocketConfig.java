package com.backendCloud.Backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    /**
     * This method is used to configure the message broker.
     * It enables a simple message broker and sets the application destination prefixes.
     * The simple broker is used to send messages to connected clients.
     * The application destination prefixes are used to indicate which endpoints are handled by the application.
     * In this case, it is "/topic" and "/app".
     * STOMP is used for sending and receiving messages. 
     * It is used over WebSocket.
     */

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

        /**
     * This method is used to configure the stomp endpoints.
     * It allows the '/chat' endpoint for stomp communication.
     * It also sets the allowed origins to 'http://localhost:3000'.
     * The withSockJS() method is used to enable SockJS support.
     */

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
    }
}
