/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.models.Acknowledgement;
import com.nha.abdm.wrapper.common.models.RespRequest;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.ConsentRequestService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.FieldIdentifiers;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.RequestStatus;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.OnNotifyRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ConsentGatewayCallbackService implements ConsentGatewayCallbackInterface {

  private final RequestLogService requestLogService;
  private final HIUConsentInterface hiuConsentInterface;
  private final PatientService patientService;
  private final ConsentRequestService consentRequestService;

  private static final Logger log = LogManager.getLogger(ConsentGatewayCallbackService.class);

  @Autowired
  public ConsentGatewayCallbackService(
      RequestLogService requestLogService,
      HIUConsentInterface hiuConsentInterface,
      PatientService patientService,
      ConsentRequestService consentRequestService) {
    this.requestLogService = requestLogService;
    this.hiuConsentInterface = hiuConsentInterface;
    this.patientService = patientService;
    this.consentRequestService = consentRequestService;
  }

  @Override
  public HttpStatus onInitConsent(OnInitRequest onInitRequest) throws IllegalDataStateException {
    if (Objects.nonNull(onInitRequest)
        && Objects.nonNull(onInitRequest.getResp())
        && Objects.nonNull(onInitRequest.getConsentRequest())) {
      requestLogService.updateConsentResponse(
          onInitRequest.getResp().getRequestId(),
          FieldIdentifiers.CONSENT_ON_INIT_RESPONSE,
          RequestStatus.CONSENT_ON_INIT_RESPONSE_RECEIVED,
          onInitRequest.getConsentRequest().getId());
      // This mapping needs to be persisted in database because when gateway issues hiu notify call,
      // it passes
      // consent request id and then there is no way to track original request other that looping
      // through all the requests
      // and checking their responses for consentRequestId.
      consentRequestService.saveConsentRequest(
          onInitRequest.getConsentRequest().getId(), onInitRequest.getResp().getRequestId());
    } else if (Objects.nonNull(onInitRequest)
        && Objects.nonNull(onInitRequest.getResp())
        && Objects.nonNull(onInitRequest.getError())) {
      requestLogService.updateError(
          onInitRequest.getResp().getRequestId(),
          onInitRequest.getError().getMessage(),
          RequestStatus.CONSENT_ON_INIT_ERROR);
    } else if (Objects.nonNull(onInitRequest) && Objects.nonNull(onInitRequest.getResp())) {
      requestLogService.updateError(
          onInitRequest.getResp().getRequestId(),
          "Something went wrong while executing consent on init",
          RequestStatus.CONSENT_ON_INIT_ERROR);
      return HttpStatus.BAD_REQUEST;
    }
    return HttpStatus.ACCEPTED;
  }

  @Override
  public HttpStatus consentOnStatus(HIUConsentOnStatusRequest hiuConsentOnStatusRequest)
      throws IllegalDataStateException {
    if (Objects.nonNull(hiuConsentOnStatusRequest)
        && Objects.nonNull(hiuConsentOnStatusRequest.getConsentRequest())) {
      String gatewayRequestId =
          consentRequestService.getGatewayRequestId(
              hiuConsentOnStatusRequest.getConsentRequest().getId());
      requestLogService.updateConsentResponse(
          gatewayRequestId,
          FieldIdentifiers.CONSENT_ON_STATUS_RESPONSE,
          RequestStatus.CONSENT_ON_STATUS_RESPONSE_RECEIVED,
          hiuConsentOnStatusRequest.getConsentRequest());
    } else {
      // There is no way to track the gateway request id since gateway sent empty request. So we
      // will not be
      // able to update the error status in database.
      log.error("Something went wrong while executing consent on status");
      return HttpStatus.BAD_REQUEST;
    }
    return HttpStatus.ACCEPTED;
  }

  @Override
  public HttpStatus hiuNotify(NotifyHIURequest notifyHIURequest) throws IllegalDataStateException {
    if (Objects.nonNull(notifyHIURequest) && Objects.nonNull(notifyHIURequest.getNotification())) {
      // Get corresponding gateway request for the given consent request id.
      String gatewayRequestId =
          consentRequestService.getGatewayRequestId(
              notifyHIURequest.getNotification().getConsentRequestId());
      requestLogService.updateConsentResponse(
          gatewayRequestId,
          FieldIdentifiers.CONSENT_ON_NOTIFY_RESPONSE,
          RequestStatus.CONSENT_ON_NOTIFY_RESPONSE_RECEIVED,
          notifyHIURequest.getNotification());

      List<Acknowledgement> acknowledgements = new ArrayList<>();
      String status = notifyHIURequest.getNotification().getStatus();
      for (ConsentArtefact consentArtefact :
          notifyHIURequest.getNotification().getConsentArtefacts()) {
        acknowledgements.add(
            Acknowledgement.builder().status(status).consentId(consentArtefact.getId()).build());
      }
      OnNotifyRequest onNotifyRequest =
          OnNotifyRequest.builder()
              .requestId(UUID.randomUUID().toString())
              .timestamp(Utils.getCurrentTimeStamp())
              .acknowledgment(acknowledgements)
              .resp(RespRequest.builder().requestId(notifyHIURequest.getRequestId()).build())
              .build();
      hiuConsentInterface.hiuOnNotify(onNotifyRequest);
    } else {
      // There is no way to track the gateway request id since gateway sent empty request. So we
      // will not be
      // able to update the error status in database.
      log.error("Something went wrong while executing hiu notify");
      return HttpStatus.BAD_REQUEST;
    }
    return HttpStatus.ACCEPTED;
  }

  @Override
  public void consentOnFetch(OnFetchRequest onFetchRequest) throws IllegalDataStateException {
    if (Objects.nonNull(onFetchRequest)
        && Objects.nonNull(onFetchRequest.getConsent())
        && Objects.nonNull(onFetchRequest.getConsent().getConsentDetail())
        && Objects.nonNull(onFetchRequest.getConsent().getConsentDetail().getPatient())) {
      String patientId = onFetchRequest.getConsent().getConsentDetail().getPatient().getId();
      patientService.addConsent(patientId, onFetchRequest.getConsent());
      if (Objects.nonNull(onFetchRequest.getResp())) {
        requestLogService.updateStatus(
            onFetchRequest.getResp().getRequestId(), RequestStatus.CONSENT_ON_FETCH_SUCCESS);
      }
    } else if (Objects.nonNull(onFetchRequest)
        && Objects.nonNull(onFetchRequest.getResp())
        && Objects.nonNull(onFetchRequest.getError())) {
      requestLogService.updateError(
          onFetchRequest.getResp().getRequestId(),
          onFetchRequest.getError().getMessage(),
          RequestStatus.CONSENT_ON_FETCH_ERROR);
    } else if (Objects.nonNull(onFetchRequest) && Objects.nonNull(onFetchRequest.getResp())) {
      requestLogService.updateError(
          onFetchRequest.getResp().getRequestId(),
          "Something went wrong while executing consent on status",
          RequestStatus.CONSENT_ON_FETCH_ERROR);
    }
  }
}
