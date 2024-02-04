/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.models.RespRequest;
import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.hip.HIPClient;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HIPConsentNotificationResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.HIPHealthInformationRequestAcknowledgement;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.BundleResponseHIP;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.HIPConsentNotification;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.HIPHealthInformationRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.ConsentAcknowledgement;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.HiRequest;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.RequestStatus;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.GatewayGenericResponse;

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

  @Value("${dataOnNotifyPath}")
  public String dataOnNotifyPath;

  @Value("${dataOnRequestPath}")
  public String dataOnRequestPath;

  @Value("${facilityUrl}")
  public String facilityUrl;

  @Autowired PatientRepo patientRepo;
  @Autowired LogsRepo logsRepo;
  private ErrorResponse errorResponse = new ErrorResponse();
  private final RequestManager requestManager;
  private final HIPClient hipClient;
  @Autowired RequestLogService requestLogService;
  @Autowired
  PatientService patientService;

  @Autowired
  public DataTransferService(HIPClient hipClient, RequestManager requestManager) {
    this.hipClient = hipClient;
    this.requestManager = requestManager;
  }

  /**
   * The callback from ABDM gateway after consentGrant for dataTransfer, POST method for /on-notify
   *
   * @param HIPConsentNotification careContext and demographics details are provided, and implement
   *     a logic to check the existence of the careContexts.
   */
  public void notifyOnReceived(HIPConsentNotification hipConsentNotification) {
    HIPConsentNotificationResponse hipConsentNotificationResponse = null;

    RespRequest responseRequestId =
        RespRequest.builder().requestId(hipConsentNotification.getRequestId()).build();

    if (patientService.isCareContextPresent(
        hipConsentNotification.getNotification().getConsentDetail().getCareContexts())) {
      ConsentAcknowledgement dataAcknowledgement =
          ConsentAcknowledgement.builder()
              .status("OK")
              .consentId(hipConsentNotification.getNotification().getConsentId())
              .build();
      hipConsentNotificationResponse =
          HIPConsentNotificationResponse.builder()
              .requestId(UUID.randomUUID().toString())
              .timestamp(Utils.getCurrentTimeStamp())
              .acknowledgement(dataAcknowledgement)
              .resp(responseRequestId)
              .build();
    } else {
      errorResponse.setMessage("HIP -> Mismatch of careContext");
      errorResponse.setCode(1000);
      log.error("OnInit body -> making error body since careContexts are not matched");
      hipConsentNotificationResponse =
          HIPConsentNotificationResponse.builder()
              .requestId(UUID.randomUUID().toString())
              .timestamp(Utils.getCurrentTimeStamp())
              .error(errorResponse)
              .resp(responseRequestId)
              .build();
    }
    try {
      log.info(hipConsentNotificationResponse.toString());
      ResponseEntity<GatewayGenericResponse> response =
          requestManager.fetchResponseFromGateway(dataOnNotifyPath, hipConsentNotificationResponse);
      log.debug(dataOnNotifyPath + " : dataOnNotify: " + response.getStatusCode());
      if (response.getStatusCode() == HttpStatus.ACCEPTED) {
        requestLogService.dataTransferNotify(
            hipConsentNotification,
            RequestStatus.DATA_ON_NOTIFY_SUCCESS,
            hipConsentNotificationResponse);
      } else if (Objects.nonNull(response.getBody())
          && Objects.nonNull(response.getBody().getErrorResponse())) {
        requestLogService.dataTransferNotify(
            hipConsentNotification,
            RequestStatus.DATA_ON_NOTIFY_ERROR,
            hipConsentNotificationResponse);
      }
    } catch (Exception ex) {
      String error =
          "Exception while Initiating dataTransfer onNotify: "
              + ex.getMessage()
              + " unwrapped exception: "
              + Exceptions.unwrap(ex);
      log.debug(error);
    }
  }

  /**
   * POST /on-request as an acknowledgement for agreeing to make dataTransfer to ABDM gateway.
   *
   * @param HIPHealthInformationRequest HIU public keys and dataPush URL is provided
   */
  @Override
  public void
  requestOnReceived(HIPHealthInformationRequest hipHealthInformationRequest) {
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
      errorResponse.setCode(1000);
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
          "Exception while Initiating HIP auth: "
              + ex.getMessage()
              + " unwrapped exception: "
              + Exceptions.unwrap(ex);
      log.debug(error);
    }

    //    initiateBundleRequest(HIPHealthInformationRequest);
  }

  /**
   * Requesting HIP for FHIR bundle
   *
   * @param HIPHealthInformationRequest use the requestId to fetch the careContexts from dump to
   *     request HIP.
   */
  @Override
  public void initiateBundleRequest(HIPHealthInformationRequest HIPHealthInformationRequest) {
    // TODO Implement bundle request to Facility workflow logic.
  }

  /**
   * Encrypt the bundle and POST to /dataPushUrl of HIU
   *
   * @param bundleResponseHIP FHIR bundle received from HIP for the particular patients
   */
  @Override
  public void initiateDataTransfer(BundleResponseHIP bundleResponseHIP) {
    // TODO Implement dataPush workflow logic.
  }
}
