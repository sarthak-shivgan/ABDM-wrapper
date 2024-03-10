/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.encryption;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.HealthInformationBundle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EncryptionResponse {
  private List<HealthInformationBundle> healthInformationBundles;
  private String keyToShare;
  private String senderNonce;
}
