package com.tradingbot.tickerservice.ticker.service;

import com.tradingbot.tickerservice.ticker.domain.Ticker;
import com.tradingbot.tickerservice.ticker.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TickerServiceImpl implements TickerService {
    private final TickerRepository tickerRepository;

    @Override
    public Mono<Ticker> save(Ticker ticker) {
        return tickerRepository.save(ticker);
    }

    @Override
    public Flux<Ticker> saveAll(Flux<Ticker> tickerStream) {
        return tickerRepository.saveAll(tickerStream);
    }

    @Override
    public Flux<Ticker> findAll() {
        return tickerRepository.findAll();
    }

    @Override
    public Flux<Ticker> findTickersBySymbol(String symbol) {
        return tickerRepository.findTickersBySymbol(symbol);
    }

    @Override
    public Mono<Void> deleteAll() {
        return tickerRepository.deleteAll();
    }

    @Override
    public Mono<Void> deleteOldData(int period) {
        return tickerRepository.deleteTickersByTimeTagIsBefore(LocalDateTime.now().minusDays(period));
    }

}
