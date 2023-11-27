//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.Utility;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class CommonUtility {
	public String accessToken;

	public CommonUtility() {
	}

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
		headers.add("X-CM-ID", "sbx");
		headers.add("Authorization", "Bearer " + this.getAccessToken());
		return headers;
	}
}
