/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.DataNotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataPushNotification {
  public String requestId;
  public String timestamp;
  public DataNotificationStatus notification;
}
