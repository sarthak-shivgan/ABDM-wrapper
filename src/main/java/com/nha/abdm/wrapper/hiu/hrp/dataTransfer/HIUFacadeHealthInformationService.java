/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.cipher.CipherKeyManager;
import com.nha.abdm.wrapper.common.cipher.Key;
import com.nha.abdm.wrapper.common.requests.HealthInformationDhPublicKey;
import com.nha.abdm.wrapper.common.requests.HealthInformationKeyMaterial;
import com.nha.abdm.wrapper.common.requests.HealthInformationRequest;
import com.nha.abdm.wrapper.common.requests.IdRequest;
import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.common.responses.GenericResponse;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.ConsentCipherPrivateKeyMappingService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.RequestStatus;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.DateRange;
import com.nha.abdm.wrapper.hiu.hrp.dataTransfer.requests.HIUClientHealthInformationRequest;
import com.nha.abdm.wrapper.hiu.hrp.dataTransfer.requests.HIUGatewayHealthInformationRequest;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class HIUFacadeHealthInformationService implements HIUFacadeHealthInformationInterface {

  private static final Logger log = LogManager.getLogger(HIUFacadeHealthInformationService.class);

  @Value("${healthInformationConsentManagerPath}")
  private String healthInformationConsentManagerPath;

  @Value("${dataPushUrl}")
  private String dataPushUrl;

  private final RequestManager requestManager;
  private final RequestLogService requestLogService;
  private final CipherKeyManager cipherKeyManager;
  private final ConsentCipherPrivateKeyMappingService consentCipherPrivateKeyMappingService;

  @Autowired
  public HIUFacadeHealthInformationService(
      RequestManager requestManager,
      RequestLogService requestLogService,
      CipherKeyManager cipherKeyManager,
      ConsentCipherPrivateKeyMappingService consentCipherPrivateKeyMappingService) {
    this.requestManager = requestManager;
    this.requestLogService = requestLogService;
    this.cipherKeyManager = cipherKeyManager;
    this.consentCipherPrivateKeyMappingService = consentCipherPrivateKeyMappingService;
  }

  @Override
  public FacadeResponse healthInformation(
      HIUClientHealthInformationRequest hiuClientHealthInformationRequest)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
    if (Objects.isNull(hiuClientHealthInformationRequest)) {
      return FacadeResponse.builder().httpStatusCode(HttpStatus.BAD_REQUEST).build();
    }
    HIUGatewayHealthInformationRequest hiuGatewayHealthInformationRequest =
        getHiuGatewayHealthInformationRequest(hiuClientHealthInformationRequest);
    ResponseEntity<GenericResponse> response =
        requestManager.fetchResponseFromGateway(
            healthInformationConsentManagerPath, hiuGatewayHealthInformationRequest);
    if (response.getStatusCode().is2xxSuccessful()) {
      requestLogService.saveRequest(
          hiuGatewayHealthInformationRequest.getRequestId(),
          RequestStatus.HEALTH_INFORMATION_REQUEST_SUCCESS,
          null);
      return FacadeResponse.builder().httpStatusCode(HttpStatus.ACCEPTED).build();
    } else {
      String error = "Something went wrong while posting health information request to gateway";
      log.error(error);
      requestLogService.saveRequest(
          hiuGatewayHealthInformationRequest.getRequestId(),
          RequestStatus.HEALTH_INFORMATION_REQUEST_ERROR,
          error);
      return FacadeResponse.builder()
          .error(ErrorResponse.builder().message(error).build())
          .httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
    }
  }

  private HIUGatewayHealthInformationRequest getHiuGatewayHealthInformationRequest(
      HIUClientHealthInformationRequest hiuClientHealthInformationRequest)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
    Key key = cipherKeyManager.fetchKeys();
    HealthInformationDhPublicKey healthInformationDhPublicKey =
        HealthInformationDhPublicKey.builder()
            .expiry(hiuClientHealthInformationRequest.getExpiry())
            .parameters(CipherKeyManager.PARAMETERS)
            .keyValue(key.getPublicKey())
            .build();
    HealthInformationKeyMaterial healthInformationKeyMaterial =
        HealthInformationKeyMaterial.builder()
            .cryptoAlg(CipherKeyManager.ALGORITHM)
            .curve(CipherKeyManager.CURVE)
            .dhPublicKey(healthInformationDhPublicKey)
            .nonce(key.getNonce())
            .build();
    HealthInformationRequest healthInformationRequest =
        HealthInformationRequest.builder()
            .consent(
                IdRequest.builder().id(hiuClientHealthInformationRequest.getConsentId()).build())
            .dateRange(
                DateRange.builder()
                    .from(hiuClientHealthInformationRequest.getFromDate())
                    .to(hiuClientHealthInformationRequest.getToDate())
                    .build())
            .dataPushUrl(dataPushUrl)
            .keyMaterial(healthInformationKeyMaterial)
            .build();
    consentCipherPrivateKeyMappingService.saveConsentPrivateKeyMapping(
        hiuClientHealthInformationRequest.getConsentId(), key.getPrivateKey());
    return HIUGatewayHealthInformationRequest.builder()
        .requestId(hiuClientHealthInformationRequest.getRequestId())
        .timestamp(Utils.getCurrentTimeStamp())
        .hiRequest(healthInformationRequest)
        .build();
  }
}
