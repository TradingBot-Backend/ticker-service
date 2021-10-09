package com.tradingbot.tickerservice.ticker.repository;

import com.tradingbot.tickerservice.ticker.domain.Ticker;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@EnableReactiveMongoRepositories
@Repository
public interface TickerRepository extends ReactiveCrudRepository<Ticker, String> {

    Mono<Ticker> save(Ticker ticker);

    Flux<Ticker> saveAll(Flux<Ticker> tickerStream);

    Flux<Ticker> findAll();

    Flux<Ticker> findTickersBySymbol(String symbol);

    Flux<Ticker> findTickersBySymbolAndTimeTagIsAfter(String symbol, LocalDateTime date);

    Mono<Void> deleteAll(Flux<Ticker> tickerStream);

    Mono<Void> deleteTickersByTimeTagIsBefore(LocalDateTime date);
}
