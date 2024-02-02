/* (C) 2024 */
package com.nha.abdm.wrapper.common.dataPackaging.keys;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeyMaterial {
  private String privateKey;
  private String publicKey;
  private String nonce;
}
