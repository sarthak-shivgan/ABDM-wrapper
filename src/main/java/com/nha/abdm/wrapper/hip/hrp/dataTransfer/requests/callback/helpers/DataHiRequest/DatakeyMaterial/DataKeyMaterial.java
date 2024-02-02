/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataHiRequest.DatakeyMaterial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataKeyMaterial {
  public String cryptoAlg;
  public String curve;
  public DataDhPublicKey dhPublicKey;
  public String nonce;
}
