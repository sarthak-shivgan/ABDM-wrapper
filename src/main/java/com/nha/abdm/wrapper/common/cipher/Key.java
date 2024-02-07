/* (C) 2024 */
package com.nha.abdm.wrapper.common.cipher;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Key {
  private String privateKey;
  private String publicKey;
  private String nonce;
}
