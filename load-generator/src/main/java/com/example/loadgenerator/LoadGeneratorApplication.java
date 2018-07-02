package com.example.loadgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@SpringBootApplication
public class LoadGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadGeneratorApplication.class, args);

		WebClient client = WebClient.create("http://localhost:8080/quotes/feed");
		Flux<Quote> response =
				client.get()
						.accept(TEXT_EVENT_STREAM)
						.retrieve()
						.bodyToFlux(Quote.class);
		Flux.range(0, 1000)
			.delayElements(Duration.ofMillis(100))
			.flatMap(i -> {
				System.out.println("Request " + i + " started...");
				return response;
			}, 1000)
			.blockLast(Duration.ofMinutes(5));
	}
}
