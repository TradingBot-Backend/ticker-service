package com.tradingbot.tickerservice.movingaverage.service;

import com.tradingbot.tickerservice.ticker.domain.Ticker;
import org.springframework.stereotype.Service;

@Service
public interface MovingAverageService {
    public void loadMinMovingAverage(Ticker ticker);
    public void loadHourMovingAverage(Ticker ticker);
    public void loadDayMovingAverage(Ticker ticker);

}
