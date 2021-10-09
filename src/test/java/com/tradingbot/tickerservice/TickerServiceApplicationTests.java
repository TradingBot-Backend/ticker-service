package com.tradingbot.tickerservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.tickerservice.ticker.domain.Ticker;
import com.tradingbot.tickerservice.ticker.repository.TickerRepository;
import com.tradingbot.tickerservice.ticker.service.TickerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;

class TickerServiceApplicationTests {

	@Autowired
	private TickerRepository tickerRepository;
	@Autowired
	private TickerService tickerService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("test DB")
	void testDB() {
		tickerRepository.save(new Ticker()).log().subscribe();
	}
	@Test
	@DisplayName("test Json String to Mongo")
	void testJsonToMongo() throws Exception {
		String tickerExample = "{ \"type\" : \"ticker\",\"content\" : {\"symbol\" : \"BTC_KRW\",\"tickType\" : \"24H\"," +
				"\"date\" : \"20200129\"," +
				"\"time\" : \"121844\", " +
				"\"openPrice\" : \"2302\", " +
				"\"closePrice\" : \"2317\", " +
				"\"lowPrice\" : \"2272\", " +
				"\"highPrice\" : \"2344\", " +
				"\"value\" : \"2831915078.07065789\", " +
				"\"volume\" : \"1222314.51355788\", " +
				"\"sellVolume\" : \"760129.34079004\", " +
				"\"buyVolume\" : \"462185.17276784\", " +
				"\"prevClosePrice\" : \"2326\", " +
				"\"chgRate\" : \"0.65\", " +
				"\"chgAmt\" : \"15\", " +
				"\"volumePower\" : \"60.80\"}}";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode jsonNode = mapper.readTree(tickerExample);
		Ticker ticker = mapper.readValue(jsonNode.get("content").toString(), Ticker.class);
		//tickerRepository.save(ticker).log().subscribe();
		Mono<Ticker> tick = Mono.just(ticker);
		tick.doOnNext(a -> tickerRepository.save(a).subscribe()).log().subscribe();
	}
	@Test
	@DisplayName("test Json Array Ticker")
	void testJsonArrayToTicker() throws Exception {
		String tickerExample =
		"[{\"symbol\":\"BCH\",\"timeTag\":\"2021-10-01T00:00:00.000\",\"candle_date_time_kst\":\"2021-10-01T09:00:00\"," +
			"\"openPrice\":615600.00000000,\"highPrice\":655700.00000000,\"lowPrice\":611700.00000000,\"closePrice\":652600.00000000," +
				"\"timestamp\":1633106776363,\"candle_acc_closePrice\":23645474174.24385100,\"\":37084.43471351,\"prevClosePrice\":615700.00000000,\"chgAmt\":36900.00000000," +
				"\"chgRate\":0.0599317850},"+
		"{\"symbol\":\"BCH\",\"timeTag\":\"2021-10-01T00:00:00.000\",\"candle_date_time_kst\":\"2021-10-01T09:00:00\"," +
				"\"openPrice\":615600.00000000,\"highPrice\":655700.00000000,\"lowPrice\":611700.00000000,\"closePrice\":652600.00000000," +
				"\"timestamp\":1633106776363,\"candle_acc_closePrice\":23645474174.24385100,\"\":37084.43471351,\"prevClosePrice\":615700.00000000,\"chgAmt\":36900.00000000," +
				"\"chgRate\":0.0599317850}]";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Arrays.stream(mapper.readValue(tickerExample,Ticker[].class)).forEach(System.out::println);

	}

	@Test
	@DisplayName("test ws connection with Bithumb")
	void testConnection() throws JsonProcessingException {
		WebSocketClient client = new ReactorNettyWebSocketClient();

		client.execute(
				URI.create("wss://pubwss.bithumb.com/pub/ws"),
				session -> session.send(
								Mono.just(session.textMessage("{'type':'ticker', 'symbols': ['ETH_KRW'],'tickTypes':['30M']}")))
						//.doOnNext(System.out::println)
						.thenMany(session.receive().map(WebSocketMessage::retain).map(WebSocketMessage::getPayloadAsText).log())
						.then()).subscribe();
	}

	@Test
	@DisplayName("test flux message to Mongo")
	void testFluxMessageToMongo() {
		String[] testExample = new String[10];
		String tickerExample = "{ \"type\" : \"ticker\",\"content\" : {\"symbol\" : \"BTC_KRW\",\"tickType\" : \"24H\"," +
				"\"date\" : \"20200129\"," +
				"\"time\" : \"121844\", " +
				"\"openPrice\" : \"2302\", " +
				"\"closePrice\" : \"2317\", " +
				"\"lowPrice\" : \"2272\", " +
				"\"highPrice\" : \"2344\", " +
				"\"value\" : \"2831915078.07065789\", " +
				"\"volume\" : \"1222314.51355788\", " +
				"\"sellVolume\" : \"760129.34079004\", " +
				"\"buyVolume\" : \"462185.17276784\", " +
				"\"prevClosePrice\" : \"2326\", " +
				"\"chgRate\" : \"0.65\", " +
				"\"chgAmt\" : \"15\", " +
				"\"volumePower\" : \"60.80\"}}";
		Flux<String> tick = Flux.from(Mono.just(tickerExample));
		//  tick.doOnNext(System.out::println).subscribe();
		tick.map(message -> {
					try {
						return objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
								.readValue(objectMapper.readTree(message).get("content").toString(), Ticker.class);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					return null;
				})
				.doOnNext(ticker -> tickerRepository.save(ticker).log().subscribe()).subscribe();

	}
	@Test
	@DisplayName("test flux message to Mongo")
	void testWebSocketMessagetoMongo() {
		WebSocketClient client = new ReactorNettyWebSocketClient();
		client.execute(
						URI.create("wss://pubwss.bithumb.com/pub/ws"),
						session -> session.send(
										Mono.just(session.textMessage("{'type':'ticker', 'symbols': ['ETH_KRW'],'tickTypes':['30M']}")))
								.thenMany(session.receive().map(WebSocketMessage::getPayloadAsText)).log()
								//status 오류(connection 오류) 있을 때 체크, 값이 들어오지 않는 것 체크 (2가지 경우 있음)
								.filter(text -> text.startsWith("{\"type\":\"ticker\""))
								.map(message -> {
									//reactive하게 바꾸기
									try {
										return objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
												.readValue(objectMapper.readTree(message).get("content").toString(), Ticker.class);

									} catch (JsonProcessingException e) {
										e.printStackTrace();
									}
									return null;
								}).doOnNext(ticker -> tickerRepository.save(ticker).subscribe())
								.then())
				.subscribe();
	}
}


