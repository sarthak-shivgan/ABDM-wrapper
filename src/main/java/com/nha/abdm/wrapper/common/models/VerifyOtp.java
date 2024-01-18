/* (C) 2024 */
package com.nha.abdm.wrapper.common.models;

import lombok.Data;

@Data
public class VerifyOtp {
  private String loginHint;
  private String requestId;
  private String authCode;
}
