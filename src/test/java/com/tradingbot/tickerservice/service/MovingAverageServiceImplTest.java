package com.tradingbot.tickerservice.service;

import com.tradingbot.tickerservice.domain.MovingAverage;
import com.tradingbot.tickerservice.domain.Ticker;
import com.tradingbot.tickerservice.repository.TickerRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class MovingAverageServiceImplTest {
    private static final String COLLECTION = "moving-average";
    @Autowired ReactiveMongoTemplate reactiveMongoTemplate;
/*
    @BeforeEach
    public void setUp(){
        reactiveMongoTemplate.dropCollection(COLLECTION)
                .then(reactiveMongoTemplate.dropCollection("tickers"))
                .as(StepVerifier::create).verifyComplete();
        reactiveMongoTemplate.createCollection(COLLECTION)
                .then(reactiveMongoTemplate.dropCollection("tickers"))
                .as(StepVerifier::create).verifyComplete();
    }*/

    @Test
    void insertTicker(){
        Ticker ethTicker1 = Ticker.builder()
                .closePrice(30000)
                .symbol("ETH")
                .build();
        reactiveMongoTemplate.insert(ethTicker1).as(StepVerifier::create)
                .expectNextMatches( ticker -> {assertThat(ticker.getSymbol()).contains("ETH");
                    return true;
                }).verifyComplete();
    }
    @Test
    void insertTickerAndFindAverage() {
        Ticker ethTicker1 = Ticker.builder()
                .closePrice(30000)
                .symbol("ETH")
                .build();
        Ticker btcTicker = Ticker.builder()
                .closePrice(1000000)
                .symbol("BTC")
                .build();
        Ticker ethTicker2 = Ticker.builder()
                .closePrice(10000)
                .symbol("ETH")
                .build();

        reactiveMongoTemplate.insertAll(Arrays.asList(ethTicker1,btcTicker,ethTicker2)).as(StepVerifier::create)
                .expectNextCount(3).verifyComplete();

        Aggregation agg = Aggregation.newAggregation(match(where("symbol").is("ETH")),Aggregation.group("$symbol").avg("$closePrice").as("average"));
        reactiveMongoTemplate.aggregate(agg, "tickers", MovingAverage.class).as(StepVerifier::create)
                .consumeNextWith(movingAverage -> {
                    assertThat(movingAverage.getAverage()).isEqualTo(20000);
                }).verifyComplete();
    }
    @Test @DisplayName("코인명, 시간 데이터 정제 (gte 사용)")
    void insertAndFilterCoinAndDateAndAverageUsingGTE() {
        Ticker ethTicker1 = Ticker.builder()
                .closePrice(10)
                .symbol("ETH")
                .timeTag(LocalDateTime.now().minusDays(100))
                .build();
        Ticker btcTicker1 = Ticker.builder()
                .closePrice(20)
                .symbol("BTC")
                .timeTag(LocalDateTime.now().minusDays(10))
                .build();
        Ticker xlmTicker1 = Ticker.builder()
                .closePrice(30)
                .symbol("XLM")
                .timeTag(LocalDateTime.now().minusDays(10))
                .build();
        Ticker ethTicker2 = Ticker.builder()
                .closePrice(40)
                .symbol("ETH")
                .timeTag(LocalDateTime.now().minusDays(10))
                .build();
        Ticker btcTicker2 = Ticker.builder()
                .closePrice(50)
                .symbol("BTC")
                .timeTag(LocalDateTime.now())
                .build();
        Ticker linkTicker1 = Ticker.builder()
                .closePrice(60)
                .symbol("LINK")
                .timeTag(LocalDateTime.now().minusDays(9))
                .build();

        reactiveMongoTemplate.insertAll(Arrays.asList(ethTicker1,ethTicker2,btcTicker1,btcTicker2,xlmTicker1,linkTicker1))
                .as(StepVerifier::create)
                .expectNextCount(6).verifyComplete();

        TypedAggregation<Ticker> ethAgg1 = Aggregation.newAggregation(
                Ticker.class,
                match(where("symbol").is("ETH").and("timeTag").gte(LocalDateTime.now().minusDays(100))),
                Aggregation.group("$symbol")
                        .avg("$closePrice")
                        .as("average"));

        TypedAggregation<Ticker> ethAgg2 = Aggregation.newAggregation(
                Ticker.class,
                match(where("symbol").is("ETH").and("timeTag").gte(LocalDateTime.now().minusDays(101))),
                Aggregation.group("$symbol")
                        .avg("$closePrice")
                        .as("average"));

        reactiveMongoTemplate.aggregate(ethAgg1,MovingAverage.class)
                .as(StepVerifier::create)
                .consumeNextWith(movingAverage -> {
                    assertThat(movingAverage.getAverage()).isEqualTo(40);
                }).verifyComplete();

        reactiveMongoTemplate.aggregate(ethAgg2, MovingAverage.class)
                .as(StepVerifier::create)
                .consumeNextWith(movingAverage -> {
                    assertThat(movingAverage.getAverage()).isEqualTo(25);
                }).verifyComplete();
    }
    @Test @DisplayName("코인명, 시간 데이터 정제 (gte, lte 사용)")
    void insertAndFilterCoinAndDateAndAverageUsingLTE() {
        Ticker ethTicker1 = Ticker.builder()
                .closePrice(10)
                .symbol("ETH")
                .timeTag(LocalDateTime.now().minusDays(100))
                .build();
        Ticker btcTicker1 = Ticker.builder()
                .closePrice(20)
                .symbol("BTC")
                .timeTag(LocalDateTime.now().minusDays(10))
                .build();
        Ticker xlmTicker1 = Ticker.builder()
                .closePrice(30)
                .symbol("XLM")
                .timeTag(LocalDateTime.now().minusDays(10))
                .build();
        Ticker ethTicker2 = Ticker.builder()
                .closePrice(40)
                .symbol("ETH")
                .timeTag(LocalDateTime.now().minusDays(10))
                .build();
        Ticker btcTicker2 = Ticker.builder()
                .closePrice(50)
                .symbol("BTC")
                .timeTag(LocalDateTime.now())
                .build();
        Ticker linkTicker1 = Ticker.builder()
                .closePrice(60)
                .symbol("LINK")
                .timeTag(LocalDateTime.now().minusDays(9))
                .build();

        reactiveMongoTemplate.insertAll(Arrays.asList(ethTicker1,ethTicker2,btcTicker1,btcTicker2,xlmTicker1,linkTicker1))
                .as(StepVerifier::create)
                .expectNextCount(6).verifyComplete();

        TypedAggregation<Ticker> ethAgg1 = Aggregation.newAggregation(
                Ticker.class,
                match(where("symbol").is("BTC").and("timeTag").gte(LocalDateTime.now().minusDays(11))),
                Aggregation.group("$symbol")
                        .avg("$closePrice")
                        .as("average"));

        TypedAggregation<Ticker> ethAgg2 = Aggregation.newAggregation(
                Ticker.class,
                match(where("symbol").is("BTC").and("timeTag").gte(LocalDateTime.now().minusDays(11))
                        .lte(LocalDateTime.now())),
                Aggregation.group("$symbol")
                        .avg("$closePrice")
                        .as("average"));

        reactiveMongoTemplate.aggregate(ethAgg1,MovingAverage.class)
                .as(StepVerifier::create)
                .consumeNextWith(movingAverage -> {
                    assertThat(movingAverage.getAverage()).isEqualTo(35);
                }).verifyComplete();

        reactiveMongoTemplate.aggregate(ethAgg2, MovingAverage.class)
                .as(StepVerifier::create)
                .consumeNextWith(movingAverage -> {
                    assertThat(movingAverage.getAverage()).isEqualTo(35);
                }).verifyComplete();
    }
    @Test @DisplayName("lte Test")
    void lesserThanTest() {
        Ticker ethTicker1 = Ticker.builder()
                .closePrice(10)
                .symbol("ETH")
                .timeTag(LocalDateTime.now().minusDays(100))
                .build();
        Ticker btcTicker1 = Ticker.builder()
                .closePrice(20)
                .symbol("BTC")
                .timeTag(LocalDateTime.now().minusDays(10))
                .build();
        Ticker xlmTicker1 = Ticker.builder()
                .closePrice(30)
                .symbol("XLM")
                .timeTag(LocalDateTime.now().minusDays(10))
                .build();
        Ticker ethTicker2 = Ticker.builder()
                .closePrice(40)
                .symbol("ETH")
                .timeTag(LocalDateTime.now().minusDays(10))
                .build();
        Ticker btcTicker2 = Ticker.builder()
                .closePrice(50)
                .symbol("BTC")
                .timeTag(LocalDateTime.now())
                .build();
        Ticker linkTicker1 = Ticker.builder()
                .closePrice(60)
                .symbol("LINK")
                .timeTag(LocalDateTime.now().minusDays(9))
                .build();

        reactiveMongoTemplate.insertAll(Arrays.asList(ethTicker1,ethTicker2,btcTicker1,btcTicker2,xlmTicker1,linkTicker1))
                .as(StepVerifier::create)
                .expectNextCount(6).verifyComplete();

        TypedAggregation<Ticker> btcAgg1 = Aggregation.newAggregation(
                Ticker.class,
                match(where("symbol").is("BTC").and("timeTag").lte(LocalDateTime.now().minusDays(10))),
                Aggregation.group("$symbol")
                        .avg("$closePrice")
                        .as("average"));

        reactiveMongoTemplate.aggregate(btcAgg1,MovingAverage.class)
                .as(StepVerifier::create)
                .consumeNextWith(movingAverage -> {
                    assertThat(movingAverage.getAverage()).isEqualTo(20);
                }).verifyComplete();

    }
    @Test @DisplayName("많은 양의 데이터")
    void bigDataTest() {
        String[] coins = {"BTC","ETH","XLM","LINK"};

        for(int i = 1 ; i <= 10000; i++){
                reactiveMongoTemplate.insert(Ticker.builder().symbol(coins[i%4]).closePrice(i*10).timeTag(LocalDateTime.now()).build()).subscribe();
        }



        TypedAggregation<Ticker> btcAgg1 = Aggregation.newAggregation(
                Ticker.class,
                match(where("symbol").is("BTC").and("timeTag").gte(LocalDateTime.now().minusMinutes(5))),
                Aggregation.group("$symbol")
                        .avg("$closePrice")
                        .as("average"));

        reactiveMongoTemplate.aggregate(btcAgg1,MovingAverage.class)
                .as(StepVerifier::create)
                .consumeNextWith(movingAverage -> {
                    assertThat(movingAverage.getAverage()).isEqualTo(20);
                }).verifyComplete();

    }
}