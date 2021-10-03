package com.tradingbot.tickerservice.service;

import com.tradingbot.tickerservice.domain.Ticker;
import com.tradingbot.tickerservice.repository.TickerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class MovingAverageServiceImplTest {
    MovingAverageServiceImpl maServiceImpl;
    @Autowired
    private TickerRepository tickerRepository;
    @BeforeEach
    void setUp() {
        maServiceImpl = new MovingAverageServiceImpl(tickerRepository);
    }
    @Test
    void findSecMovingAverage() {
    }

    @Test
    void findMinMovingAverage() {
    }

    @Test
    void findHourMovingAverage() {
    }

    @Test
    void findDayMovingAverage() {
        tickerRepository.deleteAll().subscribe();
        List<Ticker> testData = new ArrayList<>();
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").closePrice(1).timeTag(LocalDateTime.now()).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").closePrice(2).timeTag(LocalDateTime.now().minusDays(5)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").closePrice(3).timeTag(LocalDateTime.now().minusDays(10)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").closePrice(4).timeTag(LocalDateTime.now().minusDays(2)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").closePrice(5).timeTag(LocalDateTime.now().minusDays(3)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("BTC_KRW").closePrice(6).timeTag(LocalDateTime.now()).build());
        testData.add(Ticker.builder().id(anyString()).symbol("XLM_KRW").closePrice(2).timeTag(LocalDateTime.now().minusDays(5)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ADA_KRW").closePrice(3).timeTag(LocalDateTime.now().minusDays(10)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("BTC_KRW").closePrice(4).timeTag(LocalDateTime.now().minusDays(2)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").closePrice(2).timeTag(LocalDateTime.now().minusDays(3)).build());
        tickerRepository.saveAll(Flux.fromStream(testData.stream())).blockLast();
        assertThat(maServiceImpl.findDayMovingAverage("ETH_KRW",9).block()).isEqualTo(2.8);
    }
}