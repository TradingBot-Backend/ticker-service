package com.tradingbot.tickerservice.service;

import com.tradingbot.tickerservice.domain.Ticker;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface MovingAverageService {
    public void loadSecMovingAverage(Ticker ticker);
    public void loadMinMovingAverage(Ticker ticker);
    public void loadHourMovingAverage(Ticker ticker);
    public void loadDayMovingAverage(Ticker ticker);

}
