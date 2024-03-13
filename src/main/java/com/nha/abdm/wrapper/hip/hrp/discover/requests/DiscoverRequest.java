/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.discover.requests;

import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.helpers.PatientDemographicDetails;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DiscoverRequest implements Serializable {

  private static final long serialVersionUID = 165269402517398406L;

  public String requestId;

  public String transactionId;

  public String timestamp;

  public ErrorResponse error;

  public PatientDemographicDetails patient;
  public String hipId;
}
