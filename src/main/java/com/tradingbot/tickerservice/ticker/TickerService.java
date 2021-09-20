package com.tradingbot.tickerservice.ticker;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public interface TickerService {
    Flux<Ticker> findAllBySymbol(String symbol);
}
