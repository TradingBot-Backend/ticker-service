package com.tradingbot.tickerservice.ticker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class TickerServiceImpl implements TickerService{
    private final TickerRepository tickerRepository;

    @Override
    public Flux<Ticker> findAllBySymbol(String symbol) {
        return tickerRepository.findAll().filter(ticker -> ticker.getSymbol().equals(symbol));
    }
}
