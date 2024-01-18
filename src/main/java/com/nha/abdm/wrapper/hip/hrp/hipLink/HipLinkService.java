/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.hipLink;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.common.ErrorResponse;
import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.models.AckRequest;
import com.nha.abdm.wrapper.common.models.VerifyOtp;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.RequestLog;
import com.nha.abdm.wrapper.hip.hrp.discover.requests.OnDiscoverPatient;
import com.nha.abdm.wrapper.hip.hrp.hipLink.requests.LinkAddCareContext;
import com.nha.abdm.wrapper.hip.hrp.hipLink.requests.LinkAuthInit;
import com.nha.abdm.wrapper.hip.hrp.hipLink.requests.LinkConfirm;
import com.nha.abdm.wrapper.hip.hrp.hipLink.requests.helpers.*;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkRecordsResponse;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;

@Service
public class HipLinkService implements HipLinkInterface {
  @Autowired PatientRepo patientRepo;
  @Autowired LogsRepo logsRepo;
  @Autowired PatientService patientService;
  @Autowired RequestManager requestManager;
  @Autowired RequestLogService requestLogService;
  private final String requesterType = "HIP";
  private final String linkPurpose = "KYC_AND_LINK";

  @Value("${linkAuthInitPath}")
  public String linkAuthInitPath;

  @Value("${linkConfirmAuthPath}")
  public String linkConfirmAuthPath;

  @Value("${linkAddContextsPath}")
  public String linkAddContextsPath;

  ResponseEntity<ObjectNode> responseEntity;
  ErrorResponse errorResponse = new ErrorResponse();
  private static final Logger log = LogManager.getLogger(HipLinkService.class);

  public AckRequest hipAuthInit(LinkRecordsResponse linkRecordsResponse) {
    try {
      patientService.addPatient(linkRecordsResponse);
      LinkRequester linkRequester =
          LinkRequester.builder()
              .id(linkRecordsResponse.getRequesterId())
              .type(requesterType)
              .build();

      LinkQuery linkQuery =
          LinkQuery.builder()
              .id(linkRecordsResponse.getAbhaAddress())
              .purpose(linkPurpose)
              .authMode(linkRecordsResponse.getAuthMode())
              .requester(linkRequester)
              .build();

      LinkAuthInit linkAuthInit =
          LinkAuthInit.builder()
              .requestId(linkRecordsResponse.getRequestId())
              .timestamp(Utils.getCurrentTimeStamp())
              .query(linkQuery)
              .build();

      log.info("LinkAuthInit : " + linkAuthInit.toString());
      log.info("LinkRecords storing data");
      requestLogService.setHipLinkResponse(linkRecordsResponse);
      try {
        responseEntity =
            requestManager.fetchResponseFromPostRequest(linkAuthInitPath, linkAuthInit);
        log.info(linkAuthInitPath + " : linkAuthInitPath: " + responseEntity.getStatusCode());
        return AckRequest.builder()
            .code(responseEntity.getStatusCode().value())
            .requestId(linkAuthInit.getRequestId())
            .build();
      } catch (Exception e) {
        log.info(linkAuthInitPath + " : linkAuthInit -> Error : " + Exceptions.unwrap(e));
      }

    } catch (Exception e) {
      log.info("Link authInit : " + e);
    }
    errorResponse.setCode(1000);
    errorResponse.setMessage("Error Linking careContexts");
    return AckRequest.builder().error(errorResponse).build();
  }

