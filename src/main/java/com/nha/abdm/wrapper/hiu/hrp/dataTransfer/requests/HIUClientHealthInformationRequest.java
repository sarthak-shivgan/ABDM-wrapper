/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HIUClientHealthInformationRequest {
  private String requestId;
  private String consentId;
}
