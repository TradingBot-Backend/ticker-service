package com.tradingbot.tickerservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
//이거 정리할 수 있는 방법이 있을 거
public enum MovingAverage {
    SMA5(5,"S"),
    SMA10(10,"S"),
    SMA30(30,"S"),
    SMA60(60,"S"),
    MMA5(5,"M"),
    MMA10(10,"M"),
    MMA30(30,"M"),
    MMA60(60,"M"),
    HMA3(3,"H"),
    HMA5(5,"H"),
    HMA10(10,"H"),
    HMA24(24,"H"),
    DMA5(5, "D"),
    DMA20(20, "D"),
    DMA60(60,"D"),
    DMA120(120,"D");
    private final int value;
    private final String type;
}
