/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.models.Acknowledgement;
import com.nha.abdm.wrapper.common.models.RespRequest;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.RequestStatus;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.OnNotifyRequest;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GatewayCallbackService implements GatewayCallbackInterface {

  private final RequestLogService requestLogService;
  private final HIUConsentInterface hiuConsentInterface;
  private final PatientService patientService;

  @Autowired
  public GatewayCallbackService(
      RequestLogService requestLogService,
      HIUConsentInterface hiuConsentInterface,
      PatientService patientService) {
    this.requestLogService = requestLogService;
    this.hiuConsentInterface = hiuConsentInterface;
    this.patientService = patientService;
  }

  @Override
  public void onInitConsent(OnInitRequest onInitRequest) {
    if (Objects.nonNull(onInitRequest)
        && Objects.nonNull(onInitRequest.getResp())
        && Objects.nonNull(onInitRequest.getConsentRequest())) {
      requestLogService.updateConsentResponse(
          onInitRequest.getResp().getRequestId(), onInitRequest.getConsentRequest().getId());
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
    }
  }

  @Override
  public void consentOnStatus(HIUConsentOnStatusRequest hiuConsentOnStatusRequest)
      throws IllegalDataStateException {
    if (Objects.nonNull(hiuConsentOnStatusRequest)
        && Objects.nonNull(hiuConsentOnStatusRequest.getResp())
        && Objects.nonNull(hiuConsentOnStatusRequest.getConsentRequest())) {
      requestLogService.updateConsentResponse(
          hiuConsentOnStatusRequest.getResp().getRequestId(),
          hiuConsentOnStatusRequest.getConsentRequest());
    } else if (Objects.nonNull(hiuConsentOnStatusRequest)
        && Objects.nonNull(hiuConsentOnStatusRequest.getResp())
        && Objects.nonNull(hiuConsentOnStatusRequest.getError())) {
      requestLogService.updateError(
          hiuConsentOnStatusRequest.getResp().getRequestId(),
          hiuConsentOnStatusRequest.getError().getMessage(),
          RequestStatus.CONSENT_ON_STATUS_ERROR);
    } else if (Objects.nonNull(hiuConsentOnStatusRequest)
        && Objects.nonNull(hiuConsentOnStatusRequest.getResp())) {
      requestLogService.updateError(
          hiuConsentOnStatusRequest.getResp().getRequestId(),
          "Something went wrong while executing consent on status",
          RequestStatus.CONSENT_ON_STATUS_ERROR);
    }
  }

  @Override
  public void hiuNotify(NotifyHIURequest notifyHIURequest) throws IllegalDataStateException {
    if (Objects.nonNull(notifyHIURequest) && Objects.nonNull(notifyHIURequest.getNotification())) {
      requestLogService.updateConsentResponse(
          notifyHIURequest.getRequestId(), notifyHIURequest.getNotification());

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
    } else if (Objects.nonNull(notifyHIURequest)) {
      requestLogService.updateError(
          notifyHIURequest.getRequestId(),
          "Something went wrong while executing consent on status",
          RequestStatus.CONSENT_HIU_NOTIFY_ERROR);
    }
  }

  @Override
  @Transactional
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
