/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.HealthInformationEntries;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.HealthInformationHiRequest.HealthInformationkeyMaterial.HealthInformationKeyMaterial;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HealthInformationPushRequest {
  private int pageNumber;
  private int pageCount;
  private String transactionId;
  private List<HealthInformationEntries> entries;
  private HealthInformationKeyMaterial keyMaterial;
}
