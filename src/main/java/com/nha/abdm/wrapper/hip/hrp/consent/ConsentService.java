/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.consent;

import com.nha.abdm.wrapper.common.GatewayConstants;
import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.models.Consent;
import com.nha.abdm.wrapper.common.models.RespRequest;
import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.hip.HIPClient;
import com.nha.abdm.wrapper.hip.hrp.consent.requests.HIPNotification;
import com.nha.abdm.wrapper.hip.hrp.consent.requests.HIPNotifyRequest;
import com.nha.abdm.wrapper.hip.hrp.consent.requests.HIPOnNotifyRequest;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.helpers.ConsentAcknowledgement;
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
public class ConsentService implements ConsentInterface {
  private static final Logger log = LogManager.getLogger(ConsentService.class);
  private ErrorResponse errorResponse = new ErrorResponse();
  private final RequestManager requestManager;
  private final HIPClient hipClient;
  @Autowired RequestLogService requestLogService;
  @Autowired PatientService patientService;

  @Value("${consentHipOnNotifyPath}")
  private String consentHipOnNotifyPath;

  public ConsentService(RequestManager requestManager, HIPClient hipClient) {
    this.requestManager = requestManager;
    this.hipClient = hipClient;
  }

  /**
   * The callback from ABDM gateway after consentGrant for dataTransfer, POST method for /on-notify
   *
   * @param hipNotifyRequest careContext and demographics details are provided, and implement a
   *     logic to check the existence of the careContexts.
   */
  public void notifyOnReceived(HIPNotifyRequest hipNotifyRequest) throws IllegalDataStateException {
    HIPOnNotifyRequest hipOnNotifyRequest = null;
    if (hipNotifyRequest != null
        && hipNotifyRequest.getNotification() != null
        && hipNotifyRequest.getNotification().getConsentDetail() != null
        && hipNotifyRequest.getNotification().getConsentDetail().getPatient() != null) {
      HIPNotification hipNotification = hipNotifyRequest.getNotification();

      RespRequest responseRequestId =
          RespRequest.builder().requestId(hipNotifyRequest.getRequestId()).build();

      if (patientService.isCareContextPresent(
          hipNotification.getConsentDetail().getCareContexts())) {
        Consent consent =
            Consent.builder()
                .status(hipNotification.getStatus())
                .consentDetail(hipNotification.getConsentDetail())
                .signature(hipNotification.getSignature())
                .build();
        patientService.addConsent(hipNotification.getConsentDetail().getPatient().getId(), consent);
        ConsentAcknowledgement dataAcknowledgement =
            ConsentAcknowledgement.builder()
                .status("OK")
                .consentId(hipNotifyRequest.getNotification().getConsentId())
                .build();
        hipOnNotifyRequest =
            HIPOnNotifyRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .timestamp(Utils.getCurrentTimeStamp())
                .acknowledgement(dataAcknowledgement)
                .resp(responseRequestId)
                .build();
      } else {
        errorResponse.setMessage("HIP -> Mismatch of careContext");
        errorResponse.setCode(GatewayConstants.ERROR_CODE);
        log.error("OnInit body -> making error body since careContexts are not matched");
        hipOnNotifyRequest =
            HIPOnNotifyRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .timestamp(Utils.getCurrentTimeStamp())
                .error(errorResponse)
                .resp(responseRequestId)
                .build();
      }
      try {
        log.info(hipOnNotifyRequest.toString());
        ResponseEntity<GatewayGenericResponse> response =
            requestManager.fetchResponseFromGateway(consentHipOnNotifyPath, hipOnNotifyRequest);
        log.debug(consentHipOnNotifyPath + " : consentOnNotify: " + response.getStatusCode());
        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
          requestLogService.dataTransferNotify(
              hipNotifyRequest, RequestStatus.DATA_ON_NOTIFY_SUCCESS, hipOnNotifyRequest);
        } else if (Objects.nonNull(response.getBody())
            && Objects.nonNull(response.getBody().getErrorResponse())) {
          requestLogService.dataTransferNotify(
              hipNotifyRequest, RequestStatus.DATA_ON_NOTIFY_ERROR, hipOnNotifyRequest);
        }
      } catch (Exception ex) {
        String error =
            "Exception while Initiating consentOnNotify onNotify: "
                + ex.getMessage()
                + " unwrapped exception: "
                + Exceptions.unwrap(ex);
        log.debug(error);
      }
    }
  }
}
