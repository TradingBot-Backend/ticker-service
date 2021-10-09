package com.tradingbot.tickerservice.movingaverage.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HourMovingAverage {
    HMA3(3),
    HMA5(5),
    HMA10(10),
    HMA24(24);
    private final int value;

}
