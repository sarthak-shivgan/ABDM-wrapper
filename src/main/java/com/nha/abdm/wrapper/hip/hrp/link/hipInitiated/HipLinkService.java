/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.hipInitiated;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.common.ErrorResponse;
import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.models.FacadeResponse;
import com.nha.abdm.wrapper.common.models.VerifyOTP;
import com.nha.abdm.wrapper.hip.HIPClient;
import com.nha.abdm.wrapper.hip.HIPPatient;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.RequestLog;
import com.nha.abdm.wrapper.hip.hrp.discover.requests.OnDiscoverPatient;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkAddCareContext;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkAuthInit;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkConfirm;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkRecordsRequest;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.helpers.*;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnInitResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
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
  @Autowired RequestManager requestManager;
  @Autowired RequestLogService requestLogService;
  @Autowired HIPClient hipClient;
  private final String requesterType = "HIP";
  private final String linkPurpose = "KYC_AND_LINK";

  @Value("${linkAuthInitPath}")
  public String linkAuthInitPath;

  @Value("${linkConfirmAuthPath}")
  public String linkConfirmAuthPath;

  @Value("${linkAddContextsPath}")
  public String linkAddContextsPath;

  ResponseEntity<ObjectNode> responseEntity;
  private static final Logger log = LogManager.getLogger(HipLinkService.class);

  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>1)Build the required body for /auth/init including abhaAddress.<br>
   * 2)Stores the request of linkRecordsResponse into requestLog.<br>
   * 3)makes a POST request to /auth/init API
   *
   * @param linkRecordsRequest Response which has authMode, patient details and careContexts.
   * @return it returns the requestId and status of initiation to the Facility for future tracking
   */
  public FacadeResponse hipAuthInit(LinkRecordsRequest linkRecordsRequest) {
    try {
      LinkRequester linkRequester =
          LinkRequester.builder()
              .id(linkRecordsRequest.getRequesterId())
              .type(requesterType)
              .build();

      LinkQuery linkQuery =
          LinkQuery.builder()
              .id(linkRecordsRequest.getAbhaAddress())
              .purpose(linkPurpose)
              .authMode(linkRecordsRequest.getAuthMode())
              .requester(linkRequester)
              .build();

      LinkAuthInit linkAuthInit =
          LinkAuthInit.builder()
              .requestId(linkRecordsRequest.getRequestId())
              .timestamp(Utils.getCurrentTimeStamp())
              .query(linkQuery)
              .build();

      log.debug("LinkAuthInit : " + linkAuthInit.toString());
      log.debug("LinkRecords storing data");
      requestLogService.setHipLinkResponse(linkRecordsRequest);
      try {
        responseEntity =
            requestManager.fetchResponseFromPostRequest(linkAuthInitPath, linkAuthInit);
        log.info(linkAuthInitPath + " : linkAuthInit: " + responseEntity.getStatusCode());
        return FacadeResponse.builder()
            .code(responseEntity.getStatusCode().value())
            .requestId(linkAuthInit.getRequestId())
            .build();
      } catch (Exception e) {
        log.error(linkAuthInitPath + " : linkAuthInit -> Error : " + Exceptions.unwrap(e));
        ErrorResponse errorResponse =
            ErrorResponse.builder()
                .message(
                    "Error while link auth init: "
                        + e.getMessage()
                        + " exception: "
                        + Exceptions.unwrap(e))
                .build();
        return FacadeResponse.builder().error(errorResponse).build();
      }

    } catch (Exception e) {
      log.error("Link authInit : " + Exceptions.unwrap(e));
      ErrorResponse errorResponse =
          ErrorResponse.builder()
              .code(1000)
              .message(
                  "Error while linking care contexts in auth init: "
                      + e.getMessage()
                      + " exception: "
                      + Exceptions.unwrap(e))
              .build();
      return FacadeResponse.builder().error(errorResponse).build();
    }
  }

  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>1) Build the required body for /auth/confirm.<br>
   * 2)transactionId from LinkOnInitResponse and the credentials of the authMethod i.e. DEMOGRAPHICS
   * details of patient to make request.<br>
   * 3)Stores the request of LinkOnInitResponse into requestLog.<br>
   * 4)Makes a POST request to "/v0.5/users/auth/confirm"
   *
   * @param linkOnInitResponse Response from ABDM gateway with transactionId after successful
   *     auth/init.
   */
  public void hipConfirmCall(LinkOnInitResponse linkOnInitResponse) {
    RequestLog existingRecord =
        logsRepo.findByGatewayRequestId(linkOnInitResponse.getResp().getRequestId());
    if (existingRecord == null) {
      log.error("hipConfirmCall: Illegal State - Request Id not found in database.");
      return;
    }
    log.debug("In confirmAuth found existing record");
    LinkRecordsRequest linkRecordsRequest =
        (LinkRecordsRequest) existingRecord.getRawResponse().get("LinkRecordsResponse");
    Patient patient =
        Optional.ofNullable(patientRepo.findByAbhaAddress(linkRecordsRequest.getAbhaAddress()))
            .orElseGet(() -> getPatient(linkRecordsRequest.getAbhaAddress()));
    UserDemographic userDemographic =
        UserDemographic.builder()
            .name(patient.getName())
            .gender(patient.getGender())
            .dateOfBirth(patient.getDateOfBirth())
            .build();
    LinkCredential linkCredential = LinkCredential.builder().demographic(userDemographic).build();
    LinkConfirm linkConfirm =
        LinkConfirm.builder()
            .requestId(UUID.randomUUID().toString())
            .timestamp(Utils.getCurrentTimeStamp())
            .transactionId(linkOnInitResponse.getAuth().getTransactionId())
            .credential(linkCredential)
            .build();
    log.debug("hipConfirmCall" + linkConfirm.toString());
    requestLogService.setHipOnInitResponse(linkOnInitResponse, linkConfirm);
    try {
      responseEntity =
          requestManager.fetchResponseFromPostRequest(linkConfirmAuthPath, linkConfirm);
      log.info(linkConfirmAuthPath + " : linkConfirmAuth: " + responseEntity.getStatusCode());
    } catch (Exception e) {
      log.info(linkConfirmAuthPath + " : linkConfirmAuth -> Error : " + Exceptions.unwrap(e));
    }
  }

  private Patient getPatient(String abhaAddress) {
    log.debug("Patient not found in database, sending request to HIP.");
    HIPPatient hipPatient = hipClient.getPatient(abhaAddress);
    Patient patient = new Patient();
    patient.setAbhaAddress(hipPatient.getAbhaAddress());
    patient.setGender(hipPatient.getGender());
    patient.setName(hipPatient.getName());
    patient.setDateOfBirth(hipPatient.getDateOfBirth());
    patient.setDisplay(hipPatient.getPatientDisplay());
    patient.setPatientReference(hipPatient.getPatientReference());
    patient.setPatientMobile(hipPatient.getPatientMobile());

    // Save the patient into the database.
    patientRepo.save(patient);

    return patient;
  }

  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>1)Build the required body for /auth/on-confirm.<br>
   * 2)If the authMode is "MOBILE_OTP", using the request of verifyOTP which has OTP.<br>
   * 3)And fetch the rawResponse dump of "HIPOnConfirm" (on-confirm request API) from db.<br>
   * 4)Updating the gatewayRequestId in db via "updateOnInitResponseOTP"<br>
   * 5)Makes a POST request to /v0.5/users/auth/confirm.
   *
   * @param verifyOTP Response to facade with OTP for authentication.
   */
  public FacadeResponse hipConfirmCallOtp(VerifyOTP verifyOTP) {
    RequestLog existingRecord = logsRepo.findByClientRequestId(verifyOTP.getRequestId());
    if (existingRecord == null) {
      return FacadeResponse.builder()
          .error(
              ErrorResponse.builder()
                  .message("Illegal State: Request Not found in database.")
                  .build())
          .build();
    }
    log.debug("In confirmAuth found existing record");

    LinkCredential linkCredential =
        LinkCredential.builder().authCode(verifyOTP.getAuthCode()).build();

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
    requestLogService.updateOnInitResponseOTP(verifyOTP.getRequestId(), linkConfirm.getRequestId());
    try {
      responseEntity =
          requestManager.fetchResponseFromPostRequest(linkConfirmAuthPath, linkConfirm);
      log.info(linkConfirmAuthPath + " : linkConfirmAuth: " + responseEntity.getStatusCode());
      return FacadeResponse.builder()
          .message(linkConfirmAuthPath + " : linkConfirmAuth: " + responseEntity.getStatusCode())
          .build();
    } catch (Exception e) {
      String error =
          linkConfirmAuthPath
              + ": Error while executing link Confirm Auth: "
              + e.getMessage()
              + " exception: "
              + Exceptions.unwrap(e);
      log.error(error);
      return FacadeResponse.builder().error(ErrorResponse.builder().message(error).build()).build();
    }
  }

  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>1)linkOnConfirmResponse has the linkToken for linking the careContext.<br>
   * 2)Using the gatewayRequestId fetching the careContext which is present in linkRecordsResponse.
   * <br>
   * 3)Build the body of linkAddCareContext using the linkToken and careContexts.<br>
   * 4)Makes a POST request to "/v0.5/links/link/add-contexts".
   *
   * @param linkOnConfirmResponse Response from ABDM gateway with linkToken for linking careContext.
   */
  public void hipAddCareContext(LinkOnConfirmResponse linkOnConfirmResponse) {
    RequestLog existingRecord =
        logsRepo.findByGatewayRequestId(linkOnConfirmResponse.getResp().getRequestId());
    if (existingRecord == null) {
      log.error("hipAddCareContext: Illegal state - Gateway request Id not found in database");
      return;
    }
    LinkRecordsRequest linkRecordsRequest =
        (LinkRecordsRequest) existingRecord.getRawResponse().get("LinkRecordsResponse");
    Patient patient = patientRepo.findByAbhaAddress(linkRecordsRequest.getAbhaAddress());
    if (patient == null) {
      log.error("hipAddCareContext: Illegal state - Patient not found in database");
      return;
    }

    OnDiscoverPatient patientNode =
        OnDiscoverPatient.builder()
            .referenceNumber(patient.getPatientReference())
            .display(patient.getDisplay())
            .careContexts(linkRecordsRequest.getPatient().getCareContexts())
            .build();
    LinkTokenAndPatient linkNode =
        LinkTokenAndPatient.builder()
            .accessToken(linkOnConfirmResponse.getAuth().getAccessToken())
            .patient(patientNode)
            .build();
    LinkAddCareContext linkAddCareContext =
        LinkAddCareContext.builder()
            .requestId(UUID.randomUUID().toString())
            .timestamp(Utils.getCurrentTimeStamp())
            .link(linkNode)
            .build();
    log.debug("Link AddCareContext : " + linkAddCareContext.toString());
    requestLogService.setHipOnConfirmResponse(linkOnConfirmResponse, linkAddCareContext);
    try {
      responseEntity =
          requestManager.fetchResponseFromPostRequest(linkAddContextsPath, linkAddCareContext);
      log.debug(linkAddContextsPath + " : linkAddContexts: " + responseEntity.getStatusCode());

    } catch (Exception e) {
      log.error(
          linkAddContextsPath
              + " : linkAddContexts -> Error : "
              + Arrays.toString(e.getStackTrace()));
    }
  }
}
