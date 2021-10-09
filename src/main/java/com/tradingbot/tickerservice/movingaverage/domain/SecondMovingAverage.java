package com.tradingbot.tickerservice.movingaverage.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecondMovingAverage {
    SMA5(5),
    SMA10(10),
    SMA30(30),
    SMA60(60);
    private final int value;
}
