/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.hipLink.requests;

import com.nha.abdm.wrapper.hip.hrp.hipLink.requests.helpers.LinkLinkNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LinkAddCareContext {
  private String requestId;
  private String timestamp;
  private LinkLinkNode link;
}
