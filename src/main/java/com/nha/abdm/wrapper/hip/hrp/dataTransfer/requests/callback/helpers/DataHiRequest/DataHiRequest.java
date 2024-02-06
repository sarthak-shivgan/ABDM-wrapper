/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataHiRequest;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataHiRequest.DatakeyMaterial.DataKeyMaterial;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.DateRange;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.OnFetchConsentManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataHiRequest {
  public OnFetchConsentManager consent;
  public String dataPushUrl;
  public DateRange dateRange;
  public DataKeyMaterial keyMaterial;
}
