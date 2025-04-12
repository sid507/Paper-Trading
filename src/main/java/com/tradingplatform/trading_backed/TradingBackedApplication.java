package com.tradingplatform.trading_backed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class TradingBackedApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingBackedApplication.class, args);
	}

	@RestController
	@RequestMapping("/hello")
	public class HelloController {
		@GetMapping
		public String index() {
			return "Hello World!";
		}
	}

}
