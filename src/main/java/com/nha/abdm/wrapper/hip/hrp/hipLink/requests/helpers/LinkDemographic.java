/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.hipLink.requests.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LinkDemographic {
  private String name;
  private String gender;
  private String dateOfBirth;
}
