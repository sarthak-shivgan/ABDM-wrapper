/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.requests.OnHealthInformationRequest;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.ConsentCipherMappingService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.RequestLog;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HealthInformationGatewayCallbackService
    implements HealthInformationGatewayCallbackInterface {

  private final RequestLogService requestLogService;
  private final LogsRepo logsRepo;
  private final ConsentCipherMappingService consentCipherMappingService;

  @Autowired
  public HealthInformationGatewayCallbackService(
      RequestLogService requestLogService,
      LogsRepo logsRepo,
      ConsentCipherMappingService consentCipherMappingService) {
    this.requestLogService = requestLogService;
    this.logsRepo = logsRepo;
    this.consentCipherMappingService = consentCipherMappingService;
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
    RequestLog requestLog = logsRepo.findByGatewayRequestId(requestId);
    if (requestLog == null) {
      throw new IllegalDataStateException("Request not found in database for: " + requestId);
    }
    String consentId = requestLog.getConsentId();
    if (StringUtils.isEmpty(consentId)) {
      throw new IllegalDataStateException(
          "Consent id not found for transaction id: "
              + onHealthInformationRequest.getHiRequest().getTransactionId());
    }
    consentCipherMappingService.updateTransactionId(
        consentId, onHealthInformationRequest.getHiRequest().getTransactionId());
    return HttpStatus.ACCEPTED;
  }
}
