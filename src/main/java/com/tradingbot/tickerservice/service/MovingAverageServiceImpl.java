package com.tradingbot.tickerservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.tickerservice.domain.*;
import com.tradingbot.tickerservice.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
@Service
@RequiredArgsConstructor
public class MovingAverageServiceImpl implements MovingAverageService, InitializingBean {
    private final TickerRepository tickerRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private HashOperations<String, String, String> hashOperations;

    @Override
    public void loadMinMovingAverage(Ticker ticker) {
        Arrays.stream(MinuteMovingAverage.values())
                .forEach(minutes ->
                        reactiveMongoTemplate.aggregate(
                                Aggregation.newAggregation(
                                        Ticker.class,
                                        match(where("symbol")
                                                .is(ticker.getSymbol())
                                                .and("timeTag")
                                                .gte(LocalDateTime.now().minusMinutes(minutes.getValue()))),
                                        group("$symbol")
                                                .avg("$closePrice")
                                                .as("average")),
                                MovingAverage.class)
                                        .doOnNext(value -> {
                                            hashOperations.put(ticker.getSymbol(), minutes.name(), value.toString());
                                            hashOperations.put(ticker.getSymbol(), minutes.name()+"_TIMETAG",ticker.getTimeTag().toString());
                                        }).subscribe());
    }

    @Override
    public void loadHourMovingAverage(Ticker ticker) {
        Arrays.stream(HourMovingAverage.values())
                .forEach(hours ->
                        reactiveMongoTemplate.aggregate(
                                        Aggregation.newAggregation(
                                                Ticker.class,
                                                match(where("symbol")
                                                        .is(ticker.getSymbol())
                                                        .and("timeTag")
                                                        .gte(LocalDateTime.now().minusHours(hours.getValue()))),
                                                group("$symbol")
                                                        .avg("$closePrice")
                                                        .as("average")),
                                        MovingAverage.class)
                                .doOnNext(value -> {
                                    hashOperations.put(ticker.getSymbol(), hours.name(), value.toString());
                                    hashOperations.put(ticker.getSymbol(), hours.name()+"_TIMETAG",ticker.getTimeTag().toString());
                                }).subscribe());
    }

    @Override
    public void loadDayMovingAverage(Ticker ticker) {
        Arrays.stream(DayMovingAverage.values())
                .forEach(days ->
                        reactiveMongoTemplate.aggregate(
                                        Aggregation.newAggregation(
                                                Ticker.class,
                                                match(where("symbol")
                                                        .is(ticker.getSymbol())
                                                        .and("timeTag")
                                                        .gte(LocalDateTime.now().minusHours(days.getValue()))),
                                                group("$symbol")
                                                        .avg("$closePrice")
                                                        .as("average")),
                                        MovingAverage.class)
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
