/* (C) 2024 */
package com.nha.abdm.wrapper.common.requests;

import com.nha.abdm.wrapper.hiu.hrp.consent.requests.DateRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HealthInformationRequest {
  private IdRequest consent;
  private DateRange dateRange;
  private String dataPushUrl;
  private HealthInformationKeyMaterial keyMaterial;
}
