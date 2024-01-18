/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.hipLink.requests;

import com.nha.abdm.wrapper.hip.hrp.hipLink.requests.helpers.LinkQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkAuthInit {
  private String requestId;
  private String timestamp;
  private LinkQuery query;
}
