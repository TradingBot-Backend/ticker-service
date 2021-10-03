package com.tradingbot.tickerservice.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface MovingAverageService {
    public Mono<Double> findSecMovingAverage(String symbol, int period);
    public Mono<Double> findMinMovingAverage(String symbol, int period);
    public Mono<Double> findHourMovingAverage(String symbol, int period);
    public Mono<Double> findDayMovingAverage(String symbol, int period);

}
