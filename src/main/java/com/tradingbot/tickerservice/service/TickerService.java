package com.tradingbot.tickerservice.service;

import com.tradingbot.tickerservice.domain.Ticker;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface TickerService {
    Mono<Ticker> save(Ticker ticker);
    Flux<Ticker> saveAll(Flux<Ticker> tickerStream);
    Flux<Ticker> findAll();
    Flux<Ticker> findTickersBySymbol(String symbol);
    Mono<Void> deleteAll();
    Mono<Void> deleteOldData(int period);
}
