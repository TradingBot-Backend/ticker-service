package com.tradingbot.tickerservice.config;

import com.tradingbot.tickerservice.CoinWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@RequiredArgsConstructor
@Configuration
public class WebSocketConfig {
    private final CoinWebSocketHandler coinWebSocketHandler;

    @Bean
    public HandlerMapping handlerMapping(){
        Map<String, WebSocketHandler> map = new HashMap<>();
        //Lambda로 바꾸기
        map = Collections.singletonMap("/coins", coinWebSocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.initApplicationContext();
        mapping.setOrder(10);
        mapping.setUrlMap(map);
        return mapping;
    }

    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }
}
