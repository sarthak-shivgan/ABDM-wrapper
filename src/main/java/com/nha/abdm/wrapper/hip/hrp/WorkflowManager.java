/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nha.abdm.wrapper.common.models.FacadeResponse;
import com.nha.abdm.wrapper.common.models.VerifyOTP;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.discover.DiscoveryInterface;
import com.nha.abdm.wrapper.hip.hrp.discover.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.HipLinkInterface;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.LinkInterface;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.InitResponse;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkflowManager {
  private static final Logger log = LogManager.getLogger(WorkflowManager.class);
  @Autowired DiscoveryInterface discoveryInterface;
  @Autowired PatientService patientService;
  @Autowired LinkInterface linkInterface;
  @Autowired HipLinkInterface hipLinkInterface;
  @Autowired RequestLogService requestLogService;

  /**
   * userInitiated linking
   *
   * <p>Routing the discover response to discovery interface for Making POST on-discover
   *
   * @param discoverResponse Response from ABDM gateway for patient discovery
   */
  public void initiateOnDiscover(DiscoverResponse discoverResponse)
      throws URISyntaxException, JsonProcessingException {
    if (discoverResponse != null) {
      discoveryInterface.onDiscover(discoverResponse);
    } else {
      log.error("Error in Discover response from gateWay");
    }
  }

  /**
   * userInitiated linking
   *
   * <p>Routing the initResponse to linkInterface for making POST on-init request.
   *
   * @param initResponse Response from ABDM gateway after successful on-Discover request.
   */
  public void initiateOnInit(InitResponse initResponse)
      throws URISyntaxException, JsonProcessingException {
    if (initResponse != null) {
      linkInterface.onInit(initResponse);
    } else {
      log.error("Error in Init response from gateWay");
    }
  }

  /**
   * userInitiated linking
   *
   * <p>Routing confirmResponse to linkInterface for making on on-confirm request.
   *
   * @param confirmResponse Response form ABDM gateway after successful on-init request.
   */
  public void initiateOnConfirmCall(ConfirmResponse confirmResponse)
      throws URISyntaxException, JsonProcessingException {
    if (confirmResponse != null) {
      linkInterface.onConfirm(confirmResponse);
    } else {
      log.error("Error in Confirm response from gateWay");
    }
  }

  /**
   * hipInitiatedLinking
   *
   * <p>Fetching the status from requestLogs using clientId.
   *
   * @param requestId clientRequestId for tracking the linking status.
   * @return "Success", "Initiated", "appropriate error message".
   */
  public FacadeResponse getCareContextRequestStatus(String requestId) {
    return FacadeResponse.builder().message(requestLogService.getStatus(requestId)).build();
  }

  /**
   * hipInitiatedLinking
   *
   * <p>Routing linkRecordsResponse to hipLinkInterface for making authInit body and POST auth/init.
   *
   * @param linkRecordsResponse Response received from facility to facade to link careContext
   * @return clientRequestId and statusCode.
   */
  public FacadeResponse initiateHipAuthInit(LinkRecordsResponse linkRecordsResponse) {
    if (linkRecordsResponse != null) {
      return hipLinkInterface.hipAuthInit(linkRecordsResponse);
    } else {
      log.error("OnConfirm -> error due to response");
    }
    return FacadeResponse.builder().message("Error in linkRecordsResponse").build();
  }

  /**
   * hipInitiatedLinking
   *
   * <p>if authMode == DEMOGRAPHICS start auth/confirm by routing via hipLinkInterface<br>
   * else if authMode == MOBILE_OTP, storing the response in requestLog <br>
   * when the otp is received via verifyOTP starting auth/confirm
   *
   * @param linkOnInitResponse Response from ABDM gateway after successful auth/init request.
   */
  public void initiateAuthConfirmDemographics(LinkOnInitResponse linkOnInitResponse)
      throws TimeoutException {
    if (linkOnInitResponse != null && linkOnInitResponse.getError() == null) {
      if (linkOnInitResponse.getAuth().getMode().equals("DEMOGRAPHICS"))
        hipLinkInterface.hipConfirmCall(linkOnInitResponse);
      else if (linkOnInitResponse.getAuth().getMode().equals("MOBILE_OTP"))
        requestLogService.setHipOnInitResponseOTP(linkOnInitResponse);
    } else if (linkOnInitResponse.getError() != null) {
      log.error("OnInit error" + linkOnInitResponse.getError().getMessage());
    } else {
      log.error("Oninit -> error due to response");
    }
  }

  /**
   * hipInitiatedLinking
   *
   * <p>Routing the linkOnConfirmResponse to hipLinkInterface for making addCareContext body, and
   * POST /v0.5/links/link/add-contexts
   *
   * @param linkOnConfirmResponse Response from ABDM gateway after successful auth/confirm.<br>
   */
  public void addCareContext(LinkOnConfirmResponse linkOnConfirmResponse) {
    if (linkOnConfirmResponse != null && linkOnConfirmResponse.getError() == null) {
      hipLinkInterface.hipAddCareContext(linkOnConfirmResponse);
    } else if (linkOnConfirmResponse.getError() != null) {
      log.error("OnConfirm error" + linkOnConfirmResponse.getError().getMessage());
    } else {
      log.error("OnConfirm -> error due to response");
    }
  }

  /**
   * Storing the patient in wrapper
   *
   * @param patient Demographic details of patient.
   * @return status of storing the patient data.
   */
  public FacadeResponse addPatient(Patient patient) {
    return patientService.addPatientInWrapper(patient);
  }

  /**
   * hipInitiatedLinking
   *
   * <p>In HipInitiatedLinking if authMode is MOBILE_OTP, then start auth/confirm request with OTP.
   *
   * @param verifyOTP request body which has OTP and clientRequestId.
   */
  public void initiateHipConfirmCallOTP(VerifyOTP verifyOTP) {
    if (verifyOTP != null) {
      hipLinkInterface.hipConfirmCallOtp(verifyOTP);
    } else log.error("Error in response of VerifyOtp");
  }
}
