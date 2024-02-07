/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HealthInformationNotificationStatus {
  public String consentId;
  public String transactionId;
  public String doneAt;
  public HealthInformationNotifier notifier;
  public HealthInformationStatusNotification statusNotification;
}
