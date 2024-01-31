/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests;

import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.DataAcknowledgement;
import com.nha.abdm.wrapper.hip.hrp.discover.requests.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataOnNotifyRequest {
  public String requestId;
  public String timestamp;
  public DataAcknowledgement acknowledgement;
  public ErrorResponse error;
  public Response resp;
}
