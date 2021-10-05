package com.tradingbot.tickerservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
//이거 정리할 수 있는 방법이 있을 거
public enum DayMovingAverage {
    DMA5(5),
    DMA20(20),
    DMA60(60),
    DMA120(120);
    private final int value;
}
