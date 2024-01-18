/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.hipLink.requests.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkQuery {
  private String id;
  private String purpose;
  private String authMode;
  private LinkRequester requester;
}
