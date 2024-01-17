/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnInitLinkMeta {

  private String communicationMedium;
  private String communicationHint;
  private String communicationExpiry;
}
