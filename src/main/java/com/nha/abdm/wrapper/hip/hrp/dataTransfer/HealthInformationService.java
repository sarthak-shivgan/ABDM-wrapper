/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.GatewayConstants;
import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.models.RespRequest;
import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.hip.HIPClient;
import com.nha.abdm.wrapper.hip.HIUClient;
import com.nha.abdm.wrapper.hip.hrp.consent.requests.HIPNotifyRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.encryption.EncryptionResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.encryption.EncryptionService;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HIPHealthInformationRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HealthInformationBundle;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HealthInformationBundleRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HealthInformationPushNotification;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HealthInformationPushRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.OnHealthInformationRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.HealthInformationEntries;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.HealthInformationHiRequest.HealthInformationkeyMaterial.HealthInformationDhPublicKey;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.HealthInformationHiRequest.HealthInformationkeyMaterial.HealthInformationKeyMaterial;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.*;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.ConsentPatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.RequestLog;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.FieldIdentifiers;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.RequestStatus;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.GatewayGenericResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.ConsentCareContexts;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;

@Service
public class HealthInformationService implements HealthInformationInterface {
  private static final Logger log = LogManager.getLogger(HealthInformationService.class);

  @Value("${healthInformationOnRequestPath}")
  public String healthInformationOnRequestPath;

  @Value("${healthInformationPushNotificationPath}")
  public String healthInformationPushNotificationPath;

  @Autowired PatientRepo patientRepo;
  @Autowired LogsRepo logsRepo;
  private final RequestManager requestManager;
  private final HIPClient hipClient;
  private final HIUClient hiuClient;
  @Autowired RequestLogService requestLogService;
  @Autowired PatientService patientService;
  @Autowired ConsentPatientService consentPatientService;
  @Autowired EncryptionService encryptionService;

  @Autowired
  public HealthInformationService(
      HIPClient hipClient, HIUClient hiuClient, RequestManager requestManager) {
    this.hipClient = hipClient;
    this.requestManager = requestManager;
    this.hiuClient = hiuClient;
  }

  /**
   * POST /on-request as an acknowledgement for agreeing to make dataTransfer to ABDM gateway.
   *
   * @param hipHealthInformationRequest HIU public keys and dataPush URL is provided
   */
  @Override
  public void healthInformation(HIPHealthInformationRequest hipHealthInformationRequest)
      throws IllegalDataStateException, InvalidAlgorithmParameterException,
          NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException,
          InvalidKeyException {
    OnHealthInformationRequest onHealthInformationRequest = null;
    RespRequest responseRequestId =
        RespRequest.builder().requestId(hipHealthInformationRequest.getRequestId()).build();
    String consentId = hipHealthInformationRequest.getHiRequest().getConsent().getId();
    HiRequestStatus hiRequestStatus =
        HiRequestStatus.builder()
            .sessionStatus("ACKNOWLEDGED")
            .transactionId(hipHealthInformationRequest.getTransactionId())
            .build();
    // Lookup in consent patient table is good enough as we are saving mapping when we are saving
    // consent in patient table.
    boolean requestedPatientConsent = consentPatientService.findMappingByConsentId(consentId);
    if (requestedPatientConsent) {
      onHealthInformationRequest =
          OnHealthInformationRequest.builder()
              .requestId(UUID.randomUUID().toString())
              .timestamp(Utils.getCurrentTimeStamp())
              .hiRequestStatus(hiRequestStatus)
              .resp(responseRequestId)
              .build();
    } else {
      String error = "ConsentId not found in database " + consentId;
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage(error);
      errorResponse.setCode(GatewayConstants.ERROR_CODE);
      log.error(error);
      onHealthInformationRequest =
          OnHealthInformationRequest.builder()
              .requestId(UUID.randomUUID().toString())
              .timestamp(Utils.getCurrentTimeStamp())
              .error(errorResponse)
              .resp(responseRequestId)
              .build();
    }
    log.debug(
        "health information acknowledgment request body : "
            + onHealthInformationRequest.toString());
    healthInformationAcknowledgementRequest(
        hipHealthInformationRequest, onHealthInformationRequest);
    HealthInformationBundle healthInformationBundle =
        fetchHealthInformationBundle(hipHealthInformationRequest);
    pushHealthInformation(healthInformationBundle, consentId);
    healthInformationPushAcknowledge(hipHealthInformationRequest, consentId);
  }

