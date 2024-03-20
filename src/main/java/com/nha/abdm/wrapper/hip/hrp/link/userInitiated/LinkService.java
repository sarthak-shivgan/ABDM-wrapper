/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.userInitiated;

import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.models.CareContext;
import com.nha.abdm.wrapper.common.models.RespRequest;
import com.nha.abdm.wrapper.common.responses.GenericResponse;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.requests.*;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.InitResponse;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;

@Service
public class LinkService implements LinkInterface {

  @Autowired PatientRepo patientRepo;
  private final RequestManager requestManager;
  @Autowired RequestLogService requestLogService;
  @Autowired PatientService patientService;

  @Value("${onInitLinkPath}")
  public String onInitLinkPath;

  @Value("${onConfirmLinkPath}")
  public String onConfirmLinkPath;

  @Autowired
  public LinkService(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  private static final Logger log = LogManager.getLogger(LinkService.class);

  /**
   * <B>userInitiatedLinking</B>
   *
   * <p>The Response has list of careContext.<br>
   * 1) HIP needs to send the OTP to respective user.<br>
   * 2) build the onInitRequest body with OTP expiry.<br>
   * 3) POST Method to "/link/on-init"
   *
   * @param initResponse Response from ABDM gateway for linking of careContexts.
   */
  @Override
  public void onInit(InitResponse initResponse) {
    OnInitRequest onInitRequest;
    String linkReferenceNumber = UUID.randomUUID().toString();
    String requestId = UUID.randomUUID().toString();

    OnInitLinkMeta onInitLinkMeta =
        OnInitLinkMeta.builder()
            .communicationMedium("MOBILE")
            .communicationHint("string")
            .communicationExpiry(Utils.getSmsExpiry())
            .build();

    OnInitLink onInitLink =
        OnInitLink.builder()
            .referenceNumber(linkReferenceNumber)
            .authenticationType("DIRECT")
            .meta(onInitLinkMeta)
            .build();

    onInitRequest =
        OnInitRequest.builder()
            .requestId(requestId)
            .timestamp(Utils.getCurrentTimeStamp())
            .transactionId(initResponse.getTransactionId())
            .link(onInitLink)
            .resp(RespRequest.builder().requestId(initResponse.getRequestId()).build())
            .build();

    log.info("onInit body : " + onInitRequest.toString());
    try {
      ResponseEntity<GenericResponse> responseEntity =
          requestManager.fetchResponseFromGateway(onInitLinkPath, onInitRequest);
      log.info(onInitLinkPath + " : onInitCall: " + responseEntity.getStatusCode());
    } catch (Exception e) {
      log.info(onInitLinkPath + " : OnInitCall -> Error : " + Arrays.toString(e.getStackTrace()));
    }
    try {
      requestLogService.setLinkResponse(initResponse, requestId, linkReferenceNumber);
    } catch (Exception e) {
      log.info("onInitCall -> Error: unable to set content : " + Exceptions.unwrap(e));
    }
  }

  /**
   * <B>userInitiatedLinking</B>
   *
   * <p>The confirmResponse has the OTP entered by the user for authentication.<br>
   * 1) Validate the OTP and send the response of careContexts or error. <br>
   * 2) build the request body of OnConfirmRequest.<br>
   * 3) POST method to link/on-confirm
   *
   * @param confirmResponse Response from ABDM gateway with OTP entered by user.
   */
  @Override
  public void onConfirm(ConfirmResponse confirmResponse) {
    String display = null;
    String linkRefNumber = confirmResponse.getConfirmation().getLinkRefNumber();

    String abhaAddress = requestLogService.getPatientId(linkRefNumber);
    String patientReference = requestLogService.getPatientReference(linkRefNumber);
    Patient patientWithAbha = patientRepo.findByAbhaAddress(abhaAddress);
    Patient patientWithPatientRef = patientRepo.findByPatientReference(patientReference);

    if (patientWithAbha != null) {
      display = patientWithAbha.getPatientDisplay();
    } else if (patientWithPatientRef != null) {
      display = patientWithPatientRef.getPatientDisplay();
    }
    log.info("onConfirm Abha address is: " + abhaAddress);
    if (abhaAddress == null) {
      log.info("OnConfirmCall -> patient with abhaAddress not found in logs.");
    }
    List<CareContext> careContexts = requestLogService.getSelectedCareContexts(linkRefNumber);

    OnConfirmPatient onConfirmPatient = null;
    String tokenNumber = confirmResponse.getConfirmation().getToken();
    if (tokenNumber.equals("123456")) {
      onConfirmPatient =
          OnConfirmPatient.builder()
              .referenceNumber(patientReference)
              .display(display)
              .careContexts(careContexts)
              .build();
    }

    OnConfirmRequest onConfirmRequest =
        OnConfirmRequest.builder()
            .requestId(UUID.randomUUID().toString())
            .timestamp(Utils.getCurrentTimeStamp())
            .patient(onConfirmPatient)
            .resp(RespRequest.builder().requestId(confirmResponse.getRequestId()).build())
            .build();
    log.info("onConfirm : " + onConfirmRequest.toString());
    try {
      ResponseEntity responseEntity =
          requestManager.fetchResponseFromGateway(onConfirmLinkPath, onConfirmRequest);
      log.info(onConfirmLinkPath + " : onConfirmCall: " + responseEntity.getStatusCode());
      patientService.updateCareContextStatus(abhaAddress, careContexts);
    } catch (Exception e) {
      log.error(onConfirmLinkPath + " : OnConfirmCall -> Error :" + Exceptions.unwrap(e));
    }
  }
}
