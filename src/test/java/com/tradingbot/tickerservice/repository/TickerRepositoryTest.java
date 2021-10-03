package com.tradingbot.tickerservice.repository;

import com.tradingbot.tickerservice.domain.Ticker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@DataMongoTest
@ExtendWith(SpringExtension.class)
class TickerRepositoryTest {
    @Autowired
    TickerRepository tickerRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void save() {
        Ticker ticker = Ticker.builder()
                .id(anyString())
                .symbol("ETH_KRW")
                .tickType("30M")
                .openPrice(30)
                .closePrice(40)
                .lowPrice(50)
                .highPrice(60)
                .value(70)
                .volume(80)
                .sellVolume(90)
                .buyVolume(100)
                .prevClosePrice(110)
                .chgRate(120)
                .chgAmt(130)
                .volumePower(140)
                .timeTag(LocalDateTime.now())
                .build();
        tickerRepository.save(ticker).as(StepVerifier::create).expectNextMatches(tick -> {
            assertThat(tick.getSymbol()).contains("ETH_KRW");
            return true;
        }).verifyComplete();


    }

    @Test
    void findTickersBySymbol() {
        tickerRepository.deleteAll().subscribe();
        List<Ticker> testData = new ArrayList<>();
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now()).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(5)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(10)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(2)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(3)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("BTC_KRW").timeTag(LocalDateTime.now()).build());
        testData.add(Ticker.builder().id(anyString()).symbol("XLM_KRW").timeTag(LocalDateTime.now().minusDays(5)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ADA_KRW").timeTag(LocalDateTime.now().minusDays(10)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("BTC_KRW").timeTag(LocalDateTime.now().minusDays(2)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(3)).build());
        tickerRepository.saveAll(testData).subscribe();
        assertThat(tickerRepository.findTickersBySymbol("ETH_KRW").count().block()).isEqualTo(6);
    }

    @Test
    void findTickersBySymbolAndTimeTagIsAfter() {
        tickerRepository.deleteAll().subscribe();
        List<Ticker> testData = new ArrayList<>();
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now()).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(5)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(10)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(2)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(3)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("BTC_KRW").timeTag(LocalDateTime.now()).build());
        testData.add(Ticker.builder().id(anyString()).symbol("XLM_KRW").timeTag(LocalDateTime.now().minusDays(5)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ADA_KRW").timeTag(LocalDateTime.now().minusDays(10)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("BTC_KRW").timeTag(LocalDateTime.now().minusDays(2)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(3)).build());
        tickerRepository.saveAll(testData).subscribe();
        assertThat(tickerRepository.findTickersBySymbolAndTimeTagIsAfter("ETH_KRW", LocalDateTime.now().minusDays(9)).count().block()).isEqualTo(5);
    }

    @Test
    void deleteAll() {
        tickerRepository.deleteAll().subscribe();
        List<Ticker> testData = new ArrayList<>();
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now()).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(5)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(10)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(2)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(3)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("BTC_KRW").timeTag(LocalDateTime.now()).build());
        testData.add(Ticker.builder().id(anyString()).symbol("XLM_KRW").timeTag(LocalDateTime.now().minusDays(5)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ADA_KRW").timeTag(LocalDateTime.now().minusDays(10)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("BTC_KRW").timeTag(LocalDateTime.now().minusDays(2)).build());
        testData.add(Ticker.builder().id(anyString()).symbol("ETH_KRW").timeTag(LocalDateTime.now().minusDays(3)).build());
        tickerRepository.saveAll(testData).subscribe();
        tickerRepository.deleteAll().subscribe();
        assertThat(tickerRepository.findAll().count().block()).isEqualTo(0);
    }

    @Test
    void deleteTickersByTimeTagIsBefore() {
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
        tickerRepository.deleteTickersByTimeTagIsBefore(LocalDateTime.now().minusDays(9)).block();
        assertThat(tickerRepository.findAll().count().block()).isEqualTo(8);
    }
}