/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback;

import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyHIURequest {
  private String requestId;
  private String timestamp;
  private Notification notification;
  private ErrorResponse error;
}
