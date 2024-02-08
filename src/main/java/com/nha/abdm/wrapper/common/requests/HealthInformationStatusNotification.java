/* (C) 2024 */
package com.nha.abdm.wrapper.common.requests;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HealthInformationStatusNotification {
  public String sessionStatus;
  public String hipId;
  public List<HealthInformationStatusResponse> statusResponses;
}
