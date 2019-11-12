package com.omd.ws.task;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
@ConfigurationProperties(prefix = "ws.task")
@ComponentScan(basePackages = "com.omd.ws.task")
public class WsTaskConfiguration implements WebSocketMessageBrokerConfigurer {

    private boolean configWebsocketBroker = true;
    private String taskMessageDestination;
    private int messagesPerSecond = 1;
    private long minsToKeepErroredTasks = 10;
    private int threadCount = 10;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        if(configBroker()) {
            registry.addEndpoint("/stomp").setAllowedOrigins("*");
        }
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        if(configBroker()) {
            config.enableSimpleBroker("/topic", "/queue");
        }
    }

    @Bean
    public BatchedMessageSender<TaskReport> wsTaskMessageSender(SimpMessagingTemplate messagingTemplate) {
        String destination = configBroker() ? "/topic/task" : taskMessageDestination;
        return new BatchedMessageSender<>(messagesPerSecond, destination, messagingTemplate);
    }

    @Bean
    public TaskService wsTaskService(BatchedMessageSender<TaskReport> messageSender) {
        return new SimpleTaskService(minsToKeepErroredTasks, threadCount, messageSender);
    }


    private boolean configBroker() {
        if(configWebsocketBroker && taskMessageDestination != null) {
            throw new RuntimeException("You cannot supply a task message destination when config websocket broker is set to true");
        } else if (!configWebsocketBroker && taskMessageDestination == null) {
            throw new RuntimeException("You must supply a task message destination when config websocket broker is set to false");
        }
        return configWebsocketBroker;
    }

    public void setConfigWebsocketBroker(boolean configWebsocketBroker) {
        this.configWebsocketBroker = configWebsocketBroker;
    }

    public void setTaskMessageDestination(String taskMessageDestination) {
        this.taskMessageDestination = taskMessageDestination;
    }

    public void setMessagesPerSecond(int messagesPerSecond) {
        this.messagesPerSecond = messagesPerSecond;
    }

    public void setMinsToKeepErroredTasks(long minsToKeepErroredTasks) {
        this.minsToKeepErroredTasks = minsToKeepErroredTasks;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}
