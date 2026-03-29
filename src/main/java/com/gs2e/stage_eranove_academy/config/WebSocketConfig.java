package com.gs2e.stage_eranove_academy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Préfixe pour les messages sortants vers les clients
        config.enableSimpleBroker("/topic", "/queue");
        
        // Préfixe pour les messages entrants provenant des clients (si besoin d'écouter côté serveur)
        config.setApplicationDestinationPrefixes("/app");
        
        // Permet d'envoyer des messages spécifiques à un utilisateur (ex: /user/queue/alertes)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint pour la connexion au WebSocket depuis Angular
        registry.addEndpoint("/ws-ecarto")
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*") // Pour Angular
                .withSockJS(); // Fallback si WebSocket pas dispo
    }
}
