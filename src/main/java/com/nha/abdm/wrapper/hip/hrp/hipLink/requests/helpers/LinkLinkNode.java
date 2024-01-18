/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.hipLink.requests.helpers;

import com.nha.abdm.wrapper.hip.hrp.discover.requests.OnDiscoverPatient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LinkLinkNode {
  private String accessToken;
  private OnDiscoverPatient patient;
}
