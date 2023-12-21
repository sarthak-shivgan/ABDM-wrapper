package com.nha.abdm.wrapper.hrp.common;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import com.nha.abdm.wrapper.hrp.properties.ApplicationConfig;
import com.nha.abdm.wrapper.hrp.services.CommonServices;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class Utils {
	@Autowired
	ApplicationConfig applicationConfig;
	@Autowired
	CommonServices commonServices;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken =accessToken;
	}

	public String accessToken;

	public String fetchAccessToken() throws URISyntaxException, JsonProcessingException {
		if(accessToken==null){
			return commonServices.startSession();
		}
		return getAccessToken();
	}

	public String getCurrentTimeStamp() {
		return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
	}

	public HttpHeaders initialiseHeadersForGateway() throws URISyntaxException, JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("X-CM-ID",applicationConfig.environment);
		headers.add("Authorization",  fetchAccessToken());
		return headers;
	}
	public String getRandomFutureDate(){
		return LocalDateTime.ofInstant(
				Instant.ofEpochMilli(System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(1, 11) * 365 * 24 * 60 * 60 * 1000),
				ZoneOffset.UTC
		).toString();
	}
}
