package com.tradingbot.tickerservice.ticker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonComponent
@Document(collection = "tickers")
public class Ticker {
    @JsonIgnore
    @Id
    private String id;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("tickType")
    private String tickType;
    //timetag와 크게 다르지 않다면 없애도될듯
    @JsonProperty("date")
    private String date;
    @JsonProperty("time")
    private String time;
    @JsonProperty("openPrice")
    private double openPrice;
    @JsonProperty("closePrice")
    private double closePrice;
    @JsonProperty("lowPrice")
    private double lowPrice;
    @JsonProperty("highPrice")
    private double highPrice;
    @JsonProperty("value")
    private double value;
    @JsonProperty("volume")
    private double volume;
    @JsonProperty("sellVolume")
    private double sellVolume;
    @JsonProperty("buyVolume")
    private double buyVolume;
    @JsonProperty("prevClosePrice")
    private double prevClosePrice;
    @JsonProperty("chgRate")
    private double chgRate;
    @JsonProperty("chgAmt")
    private double chgAmt;
    @JsonProperty("volumePower")
    private double volumePower;
    @JsonProperty("timeTag")
    @Field("timeTag")
    private LocalDateTime timeTag = LocalDateTime.now();
}
