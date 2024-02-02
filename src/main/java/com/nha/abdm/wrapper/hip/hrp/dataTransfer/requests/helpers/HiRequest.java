/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HiRequest {
  public String transactionId;
  public String sessionStatus;
}
