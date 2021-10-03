package com.tradingbot.tickerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
@Configuration
public class WebSocketConfig {
    @Bean
    public HttpClient httpClient() { return HttpClient.newBuilder().build(); }
    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }
}
