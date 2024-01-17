/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.common.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareContextRequest {

  private String referenceNumber;
  private String display;
}
