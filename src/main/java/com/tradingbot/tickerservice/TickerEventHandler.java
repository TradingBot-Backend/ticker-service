package com.tradingbot.tickerservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.tickerservice.domain.MovingAverage;
import com.tradingbot.tickerservice.domain.Symbol;
import com.tradingbot.tickerservice.domain.Ticker;
import com.tradingbot.tickerservice.service.MovingAverageService;
import com.tradingbot.tickerservice.service.TickerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Arrays;
@Slf4j
@Component
@RequiredArgsConstructor
public class TickerEventHandler implements CommandLineRunner, InitializingBean {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final WebSocketClient client;
    private final TickerService tickerService;
    private final MovingAverageService movingAverageService;
    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;

    @Override
    public void afterPropertiesSet() throws Exception {
        tickerService.deleteAll().subscribe();
        WebClient client = WebClient.create();
        Arrays.stream(Symbol.values())
                .distinct()
                .map(i -> new StringBuffer("https://api.upbit.com/v1/candles/days?count=200&market=KRW-")
                        .append(i.toString().substring(0, i.toString().indexOf("_"))).toString())
                .forEach(uri -> client.get().uri(uri).retrieve().bodyToFlux(String.class).log()
                        .map(response ->
                                response.replaceAll("market", "symbol")
                                        .replaceAll("KRW-", "")
                                        .replaceAll("opening_price", "openPrice")
                                        .replaceAll("trade_price", "closePrice")
                                        .replaceAll("high_price", "highPrice")
                                        .replaceAll("low_price", "lowPrice")
                                        .replaceAll("candle_acc_trade_price", "")
                                        .replaceAll("candle_acc_trade_volume", "")
                                        .replaceAll("prev_closing_price", "prevClosePrice")
                                        .replaceAll("candle_date_time_kst", "timeTag")
                                        .replaceAll("change_price", "chgAmt")
                                        .replaceAll("change_rate", "chgRate")
                                        .replaceAll(":00:00", ":00:00.000"))
                        .map(response -> {
                            try {
                                return objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                        .readValue(response, Ticker[].class);
                            } catch (JsonProcessingException e) {
                                throw Exceptions.propagate(e);
                            }})
                        .flatMap(i -> Flux.fromArray(i))
                        .doOnNext(ticker -> tickerService.save(ticker).subscribe())
                        .subscribe());
        hashOperations = redisTemplate.opsForHash();
    }
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
                                .map(text-> text.replaceAll("_KRW", ""))
                                .map(message -> {
                                    try {
                                        return objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                                .readValue(objectMapper.readTree(message).get("content").toString(), Ticker.class);
                                    } catch (JsonProcessingException e) {
                                        throw Exceptions.propagate(e);
                                    }
                                })
                                .doOnNext(tickers -> {
                                    try {
                                        kafkaTemplate.send(tickers.getSymbol(), objectMapper.writeValueAsString(tickers));
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                })
                                .doOnNext(ticker -> tickerService.save(ticker).subscribe())
                                .doOnNext(ticker -> movingAverageService.findDayMovingAverage(ticker.getSymbol(), MovingAverage.DMA5.getValue())
                                        .doOnNext(value -> hashOperations
                                                .put(ticker.getSymbol(),"5DMA",value.toString()))
                                        .subscribe())
                                .doOnNext(ticker -> hashOperations.put(ticker.getSymbol(),"5DMA_TIMETAG",ticker.getTimeTag().toString()))
                                .doOnNext(ticker -> movingAverageService.findDayMovingAverage(ticker.getSymbol(), MovingAverage.DMA20.getValue())
                                        .doOnNext(value -> hashOperations
                                                .put(ticker.getSymbol(),"20DMA",value.toString()))
                                        .subscribe())
                                .doOnNext(ticker -> hashOperations.put(ticker.getSymbol(),"20DMA_TIMETAG",ticker.getTimeTag().toString()))
                                .doOnNext(ticker -> movingAverageService.findDayMovingAverage(ticker.getSymbol(), MovingAverage.DMA60.getValue())
                                        .doOnNext(value -> hashOperations
                                                .put(ticker.getSymbol(),"60DMA",value.toString()))
                                        .subscribe())
                                .doOnNext(ticker -> hashOperations.put(ticker.getSymbol(),"60DMA_TIMETAG",ticker.getTimeTag().toString()))
                                .doOnNext(ticker -> movingAverageService.findDayMovingAverage(ticker.getSymbol(), MovingAverage.DMA120.getValue())
                                        .doOnNext(value -> hashOperations
                                                .put(ticker.getSymbol(),"120DMA",value.toString()))
                                        .subscribe())
                                .doOnNext(ticker -> hashOperations.put(ticker.getSymbol(),"120DMA_TIMETAG",ticker.getTimeTag().toString()))
                                .doOnNext(ticker -> tickerService.deleteOldData(180).subscribe())
                                .then()).log()
                .repeat().log().subscribe();
    }

}
