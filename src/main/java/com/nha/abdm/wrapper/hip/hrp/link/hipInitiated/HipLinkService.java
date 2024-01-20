/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.hipInitiated;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.common.ErrorResponse;
import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.models.FacadeResponse;
import com.nha.abdm.wrapper.common.models.VerifyOTP;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.RequestLog;
import com.nha.abdm.wrapper.hip.hrp.discover.requests.OnDiscoverPatient;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkAddCareContext;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkAuthInit;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkConfirm;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.helpers.*;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkRecordsResponse;
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

  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>1)Build the required body for /auth/init including abhaAddress.<br>
   * 2)Stores the request of linkRecordsResponse into requestLog.<br>
   * 3)makes a POST request to /auth/init API
   *
   * @param linkRecordsResponse Response which has authMode, patient details and careContexts.
   * @return it returns the requestId and status of initiation to the Facility for future tracking
   */
  public FacadeResponse hipAuthInit(LinkRecordsResponse linkRecordsResponse) {
    try {
      //      patientService.addPatient(linkRecordsResponse);
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
        return FacadeResponse.builder()
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
    return FacadeResponse.builder().error(errorResponse).build();
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
  public void hipConfirmCall(LinkOnInitResponse linkOnInitResponse) throws TimeoutException {
    RequestLog existingRecord =
        logsRepo.findByGatewayRequestId(linkOnInitResponse.getResp().getRequestId());
    if (existingRecord != null) {
      log.info("In confirmAuth found existing record");
      LinkRecordsResponse linkRecordsResponse =
          (LinkRecordsResponse) existingRecord.getRawResponse().get("LinkRecordsResponse");
      Patient patient = patientRepo.findByAbhaAddress(linkRecordsResponse.getAbhaAddress());
      if (patient == null) log.error("HipConfirmCall -> patient not found");
      else {
        UserDemographic userDemographic =
            UserDemographic.builder()
                .name(patient.getName())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .build();
        LinkCredential linkCredential =
            LinkCredential.builder().demographic(userDemographic).build();
        LinkConfirm linkConfirm =
            LinkConfirm.builder()
                .requestId(UUID.randomUUID().toString())
                .timestamp(Utils.getCurrentTimeStamp())
                .transactionId(linkOnInitResponse.getAuth().getTransactionId())
                .credential(linkCredential)
                .build();
        log.info("hipConfirmCall" + linkConfirm.toString());
        requestLogService.setHipOnInitResponse(linkOnInitResponse, linkConfirm);
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
  public void hipConfirmCallOtp(VerifyOTP verifyOTP) {
    RequestLog existingRecord = logsRepo.findByClientRequestId(verifyOTP.getRequestId());
    if (existingRecord != null) {
      log.info("In confirmAuth found existing record");

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
      requestLogService.updateOnInitResponseOTP(
          verifyOTP.getRequestId(), linkConfirm.getRequestId());
      try {
        responseEntity =
            requestManager.fetchResponseFromPostRequest(linkConfirmAuthPath, linkConfirm);
        log.info(linkConfirmAuthPath + " : linkConfirmAuth: " + responseEntity.getStatusCode());
      } catch (Exception e) {
        log.info(linkConfirmAuthPath + " : linkConfirmAuth -> Error : " + Exceptions.unwrap(e));
      }
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
    LinkRecordsResponse linkRecordsResponse =
        (LinkRecordsResponse) existingRecord.getRawResponse().get("LinkRecordsResponse");
    Patient patient = patientRepo.findByAbhaAddress(linkRecordsResponse.getAbhaAddress());

    OnDiscoverPatient patientNode =
        OnDiscoverPatient.builder()
            .referenceNumber(patient.getPatientReference())
            .display(patient.getDisplay())
            .careContexts(linkRecordsResponse.getPatient().getCareContexts())
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
    log.info("Link AddCareContext : " + linkAddCareContext.toString());
    requestLogService.setHipOnConfirmResponse(linkOnConfirmResponse, linkAddCareContext);
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
