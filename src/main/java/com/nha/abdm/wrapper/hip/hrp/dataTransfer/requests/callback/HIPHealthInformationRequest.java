/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataHiRequest.DataHiRequest;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HIPHealthInformationRequest implements Serializable {

  private static final long serialVersionUID = 165269402517398406L;

  public String requestId;

  public String timestamp;
  public String transactionId;
  public DataHiRequest hiRequest;
}
