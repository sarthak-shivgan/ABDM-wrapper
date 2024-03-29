/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.share;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenNumberGenerator {

  private AtomicInteger counter = new AtomicInteger(0);
  private LocalDate currentDate = LocalDate.now();

  @Scheduled(cron = "0 0 0 * * *")
  public void resetTokenCount() {
    counter.set(0);
    currentDate = LocalDate.now();
  }

  public String generateTokenNumber() {
    if (currentDate.isBefore(LocalDate.now())) {
      resetTokenCount();
    }
    int tokenNumber = counter.incrementAndGet();
    return String.format("%04d", tokenNumber);
  }
}
