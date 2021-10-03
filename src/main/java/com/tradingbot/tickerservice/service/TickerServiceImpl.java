package com.tradingbot.tickerservice.service;

import com.mongodb.reactivestreams.client.MongoDatabase;
import com.tradingbot.tickerservice.domain.Ticker;
import com.tradingbot.tickerservice.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TickerServiceImpl implements TickerService {
    private final TickerRepository tickerRepository;
    private final MongoDatabase mongoDatabase;

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