  /**
   * Requesting HIP for FHIR bundle
   *
   * @param hipHealthInformationRequest use the requestId to fetch the careContexts from dump to
   *     request HIP.
   */
  private HealthInformationBundle fetchHealthInformationBundle(
      HIPHealthInformationRequest hipHealthInformationRequest) throws IllegalDataStateException {
    String consentId = hipHealthInformationRequest.getHiRequest().getConsent().getId();
    RequestLog existingLog = logsRepo.findByConsentId(consentId);
    if (existingLog == null) {
      throw new IllegalDataStateException("consent id not found in db");
    }
    HIPNotifyRequest hipNotifyRequest =
        (HIPNotifyRequest) existingLog.getRequestDetails().get(FieldIdentifiers.HIP_NOTIFY_REQUEST);
    HealthInformationBundleRequest healthInformationBundleRequest =
        HealthInformationBundleRequest.builder()
            .careContextsWithPatientReferences(
                hipNotifyRequest.getNotification().getConsentDetail().getCareContexts())
            .build();
    log.debug("Health information bundle request HIP : " + healthInformationBundleRequest.toString());
    return hipClient.healthInformationBundleRequest(healthInformationBundleRequest).getBody();
  }

  /**
   * Encrypt the bundle and POST to /dataPushUrl of HIU
   *
   * @param healthInformationBundle FHIR bundle received from HIP for the particular patients
   */
  private void pushHealthInformation(
      HealthInformationBundle healthInformationBundle, String consentId)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException,
          NoSuchProviderException, InvalidKeyException {
    log.info(healthInformationBundle.toString());
    RequestLog existingRecord = logsRepo.findByConsentId(consentId);

    HIPNotifyRequest hipNotifyRequest =
        (HIPNotifyRequest)
            existingRecord.getRequestDetails().get(FieldIdentifiers.HIP_NOTIFY_REQUEST);

    HIPHealthInformationRequest hipHealthInformationRequest =
        (HIPHealthInformationRequest)
            existingRecord.getRequestDetails().get(FieldIdentifiers.HEALTH_INFORMATION_REQUEST);
    HealthInformationPushRequest healthInformationPushRequest =
        fetchHealthInformationPushRequest(
            hipNotifyRequest, hipHealthInformationRequest, healthInformationBundle);

    log.debug("Health Information push request: " + healthInformationPushRequest);
    log.info("initiating the dataTransfer to HIU");
    hiuClient.pushHealthInformation(
        hipHealthInformationRequest.getHiRequest().getDataPushUrl(), healthInformationPushRequest);
  }

  private HealthInformationPushRequest fetchHealthInformationPushRequest(
      HIPNotifyRequest hipNotifyRequest,
      HIPHealthInformationRequest hipHealthInformationRequest,
      HealthInformationBundle healthInformationBundle)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException,
          NoSuchProviderException, InvalidKeyException {
    EncryptionResponse encryptedData =
        encryptionService.encrypt(hipHealthInformationRequest, healthInformationBundle);

    HealthInformationDhPublicKey receiverDhPublicKey =
        hipHealthInformationRequest.getHiRequest().getKeyMaterial().getDhPublicKey();

    HealthInformationDhPublicKey dhPublicKey =
        HealthInformationDhPublicKey.builder()
            .expiry(receiverDhPublicKey.getExpiry())
            .parameters(receiverDhPublicKey.getParameters())
            .keyValue(encryptedData.getKeyToShare())
            .build();

    HealthInformationKeyMaterial keyMaterial =
        HealthInformationKeyMaterial.builder()
            .cryptoAlg(hipHealthInformationRequest.getHiRequest().getKeyMaterial().getCryptoAlg())
            .curve(hipHealthInformationRequest.getHiRequest().getKeyMaterial().getCurve())
            .dhPublicKey(dhPublicKey)
            .nonce(encryptedData.getSenderNonce())
            .build();
    List<HealthInformationEntries> entries = new ArrayList<>();
    List<String> careContextReferenceList =
        hipNotifyRequest.getNotification().getConsentDetail().getCareContexts().stream()
            .map(ConsentCareContexts::getCareContextReference)
            .toList();
    for (String careContextReference : careContextReferenceList) {
      HealthInformationEntries healthInformationEntries =
          HealthInformationEntries.builder()
              .content(encryptedData.getEncryptedData())
              .media("application/fhir+json")
              .checksum("string")
              .careContextReference(careContextReference)
              .build();
      entries.add(healthInformationEntries);
    }
    return HealthInformationPushRequest.builder()
        .keyMaterial(keyMaterial)
        .entries(entries)
        .pageCount(1)
        .pageNumber(0)
        .transactionId(hipHealthInformationRequest.getTransactionId())
        .build();
  }