  public void hipConfirmCall(LinkOnInitResponse data) throws TimeoutException {
    RequestLog existingRecord = logsRepo.findByGatewayRequestId(data.getResp().getRequestId());
    if (existingRecord != null) {
      log.info("In confirmAuth found existing record");
      LinkRecordsResponse linkRecordsResponse =
          (LinkRecordsResponse) existingRecord.getRawResponse().get("LinkRecordsResponse");
      Patient patient = patientRepo.findByAbhaAddress(linkRecordsResponse.getAbhaAddress());
      if (patient == null) throw new RuntimeException("Patient not found");
      else {
        LinkDemographic linkDemographic =
            LinkDemographic.builder()
                .name(patient.getName())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .build();
        LinkCredential linkCredential =
            LinkCredential.builder().demographic(linkDemographic).build();
        LinkConfirm linkConfirm =
            LinkConfirm.builder()
                .requestId(UUID.randomUUID().toString())
                .timestamp(Utils.getCurrentTimeStamp())
                .transactionId(data.getAuth().getTransactionId())
                .credential(linkCredential)
                .build();
        log.info("hipConfirmCall" + linkConfirm.toString());
        requestLogService.setHipOnInitResponse(data, linkConfirm);
        try {
          responseEntity =
              requestManager.fetchResponseFromPostRequest(linkConfirmAuthPath, linkConfirm);
          log.info(linkConfirmAuthPath + " : linkConfirmAuth: " + responseEntity.getStatusCode());
        } catch (Exception e) {
          log.info(linkConfirmAuthPath + " : linkConfirmAuth -> Error : " + Exceptions.unwrap(e));
        }
      }
    }
  }

  public void hipConfirmCallOtp(VerifyOtp data) {
    RequestLog existingRecord = logsRepo.findByClientRequestId(data.getRequestId());
    if (existingRecord != null) {
      log.info("In confirmAuth found existing record");

      LinkCredential linkCredential = LinkCredential.builder().authCode(data.getAuthCode()).build();

      LinkOnInitResponse linkOnInitResponse =
          (LinkOnInitResponse) existingRecord.getRawResponse().get("HIPOnInitOtp");
      LinkConfirm linkConfirm =
          LinkConfirm.builder()
              .requestId(UUID.randomUUID().toString())
              .timestamp(Utils.getCurrentTimeStamp())
              .transactionId(linkOnInitResponse.getAuth().getTransactionId())
              .credential(linkCredential)
              .build();
      log.info("hipConfirmCallOtp" + linkConfirm.toString());
      requestLogService.updateOnInitResponseOTP(data.getRequestId(), linkConfirm.getRequestId());
      try {
        responseEntity =
            requestManager.fetchResponseFromPostRequest(linkConfirmAuthPath, linkConfirm);
        log.info(linkConfirmAuthPath + " : linkConfirmAuth: " + responseEntity.getStatusCode());
      } catch (Exception e) {
        log.info(linkConfirmAuthPath + " : linkConfirmAuth -> Error : " + Exceptions.unwrap(e));
      }
    }
  }

  public void hipAddCareContext(LinkOnConfirmResponse data) {
    RequestLog existingRecord = logsRepo.findByGatewayRequestId(data.getResp().getRequestId());
    LinkRecordsResponse linkRecordsResponse =
        (LinkRecordsResponse) existingRecord.getRawResponse().get("LinkRecordsResponse");
    Patient patient = patientRepo.findByAbhaAddress(linkRecordsResponse.getAbhaAddress());

    OnDiscoverPatient patientNode =
        OnDiscoverPatient.builder()
            .referenceNumber(patient.getPatientReference())
            .display(patient.getDisplay())
            .careContexts(linkRecordsResponse.getPatient().getCareContexts())
            .build();
    LinkLinkNode linkNode =
        LinkLinkNode.builder()
            .accessToken(data.getAuth().getAccessToken())
            .patient(patientNode)
            .build();
    LinkAddCareContext linkAddCareContext =
        LinkAddCareContext.builder()
            .requestId(UUID.randomUUID().toString())
            .timestamp(Utils.getCurrentTimeStamp())
            .link(linkNode)
            .build();
    log.info("Link AddCareContext : " + linkAddCareContext.toString());
    requestLogService.setHipOnConfirmResponse(data, linkAddCareContext);
    try {
      responseEntity =
          requestManager.fetchResponseFromPostRequest(linkAddContextsPath, linkAddCareContext);
      log.info(linkAddContextsPath + " : linkAddContexts: " + responseEntity.getStatusCode());

    } catch (Exception e) {
      log.info(
          linkAddContextsPath
              + " : linkAddContexts -> Error : "
              + Arrays.toString(e.getStackTrace()));
    }
  }
}
