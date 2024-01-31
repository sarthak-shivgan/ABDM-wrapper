/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests;

import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.DataHiStatus;
import com.nha.abdm.wrapper.hip.hrp.discover.requests.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataOnRequest {
  public String requestId;
  public String timestamp;
  public DataHiStatus hiRequest;
  public ErrorResponse error;
  public Response resp;
}
