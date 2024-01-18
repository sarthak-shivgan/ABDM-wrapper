/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.discover.requests;

import com.nha.abdm.wrapper.hip.hrp.common.requests.CareContextRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnDiscoverPatient {

  private String referenceNumber;
  private String display;
  private List<CareContextRequest> careContexts;
  private List<String> matchedBy;
}
