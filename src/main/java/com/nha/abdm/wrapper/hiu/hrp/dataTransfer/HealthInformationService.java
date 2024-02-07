/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.requests.HealthInformationKeyMaterial;
import com.nha.abdm.wrapper.common.requests.HealthInformationPushRequest;
import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.common.responses.GenericResponse;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HealthInformationService implements HealthInformationInterface {

  private final RequestLogService requestLogService;

  public HealthInformationService(RequestLogService requestLogService) {
    this.requestLogService = requestLogService;
  }

  @Override
  public GenericResponse processEncryptedHealthInformation(
      HealthInformationPushRequest healthInformationPushRequest) {
    if (Objects.isNull(healthInformationPushRequest)
        || Objects.isNull(healthInformationPushRequest.getEntries())) {
      return GenericResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build();
    }
    String transactionId = healthInformationPushRequest.getTransactionId();
    HealthInformationKeyMaterial keyMaterial = healthInformationPushRequest.getKeyMaterial();
    if (StringUtils.isEmpty(transactionId)) {
      return GenericResponse.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorResponse(ErrorResponse.builder().message("Invalid transaction id").build())
          .build();
    }
    if (Objects.isNull(keyMaterial)
        || Objects.isNull(keyMaterial.getNonce())
        || Objects.isNull(keyMaterial.getDhPublicKey())
        || StringUtils.isEmpty(keyMaterial.getDhPublicKey().getKeyValue())) {
      return GenericResponse.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorResponse(ErrorResponse.builder().message("Invalid key material").build())
          .build();
    }
    // TODO: Expiry check, and also should it be done on HIP side? I think it should.
    boolean requestLogFound = requestLogService.findRequestLogByTransactionId(transactionId);
    if (!requestLogFound) {
      return GenericResponse.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorResponse(ErrorResponse.builder().message("Transaction id not found").build())
          .build();
    }
    // TODO: Save the encrypted health information in database and
    // in sample hiu have a periodic poll using the same request id which as was used to invoke data
    // transfer,
    // Now using the same request id sample-hiu should hit wrapper endpoint(to be implemented to
    // check for request status
    // and if the request log has got response, it should reply post decrypting it.
    // requestLogService.saveEncryptedHealthInformation();

    // TODO: At the end we need to notify gateway.

    return GenericResponse.builder().httpStatus(HttpStatus.OK).build();
  }
}
