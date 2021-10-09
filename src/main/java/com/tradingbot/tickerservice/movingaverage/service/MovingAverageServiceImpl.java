package com.tradingbot.tickerservice.movingaverage.service;

import com.tradingbot.tickerservice.ticker.domain.Ticker;
import com.tradingbot.tickerservice.movingaverage.domain.DayMovingAverage;
import com.tradingbot.tickerservice.movingaverage.domain.HourMovingAverage;
import com.tradingbot.tickerservice.movingaverage.domain.MinuteMovingAverage;
import com.tradingbot.tickerservice.movingaverage.domain.MovingAverage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
public class MovingAverageServiceImpl implements MovingAverageService {
    private final ReactiveHashOperations<String, String, String> reactiveHashOperations;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

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
                                .doOnNext(value ->
                                        reactiveHashOperations
                                                .put(ticker.getSymbol(),
                                                        minutes.name(),
                                                        Double.toString(value.getAverage()))
                                                .thenMany(reactiveHashOperations
                                                        .put(ticker.getSymbol(),
                                                                minutes.name()+"_TIMETAG",
                                                                ticker.getTimeTag().toString()))
                                                .subscribe())
                                .log().subscribe());
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
                                .doOnNext(value ->
                                        reactiveHashOperations
                                                .put(ticker.getSymbol(),
                                                        hours.name(),
                                                        Double.toString(value.getAverage()))
                                                .thenMany(reactiveHashOperations
                                                        .put(ticker.getSymbol(),
                                                                hours.name()+"_TIMETAG",
                                                                ticker.getTimeTag().toString()))
                                                .subscribe())
                                .log().subscribe());
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
                                                        .gte(LocalDateTime.now().minusDays(days.getValue()))),
                                                group("$symbol")
                                                        .avg("$closePrice")
                                                        .as("average")),
                                        MovingAverage.class)
                                .doOnNext(value ->
                                        reactiveHashOperations
                                                .put(ticker.getSymbol(),
                                                        days.name(),
                                                        Double.toString(value.getAverage()))
                                                .thenMany(reactiveHashOperations
                                                        .put(ticker.getSymbol(),
                                                                days.name()+"_TIMETAG",
                                                                ticker.getTimeTag().toString()))
                                                .subscribe())
                                .log().subscribe());
    }
}