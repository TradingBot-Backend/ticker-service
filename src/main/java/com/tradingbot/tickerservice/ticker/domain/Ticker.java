package com.tradingbot.tickerservice.ticker.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonComponent
@Builder
@Document(collection = "tickers")
public class Ticker {
    @JsonIgnore
    @Id
    private String id;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("tickType")
    private String tickType;
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

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("timeTag")
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field("timeTag")
    private LocalDateTime timeTag = LocalDateTime.now();
}
