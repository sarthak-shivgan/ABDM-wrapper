/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataStatusNotification {
  public String sessionStatus;
  public String hipId;
  public List<DataStatusResponses> statusResponses;
}
