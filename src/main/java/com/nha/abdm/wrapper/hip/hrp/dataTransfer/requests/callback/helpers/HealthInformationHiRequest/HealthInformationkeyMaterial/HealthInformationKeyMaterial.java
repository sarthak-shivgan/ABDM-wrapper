/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.HealthInformationHiRequest.HealthInformationkeyMaterial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HealthInformationKeyMaterial {
  public String cryptoAlg;
  public String curve;
  public HealthInformationDhPublicKey dhPublicKey;
  public String nonce;
}
