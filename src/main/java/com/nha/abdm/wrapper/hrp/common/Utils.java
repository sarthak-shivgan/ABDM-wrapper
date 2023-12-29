package com.nha.abdm.wrapper.hrp.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Utils {
	public static String getCurrentTimeStamp() {
		return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
	}
	public static String getRandomFutureDate(){
		return LocalDateTime.ofInstant(
				Instant.ofEpochMilli(System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(1, 11) * 365 * 24 * 60 * 60 * 1000),
				ZoneOffset.UTC
		).toString();
	}
	public static String getSmsExpiry(){
		return LocalDateTime.now().plusMinutes(15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}
