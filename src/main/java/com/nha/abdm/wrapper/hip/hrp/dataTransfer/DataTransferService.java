/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.GatewayConstants;
import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.dataPackaging.encryption.EncryptionController;
import com.nha.abdm.wrapper.common.dataPackaging.encryption.EncryptionResponse;
import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.models.RespRequest;
import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hip.HIPClient;
import com.nha.abdm.wrapper.hip.hrp.consent.requests.HIPNotifyRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.DataPushNotification;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.DataPushRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HIPHealthInformationRequestAcknowledgement;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HIPRequestBundle;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.BundleResponseHIP;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.HIPHealthInformationRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataEntries;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataHiRequest.DatakeyMaterial.DataDhPublicKey;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataHiRequest.DatakeyMaterial.DataKeyMaterial;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.*;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.RequestLog;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.FieldIdentifiers;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.RequestStatus;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.GatewayGenericResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.ConsentCareContexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;

@Service
public class DataTransferService implements DataTransferInterface {
  private static final Logger log = LogManager.getLogger(DataTransferService.class);

  @Value("${facilityUrl}")
  public String facilityUrlPath;

  @Value("${dataOnRequestPath}")
  public String dataOnRequestPath;

  @Value("${dataPushNotificationPath}")
  public String dataPushNotificationPath;

  @Autowired PatientRepo patientRepo;
  @Autowired LogsRepo logsRepo;
  private ErrorResponse errorResponse = new ErrorResponse();
  private final RequestManager requestManager;
  private final HIPClient hipClient;
  @Autowired RequestLogService requestLogService;
  @Autowired PatientService patientService;
  @Autowired EncryptionController encryptionController;

  @Autowired
  public DataTransferService(HIPClient hipClient, RequestManager requestManager) {
    this.hipClient = hipClient;
    this.requestManager = requestManager;
  }

