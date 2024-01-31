/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentStatusRequest {
  private String requestId;
  private String timestamp;
  private String consentRequestId;
}
