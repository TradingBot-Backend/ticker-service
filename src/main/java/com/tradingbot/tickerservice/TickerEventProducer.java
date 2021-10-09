package com.tradingbot.tickerservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.tickerservice.movingaverage.service.MovingAverageService;
import com.tradingbot.tickerservice.ticker.domain.Symbol;
import com.tradingbot.tickerservice.ticker.domain.Ticker;
import com.tradingbot.tickerservice.ticker.service.TickerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.KafkaSender;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class TickerEventProducer implements CommandLineRunner {
    private final KafkaTemplate<String, Ticker> tickerKafkaTemplate;
    private final ObjectMapper objectMapper;
    private final WebSocketClient client;

    @Override
    public void run(String... args) throws Exception {
        client.execute(
                        URI.create("wss://pubwss.bithumb.com/pub/ws"),
                        session -> session.send(
                                        Mono.just(session.textMessage("{'type':'ticker', " +
                                                "'symbols': ['" + Symbol.BTC_KRW + "','" + Symbol.ETH_KRW + "','" + Symbol.XLM_KRW + "','" + Symbol.XRP_KRW + "','" + Symbol.LTC_KRW + "','" + Symbol.EOS_KRW + "','" + Symbol.ADA_KRW + "','" + Symbol.TRX_KRW + "','" + Symbol.LINK_KRW + "','" + Symbol.BCH_KRW + "']" +
                                                ",'tickTypes':['30M']}")))
                                .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText))
                                .filter(text -> text.startsWith("{\"type\":\"ticker\""))
                                .map(text -> text.replaceAll("_KRW", ""))
                                .map(message -> {
                                    try {
                                        return objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                                .readValue(objectMapper.readTree(message).get("content").toString(), Ticker.class);
                                    } catch (JsonProcessingException e) {
                                        throw Exceptions.propagate(e);
                                    }
                                })
                                .doOnNext(ticker -> tickerKafkaTemplate.send("TICKER", ticker))
                                .doOnNext(ticker -> tickerKafkaTemplate.send(ticker.getSymbol(),ticker))
                                .then())
                .repeat().log().subscribe();
    }
}
