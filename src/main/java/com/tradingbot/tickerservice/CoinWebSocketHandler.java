package com.tradingbot.tickerservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;

@Component
@EnableWebFlux
public class CoinWebSocketHandler implements WebSocketHandler {
    private Sinks.Many<String> tickerSink = Sinks.many().multicast().directBestEffort();

    @KafkaListener(topics={"BTC_KRW","ETH_KRW","XLM_KRW","XRP_KRW","LTC_KRW"
            ,"EOS_KRW", "ADA_KRW", "TRX_KRW", "LINK_KRW","BCH_KRW"} , groupId="ws-group")
    public void consume(String message) throws IOException {
        this.tickerSink.tryEmitNext(message);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(this.tickerSink.asFlux().map(session::textMessage).log());
    }
}