  /**
   * POST /on-request as an acknowledgement for agreeing to make dataTransfer to ABDM gateway.
   *
   * @param hipHealthInformationRequest HIU public keys and dataPush URL is provided
   */
  @Override
  public void requestOnReceived(HIPHealthInformationRequest hipHealthInformationRequest)
      throws IllegalDataStateException {
    HIPHealthInformationRequestAcknowledgement hipHealthInformationRequestAcknowledgement = null;
    RespRequest responseRequestId =
        RespRequest.builder().requestId(hipHealthInformationRequest.getRequestId()).build();
    String consentId = hipHealthInformationRequest.getHiRequest().getConsent().getId();
    HiRequest hiRequest =
        HiRequest.builder()
            .sessionStatus("ACKNOWLEDGED")
            .transactionId(hipHealthInformationRequest.getTransactionId())
            .build();
    if (logsRepo.findByConsentId(consentId) != null) {
      hipHealthInformationRequestAcknowledgement =
          HIPHealthInformationRequestAcknowledgement.builder()
              .requestId(UUID.randomUUID().toString())
              .timestamp(Utils.getCurrentTimeStamp())
              .hiRequest(hiRequest)
              .resp(responseRequestId)
              .build();
    } else {
      errorResponse.setMessage("HIP -> ConsentId not found in cache");
      errorResponse.setCode(GatewayConstants.ERROR_CODE);
      log.error("DataOnRequest body -> making error body, consentId not found in cache");
      hipHealthInformationRequestAcknowledgement =
          HIPHealthInformationRequestAcknowledgement.builder()
              .requestId(UUID.randomUUID().toString())
              .timestamp(Utils.getCurrentTimeStamp())
              .error(errorResponse)
              .resp(responseRequestId)
              .build();
    }
    log.info(hipHealthInformationRequestAcknowledgement.toString());
    try {
      ResponseEntity<GatewayGenericResponse> response =
          requestManager.fetchResponseFromGateway(
              dataOnRequestPath, hipHealthInformationRequestAcknowledgement);
      log.debug(dataOnRequestPath + " : dataOnRequest: " + response.getStatusCode());
      if (response.getStatusCode() == HttpStatus.ACCEPTED) {
        requestLogService.dataTransferRequest(
            hipHealthInformationRequest,
            RequestStatus.DATA_ON_REQUEST_SUCCESS,
            hipHealthInformationRequestAcknowledgement);
      } else if (Objects.nonNull(response.getBody())
          && Objects.nonNull(response.getBody().getErrorResponse())) {
        requestLogService.dataTransferRequest(
            hipHealthInformationRequest,
            RequestStatus.DATA_ON_REQUEST_ERROR,
            hipHealthInformationRequestAcknowledgement);
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

  /**
   * Requesting HIP for FHIR bundle
   *
   * @param hipHealthInformationRequest use the requestId to fetch the careContexts from dump to
   *     request HIP.
   */
  @Override
  public void initiateBundleRequest(HIPHealthInformationRequest hipHealthInformationRequest)
      throws IllegalDataStateException {
    RequestLog existingLog =
        logsRepo.findByConsentId(hipHealthInformationRequest.getHiRequest().getConsent().getId());
    if (existingLog == null) {
      throw new IllegalDataStateException("consent id not found in db");
    }
    HIPNotifyRequest hipNotifyRequest =
        (HIPNotifyRequest)
            existingLog.getRequestDetails().get(FieldIdentifiers.DATA_NOTIFY_REQUEST);
    HIPRequestBundle hipRequestBundle =
        HIPRequestBundle.builder()
            .careContextsWithPatientReferences(
                hipNotifyRequest.getNotification().getConsentDetail().getCareContexts())
            .consentId(hipHealthInformationRequest.getHiRequest().getConsent().getId())
            .build();
    try {
      ResponseEntity<GatewayGenericResponse> response =
          requestManager.fetchResponseFromGateway(facilityUrlPath, hipRequestBundle);
      log.debug(facilityUrlPath + " : bundleRequest: " + response.getStatusCode());
    } catch (Exception ex) {
      String error =
          "Exception while requesting FHIR BUNDLE HIP : "
              + ex.getMessage()
              + " unwrapped exception: "
              + Exceptions.unwrap(ex);
      log.debug(error);
    }
  }

  /**
   * Encrypt the bundle and POST to /dataPushUrl of HIU
   *
   * @param bundleResponseHIP FHIR bundle received from HIP for the particular patients
   */
  @Override
  public FacadeResponse initiateDataTransfer(BundleResponseHIP bundleResponseHIP) throws Exception {
    log.info(bundleResponseHIP.toString());
    String consentId = bundleResponseHIP.getConsentId();
    RequestLog existingRecord = logsRepo.findByConsentId(consentId);

    HIPNotifyRequest hipNotifyRequest =
        (HIPNotifyRequest)
            existingRecord.getRequestDetails().get(FieldIdentifiers.DATA_NOTIFY_REQUEST);

    HIPHealthInformationRequest hipHealthInformationRequest =
        (HIPHealthInformationRequest)
            existingRecord.getRequestDetails().get(FieldIdentifiers.DATA_REQUEST);

    EncryptionResponse encryptedData =
        encryptionController.encrypt(hipHealthInformationRequest, bundleResponseHIP);
    DataDhPublicKey receiverDhPublicKey =
        hipHealthInformationRequest.getHiRequest().getKeyMaterial().getDhPublicKey();

    DataDhPublicKey dhPublicKey =
        DataDhPublicKey.builder()
            .expiry(receiverDhPublicKey.getExpiry())
            .parameters(receiverDhPublicKey.getParameters())
            .keyValue(encryptedData.getKeyToShare())
            .build();

    DataKeyMaterial keyMaterial =
        DataKeyMaterial.builder()
            .cryptoAlg(hipHealthInformationRequest.getHiRequest().getKeyMaterial().getCryptoAlg())
            .curve(hipHealthInformationRequest.getHiRequest().getKeyMaterial().getCurve())
            .dhPublicKey(dhPublicKey)
            .nonce(encryptedData.getSenderNonce())
            .build();
    List<DataEntries> entries = new ArrayList<>();
    List<String> careContextReferenceList =
        hipNotifyRequest.getNotification().getConsentDetail().getCareContexts().stream()
            .map(ConsentCareContexts::getCareContextReference)
            .toList();
    for (String careContextReference : careContextReferenceList) {
      DataEntries dataEntries =
          DataEntries.builder()
              .content(encryptedData.getEncryptedData())
              .media("application/fhir+json")
              .checksum("string")
              .careContextReference(careContextReference)
              .build();
      entries.add(dataEntries);
    }
    DataPushRequest dataPushRequest =
        DataPushRequest.builder()
            .keyMaterial(keyMaterial)
            .entries(entries)
            .pageCount(1)
            .pageNumber(0)
            .transactionId(hipHealthInformationRequest.getTransactionId())
            .build();
    log.info(dataPushRequest.toString());
    log.info("initiating the dataTransfer to HIU");
    try {
      ResponseEntity<GatewayGenericResponse> response =
          requestManager.postToHIU(
              hipHealthInformationRequest.getHiRequest().getDataPushUrl(), dataPushRequest);
      log.debug(
          hipHealthInformationRequest.getHiRequest().getDataPushUrl()
              + " : dataPushHIU: "
              + response.getStatusCode());
      initiateBundleSentNotification(hipNotifyRequest, hipHealthInformationRequest);
      return FacadeResponse.builder()
          .httpStatusCode(response.getStatusCode())
          .message("Successfully made request to HIU")
          .build();
    } catch (Exception ex) {
      String error =
          "Exception while Initiating dataTransfer HIU: "
              + ex.getMessage()
              + " unwrapped exception: "
              + Exceptions.unwrap(ex);
      log.debug(error);
      return FacadeResponse.builder()
          .code(GatewayConstants.ERROR_CODE)
          .error(ErrorResponse.builder().message(Exceptions.unwrap(ex).getMessage()).build())
          .build();
    }
  }

  /**
   * After successful dataTransfer we need to send an acknowledgment to ABDM gateway saying
   * "TRANSFERRED"
   *
   * @param hipNotifyRequest to get the careContexts of the patient
   * @param hipHealthInformationRequest which has the transactionId used to POST acknowledgement
   */
  private void initiateBundleSentNotification(
      HIPNotifyRequest hipNotifyRequest, HIPHealthInformationRequest hipHealthInformationRequest) {
    List<ConsentCareContexts> listOfCareContexts =
        hipNotifyRequest.getNotification().getConsentDetail().getCareContexts();
    List<DataStatusResponses> dataStatusResponsesList = new ArrayList<>();
    for (ConsentCareContexts item : listOfCareContexts) {
      DataStatusResponses dataStatusResponses =
          DataStatusResponses.builder()
              .careContextReference(item.getCareContextReference())
              .hiStatus("OK")
              .description("Done")
              .build();
      dataStatusResponsesList.add(dataStatusResponses);
    }
    DataStatusNotification dataStatusNotification =
        DataStatusNotification.builder()
            .sessionStatus("TRANSFERRED")
            .hipId("hipId")
            .statusResponses(dataStatusResponsesList)
            .build();
    DataNotifier dataNotifier =
        DataNotifier.builder()
            .type("HIP")
            .id(hipNotifyRequest.getNotification().getConsentDetail().getHip().getId())
            .build();
    DataNotificationStatus dataNotificationStatus =
        DataNotificationStatus.builder()
            .consentId(hipNotifyRequest.getNotification().getConsentId())
            .transactionId(hipHealthInformationRequest.getTransactionId())
            .doneAt(Utils.getCurrentTimeStamp())
            .notifier(dataNotifier)
            .statusNotification(dataStatusNotification)
            .build();
    DataPushNotification dataPushNotification =
        DataPushNotification.builder()
            .requestId(UUID.randomUUID().toString())
            .timestamp(Utils.getCurrentTimeStamp())
            .notification(dataNotificationStatus)
            .build();
    log.info(dataPushNotification.toString());
    try {
      ResponseEntity<GatewayGenericResponse> response =
          requestManager.fetchResponseFromGateway(dataPushNotificationPath, dataPushNotification);
      log.debug(dataPushNotificationPath + " : dataPushNotification: " + response.getStatusCode());
    } catch (Exception ex) {
      String error =
          "An unknown error occurred while calling Gateway API HIP auth: "
              + ex.getMessage()
              + " unwrapped exception: "
              + Exceptions.unwrap(ex);
      log.debug(error);
    }
  }
}
