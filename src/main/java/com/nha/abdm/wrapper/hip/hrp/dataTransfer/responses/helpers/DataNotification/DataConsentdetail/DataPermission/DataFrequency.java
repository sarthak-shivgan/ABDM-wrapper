/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.helpers.DataNotification.DataConsentdetail.DataPermission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataFrequency {
  public int repeats;
  public String unit;
  public int value;
}
