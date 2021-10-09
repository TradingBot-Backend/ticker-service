package com.tradingbot.tickerservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.tickerservice.movingaverage.service.MovingAverageService;
import com.tradingbot.tickerservice.ticker.domain.Ticker;
import com.tradingbot.tickerservice.ticker.service.TickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class TickerEventConsumer {
    private final TickerService tickerService;
    private final MovingAverageService movingAverageService;
    private final ObjectMapper objectMapper;
    private final ReactiveHashOperations<String, String, Ticker> reactiveHashOperations;

    @KafkaListener(topics = {"TICKER"},groupId = "current-ticker-redis-group")
    public void saveCurrentTickerRedis(String message) throws IOException{
        Mono.just(objectMapper.readValue(message,Ticker.class))
                .doOnNext(ticker -> reactiveHashOperations.put(ticker.getSymbol(),"TICKER", ticker).subscribe())
                .subscribe();
    }

    @KafkaListener(topics = {"TICKER"}, groupId = "current-ticker-mongo-group")
    public void saveCurrentTickerMongo(String message) throws IOException{
        tickerService.save(objectMapper.readValue(message,Ticker.class))
                .subscribe();
    }

    @KafkaListener(topics={"TICKER"}  , groupId="day-moving-average-group")
    public void loadDayMovingAverage(String message) throws IOException {
            movingAverageService.loadDayMovingAverage(objectMapper.readValue(message, Ticker.class));
    }

    @KafkaListener(topics={"TICKER"}  , groupId="hour-moving-average-group")
    public void loadHourMovingAverage(String message) throws IOException {
            movingAverageService.loadHourMovingAverage(objectMapper.readValue(message, Ticker.class));
    }

    @KafkaListener(topics={"TICKER"} , groupId="min-moving-average-group")
    public void loadMinMovingAverage(String message) throws IOException {
        movingAverageService.loadMinMovingAverage(objectMapper.readValue(message, Ticker.class));
    }
}
