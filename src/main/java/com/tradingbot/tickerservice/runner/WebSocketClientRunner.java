package com.tradingbot.tickerservice.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.tickerservice.ticker.Coin;
import com.tradingbot.tickerservice.ticker.Ticker;
import com.tradingbot.tickerservice.ticker.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class WebSocketClientRunner implements CommandLineRunner {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final WebSocketClient client;
    private final TickerRepository tickerRepository;

    @Override
    public void run(String... args) {
        tickerRepository.deleteAll().subscribe();
        client.execute(
                URI.create("wss://pubwss.bithumb.com/pub/ws"),
                session -> session.send(
                                Mono.just(session.textMessage("{'type':'ticker', " +
                                        "'symbols': ['" + Coin.BTC + "_KRW','" + Coin.ETH + "_KRW','" + Coin.XLM + "_KRW','" + Coin.XRP + "_KRW','" + Coin.LTC + "_KRW','" + Coin.EOS + "_KRW','" + Coin.ADA + "_KRW','" + Coin.TRX + "_KRW','" + Coin.LINK + "_KRW','" + Coin.BCH + "_KRW']" +
                                        ",'tickTypes':['30M']}")))
                        .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText))
                        //status 오류(connection 오류) 있을 때 체크, 값이 들어오지 않는 것 체크 (2가지 경우 있음)
                        .filter(text -> text.startsWith("{\"type\":\"ticker\""))
                        .map(message -> {
                            //error handling => reactive 한 throw 수행하게 Refactoring 필요, 아님 scheduler 이용하여 별도 스레드 부여
                            try {
                                return objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                        .readValue(objectMapper.readTree(message).get("content").toString(), Ticker.class);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        //kafka producer, mongodb 데이터 받는 부분 2개로 나누기
                        //kafka error handling -> 카프카 서버가 다운되었을때?
                        .doOnNext(tickers -> {
                            try {
                                kafkaTemplate.send(tickers.getSymbol(), objectMapper.writeValueAsString(tickers));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        })
                        .log().doOnNext(ticker -> tickerRepository.save(ticker).subscribe())
                        .then()).subscribe();
    }
}
