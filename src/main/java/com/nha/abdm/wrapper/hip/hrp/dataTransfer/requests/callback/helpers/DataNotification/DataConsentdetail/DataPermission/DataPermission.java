/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataNotification.DataConsentdetail.DataPermission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataPermission {
  public String accessMode;
  public String dataEraseAt;
  public DataDateRange dateRange;
  public DataFrequency frequency;
}
