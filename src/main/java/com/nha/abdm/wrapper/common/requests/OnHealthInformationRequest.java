/* (C) 2024 */
package com.nha.abdm.wrapper.common.requests;

import com.nha.abdm.wrapper.common.models.RespRequest;
import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.HealthInformationRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnHealthInformationRequest {
  private String requestId;
  private String timestamp;
  private HealthInformationRequestStatus hiRequest;
  private ErrorResponse error;
  private RespRequest resp;
}
