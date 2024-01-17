/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.common.ErrorResponse;
import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.models.CareContext;
import com.nha.abdm.wrapper.hip.hrp.common.requests.CareContextRequest;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.link.requests.*;
import com.nha.abdm.wrapper.hip.hrp.link.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.responses.InitResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LinkService implements LinkInterface {

  @Autowired PatientRepo patientRepo;
  @Autowired RequestManager requestManager;
  @Autowired RequestLogService requestLogService;
  @Autowired PatientService patientService;

  @Value("${onInitLinkPath}")
  public String onInitLinkPath;

  @Value("${onConfirmLinkPath}")
  public String onConfirmLinkPath;

  private static final Logger log = LogManager.getLogger(LinkService.class);

  @Override
  public void onInit(InitResponse initResponse) {
    boolean isCareContextPresent = patientService.checkCareContexts(initResponse);
    OnInitRequest onInitRequest = null;
    String linkReferenceNumber = UUID.randomUUID().toString();
    String requestId = UUID.randomUUID().toString();
    if (isCareContextPresent) {
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
              .requestId(UUID.randomUUID().toString())
              .timestamp(Utils.getCurrentTimeStamp().toString())
              .transactionId(initResponse.getTransactionId())
              .link(onInitLink)
              .build();
    } else {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("HIP -> Mismatch of careContext");
      errorResponse.setCode(1000);
      log.error("OnInit body -> making error body since careContexts are not matched");
      onInitRequest =
          OnInitRequest.builder()
              .requestId(requestId)
              .timestamp(Utils.getCurrentTimeStamp().toString())
              .transactionId(initResponse.getTransactionId())
              .error(errorResponse)
              .build();
    }
    try {
      ResponseEntity<ObjectNode> responseEntity =
          requestManager.fetchResponseFromPostRequest(onInitLinkPath, onInitRequest);
      log.info(onInitLinkPath + " : onInitCall: " + responseEntity.getStatusCode());
    } catch (Exception e) {
      log.info(onInitLinkPath + " : OnInitCall -> Error : " + Arrays.toString(e.getStackTrace()));
    }
    try {
      requestLogService.setLinkResponse(initResponse, requestId, linkReferenceNumber);
    } catch (Exception e) {
      log.info(
          "onInitCall -> Error: unable to set content : " + Arrays.toString(e.getStackTrace()));
    }
  }

  @Override
  public void onConfirm(ConfirmResponse confirmResponse) {
    List<CareContext> careContexts = null;
    String display = null;
    String linkRefNumber = confirmResponse.getConfirmation().getLinkRefNumber();

    String abhaAddress = requestLogService.getPatientId(linkRefNumber);
    String patientReference = requestLogService.getPatientReference(linkRefNumber);
    Patient patientWithAbha = patientRepo.findByAbhaAddress(abhaAddress);
    Patient patientWithPatientRef = patientRepo.findByPatientReference(patientReference);

    if (patientWithAbha != null) {
      careContexts = patientWithAbha.getCareContexts();
      display = patientWithAbha.getDisplay();
    } else if (patientWithPatientRef != null) {
      careContexts = patientWithPatientRef.getCareContexts();
      display = patientWithPatientRef.getDisplay();
    }
    log.info("onConfirm Abha address is: " + abhaAddress);
    if (abhaAddress == null) {
      log.info("OnConfirmCall -> patient with abhaAddress not found in logs.");
    }
    List<CareContext> selectedCareContexts =
        requestLogService.getSelectedCareContexts(linkRefNumber, careContexts);

    OnConfirmPatient onConfirmPatient = null;
    String tokenNumber = confirmResponse.getConfirmation().getToken();
    if (tokenNumber.equals("123456")) {
      List<CareContextRequest> careContextsList = new ArrayList<>();
      if (careContexts != null && !careContexts.isEmpty()) {
        for (CareContext careContext : careContexts) {
          careContextsList.add(
              CareContextRequest.builder()
                  .referenceNumber(careContext.getReferenceNumber())
                  .display(careContext.getDisplay())
                  .build());
        }
      }

      onConfirmPatient =
          OnConfirmPatient.builder()
              .referenceNumber(patientReference)
              .display(display)
              .onConfirmCareContexts(careContextsList)
              .build();
    }

    OnConfirmRequest onConfirmRequest =
        OnConfirmRequest.builder()
            .requestId(UUID.randomUUID().toString())
            .timestamp(Utils.getCurrentTimeStamp().toString())
            .onConfirmPatient(onConfirmPatient)
            .build();
    try {
      ResponseEntity responseEntity =
          requestManager.fetchResponseFromPostRequest(onConfirmLinkPath, onConfirmRequest);
      log.info(onConfirmLinkPath + " : onConfirmCall: " + responseEntity.getStatusCode());
      patientService.updateCareContextStatus(patientReference, selectedCareContexts);
    } catch (Exception e) {
      log.error(
          onConfirmLinkPath + " : OnConfirmCall -> Error :" + Arrays.toString(e.getStackTrace()));
    }
  }
}
