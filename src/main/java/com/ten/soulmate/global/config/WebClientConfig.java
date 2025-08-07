package com.ten.soulmate.global.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Value("${clova.api.url}")
	private String apiUrl;

	@Value("${clova.api.key}")
    private String apiKey;	
	
	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder
				.baseUrl(apiUrl)
				.defaultHeaders(httpHeaders -> {
					httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);					
					httpHeaders.add(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);
					httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer "+apiKey);
				})
				.build();
	}
}
