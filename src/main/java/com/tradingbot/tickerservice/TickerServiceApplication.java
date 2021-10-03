package com.tradingbot.tickerservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class TickerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickerServiceApplication.class, args);
	}
}
