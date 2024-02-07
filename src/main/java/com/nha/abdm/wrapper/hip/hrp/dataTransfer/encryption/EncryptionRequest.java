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
public class EncryptionRequest {
  private String receiverPublicKey;
  private String receiverNonce;
  private String senderPrivateKey;
  private String senderPublicKey;
  private String senderNonce;
  private String plainTextData;
}
