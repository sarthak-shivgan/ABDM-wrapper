/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.encryption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EncryptionResponse {
  private String encryptedData;
  private String keyToShare;
  private String senderNonce;
}
