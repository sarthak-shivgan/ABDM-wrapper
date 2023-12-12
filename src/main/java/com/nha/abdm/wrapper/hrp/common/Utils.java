package com.nha.abdm.wrapper.hrp.common;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.nha.abdm.wrapper.hrp.properties.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class Utils {
	@Autowired
	ApplicationConfig applicationConfig;
	public String accessToken;

	public String getAccessToken() {
		return this.accessToken;
	}

	public void setAccessToken(JsonNode accessToken) {
		this.accessToken = accessToken.asText();
	}

	public String getCurrentTimeStamp() {
		return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
	}

	public HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("X-CM-ID",applicationConfig.enviornment);
		headers.add("Authorization", "Bearer " + accessToken);
		return headers;
	}
	public String getRandomFutureDate(){
		return LocalDateTime.ofInstant(
				Instant.ofEpochMilli(System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(1, 11) * 365 * 24 * 60 * 60 * 1000),
				ZoneOffset.UTC
		).toString();
	}
}
