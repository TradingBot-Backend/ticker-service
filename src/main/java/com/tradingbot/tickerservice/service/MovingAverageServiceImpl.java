package com.tradingbot.tickerservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.tickerservice.domain.*;
import com.tradingbot.tickerservice.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovingAverageServiceImpl implements MovingAverageService, InitializingBean {
    private final TickerRepository tickerRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private HashOperations<String, String, String> hashOperations;

    @Override
    public void loadSecMovingAverage(Ticker ticker) {
        Arrays.stream(SecondMovingAverage.values())
                .forEach(seconds ->
                        tickerRepository
                                .findTickersBySymbolAndTimeTagIsAfter(ticker.getSymbol(),
                                        LocalDateTime.now().minusSeconds(seconds.getValue()))
                                .defaultIfEmpty(ticker)
                                .collect(Collectors.averagingDouble(Ticker::getClosePrice))
                                .doOnNext(value -> {
                                    hashOperations.put(ticker.getSymbol(), seconds.name(), value.toString());
                                    hashOperations.put(ticker.getSymbol(), seconds.name()+"_TIMETAG",ticker.getTimeTag().toString());
                                }).subscribe());
    }

    @Override
    public void loadMinMovingAverage(Ticker ticker) {
        Arrays.stream(MinuteMovingAverage.values())
                .forEach(minutes ->
                        tickerRepository
                                .findTickersBySymbolAndTimeTagIsAfter(ticker.getSymbol(),
                                        LocalDateTime.now().minusSeconds(minutes.getValue()))
                                .defaultIfEmpty(ticker)
                                .collect(Collectors.averagingDouble(Ticker::getClosePrice))
                                .doOnNext(value -> {
                                    hashOperations.put(ticker.getSymbol(), minutes.name(), value.toString());
                                    hashOperations.put(ticker.getSymbol(), minutes.name()+"_TIMETAG",ticker.getTimeTag().toString());
                                }).subscribe());
    }

    @Override
    public void loadHourMovingAverage(Ticker ticker) {
        Arrays.stream(HourMovingAverage.values())
                .forEach(hours ->
                        tickerRepository
                                .findTickersBySymbolAndTimeTagIsAfter(ticker.getSymbol(),
                                        LocalDateTime.now().minusSeconds(hours.getValue()))
                                .defaultIfEmpty(ticker)
                                .collect(Collectors.averagingDouble(Ticker::getClosePrice))
                                .doOnNext(value -> {
                                    hashOperations.put(ticker.getSymbol(), hours.name(), value.toString());
                                    hashOperations.put(ticker.getSymbol(), hours.name()+"_TIMETAG",ticker.getTimeTag().toString());
                                }).subscribe());
    }

    @Override
    public void loadDayMovingAverage(Ticker ticker) {
        Arrays.stream(DayMovingAverage.values())
                .forEach(days ->
                        tickerRepository
                                .findTickersBySymbolAndTimeTagIsAfter(ticker.getSymbol(),
                                        LocalDateTime.now().minusSeconds(days.getValue()))
                                .defaultIfEmpty(ticker)
                                .collect(Collectors.averagingDouble(Ticker::getClosePrice))
                                .doOnNext(value -> {
                                    hashOperations.put(ticker.getSymbol(), days.name(), value.toString());
                                    hashOperations.put(ticker.getSymbol(), days.name()+"_TIMETAG",ticker.getTimeTag().toString());
                                }).subscribe());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        hashOperations = redisTemplate.opsForHash();
    }
}
