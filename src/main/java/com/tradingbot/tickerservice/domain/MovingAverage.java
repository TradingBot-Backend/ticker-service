package com.tradingbot.tickerservice.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class MovingAverage {
    private String symbol;
    private double average;
}
