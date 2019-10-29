package com.omd.ws.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class ComponentConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/queue");
    }

    @Bean
    public BatchedMessageSender<TaskReport> taskMessageSender(SimpMessagingTemplate messagingTemplate) {
        return new BatchedMessageSender<>(1, "/topic/task", messagingTemplate);
    }

    @Bean
    public TaskService taskService(BatchedMessageSender<TaskReport> messageSender) {
        return new SimpleTaskService(10, 10, messageSender);
    }

    @Bean
    public Controller controller(TaskService taskService) {
        return new Controller(taskService);
    }
}