  /**
   * After successful dataTransfer we need to send an acknowledgment to ABDM gateway saying
   * "TRANSFERRED"
   *
   * @param consentId to get the careContexts of the patient from requestLogs
   * @param hipHealthInformationRequest which has the transactionId used to POST acknowledgement
   */
  private void healthInformationPushAcknowledge(
      HIPHealthInformationRequest hipHealthInformationRequest, String consentId) {
    HIPNotifyRequest hipNotifyRequest =
        (HIPNotifyRequest)
            logsRepo
                .findByConsentId(consentId)
                .getRequestDetails()
                .get(FieldIdentifiers.HIP_NOTIFY_REQUEST);
    List<ConsentCareContexts> listOfCareContexts =
        hipNotifyRequest.getNotification().getConsentDetail().getCareContexts();
    List<HealthInformationStatusResponse> healthInformationStatusResponseList = new ArrayList<>();
    for (ConsentCareContexts item : listOfCareContexts) {
      HealthInformationStatusResponse healthInformationStatusResponse =
          HealthInformationStatusResponse.builder()
              .careContextReference(item.getCareContextReference())
              .hiStatus("OK")
              .description("Done")
              .build();
      healthInformationStatusResponseList.add(healthInformationStatusResponse);
    }
    HealthInformationStatusNotification healthInformationStatusNotification =
        HealthInformationStatusNotification.builder()
            .sessionStatus("TRANSFERRED")
            .hipId("hipId")
            .statusResponses(healthInformationStatusResponseList)
            .build();
    HealthInformationNotifier healthInformationNotifier =
        HealthInformationNotifier.builder()
            .type("HIP")
            .id(hipNotifyRequest.getNotification().getConsentDetail().getHip().getId())
            .build();
    HealthInformationNotificationStatus healthInformationNotificationStatus =
        HealthInformationNotificationStatus.builder()
            .consentId(hipNotifyRequest.getNotification().getConsentId())
            .transactionId(hipHealthInformationRequest.getTransactionId())
            .doneAt(Utils.getCurrentTimeStamp())
            .notifier(healthInformationNotifier)
            .statusNotification(healthInformationStatusNotification)
            .build();
    HealthInformationPushNotification healthInformationPushNotification =
        HealthInformationPushNotification.builder()
            .requestId(UUID.randomUUID().toString())
            .timestamp(Utils.getCurrentTimeStamp())
            .notification(healthInformationNotificationStatus)
            .build();
    log.info(healthInformationPushNotification.toString());
    ResponseEntity<GatewayGenericResponse> response =
        requestManager.fetchResponseFromGateway(
            healthInformationPushNotificationPath, healthInformationPushNotification);
    log.debug(
        healthInformationPushNotificationPath
            + " : healthInformationPushNotification: "
            + response.getStatusCode());
  }

  private void healthInformationAcknowledgementRequest(
      HIPHealthInformationRequest hipHealthInformationRequest,
      OnHealthInformationRequest onHealthInformationRequest) {
    try {
      ResponseEntity<GatewayGenericResponse> response =
          requestManager.fetchResponseFromGateway(
              healthInformationOnRequestPath, onHealthInformationRequest);
      log.debug(healthInformationOnRequestPath + " : dataOnRequest: " + response.getStatusCode());
      if (response.getStatusCode().is2xxSuccessful()) {
        requestLogService.saveHealthInformationRequest(
            hipHealthInformationRequest, RequestStatus.HEALTH_INFORMATION_ON_REQUEST_SUCCESS);
      } else if (Objects.nonNull(response.getBody())
          && Objects.nonNull(response.getBody().getErrorResponse())) {
        requestLogService.saveHealthInformationRequest(
            hipHealthInformationRequest, RequestStatus.HEALTH_INFORMATION_ON_REQUEST_ERROR);
      }
    } catch (Exception ex) {
      String error =
          "An unknown error occurred while calling Gateway API: "
              + ex.getMessage()
              + " unwrapped exception: "
              + Exceptions.unwrap(ex);
      log.debug(error);
    }
  }
}
