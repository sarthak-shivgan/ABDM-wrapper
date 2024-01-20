/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.userInitiated.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnInitLink {

  private String referenceNumber;
  private String authenticationType;
  private OnInitLinkMeta meta;
}
