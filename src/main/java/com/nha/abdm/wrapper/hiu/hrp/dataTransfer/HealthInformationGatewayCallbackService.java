/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.requests.OnHealthInformationRequest;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HealthInformationGatewayCallbackService
    implements HealthInformationGatewayCallbackInterface {

  private final RequestLogService requestLogService;

  @Autowired
  public HealthInformationGatewayCallbackService(RequestLogService requestLogService) {
    this.requestLogService = requestLogService;
  }

  @Override
  public HttpStatus onHealthInformationRequest(
      OnHealthInformationRequest onHealthInformationRequest) throws IllegalDataStateException {
    if (Objects.isNull(onHealthInformationRequest)
        || Objects.isNull(onHealthInformationRequest.getResp())
        || Objects.isNull(onHealthInformationRequest.getHiRequest())) {
      return HttpStatus.BAD_REQUEST;
    }
    String requestId = onHealthInformationRequest.getResp().getRequestId();
    requestLogService.updateTransactionId(
        requestId, onHealthInformationRequest.getHiRequest().getTransactionId());
    return HttpStatus.ACCEPTED;
  }
}
