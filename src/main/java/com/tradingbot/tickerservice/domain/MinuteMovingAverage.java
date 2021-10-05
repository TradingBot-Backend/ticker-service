package com.tradingbot.tickerservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MinuteMovingAverage {
    MMA5(5),
    MMA10(10),
    MMA30(30),
    MMA60(60);
    private final int value;
}
