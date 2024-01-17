/* (C) 2024 */
package com.nha.abdm.wrapper.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class Utils {
  public static String getCurrentTimeStamp() {
    return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
  }

  public static String getSmsExpiry() {
    return LocalDateTime.now().plusMinutes(15).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }
}
