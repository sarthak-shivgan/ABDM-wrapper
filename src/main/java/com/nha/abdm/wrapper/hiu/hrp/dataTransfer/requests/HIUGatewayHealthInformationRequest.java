/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer.requests;

import com.nha.abdm.wrapper.common.requests.HealthInformationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HIUGatewayHealthInformationRequest {
  private String requestId;
  private String timestamp;
  private HealthInformationRequest hiRequest;
}
