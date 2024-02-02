/* (C) 2024 */
package com.nha.abdm.wrapper.common.dataPackaging.encryption;

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
