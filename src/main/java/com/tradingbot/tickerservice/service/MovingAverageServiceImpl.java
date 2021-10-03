package com.tradingbot.tickerservice.service;

import com.tradingbot.tickerservice.domain.Ticker;
import com.tradingbot.tickerservice.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovingAverageServiceImpl implements MovingAverageService{
    private final TickerRepository tickerRepository;
    @Override
    public Mono<Double> findSecMovingAverage(String symbol, int period) {
        return tickerRepository.findTickersBySymbolAndTimeTagIsAfter(symbol, LocalDateTime.now().minusSeconds(period)).collect(Collectors.averagingDouble(Ticker::getClosePrice));
    }@Override
    public Mono<Double> findMinMovingAverage(String symbol, int period) {
        return tickerRepository.findTickersBySymbolAndTimeTagIsAfter(symbol, LocalDateTime.now().minusMinutes(period)).collect(Collectors.averagingDouble(Ticker::getClosePrice));
    }
    @Override
    public Mono<Double> findHourMovingAverage(String symbol, int period) {
        return tickerRepository.findTickersBySymbolAndTimeTagIsAfter(symbol, LocalDateTime.now().minusHours(period)).collect(Collectors.averagingDouble(Ticker::getClosePrice));
    }
    @Override
    public Mono<Double> findDayMovingAverage(String symbol, int period) {
        return tickerRepository.findTickersBySymbolAndTimeTagIsAfter(symbol, LocalDateTime.now().minusDays(period)).collect(Collectors.averagingDouble(Ticker::getClosePrice));
    }
}
