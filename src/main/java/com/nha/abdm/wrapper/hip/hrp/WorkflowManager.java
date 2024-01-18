/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nha.abdm.wrapper.common.models.AckRequest;
import com.nha.abdm.wrapper.common.models.VerifyOtp;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.PatientService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.discover.DiscoveryInterface;
import com.nha.abdm.wrapper.hip.hrp.discover.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.HipLinkInterface;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hip.hrp.link.LinkInterface;
import com.nha.abdm.wrapper.hip.hrp.link.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.responses.InitResponse;
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

  public void initiateOnDiscover(DiscoverResponse data)
      throws URISyntaxException, JsonProcessingException {
    if (data != null) {
      discoveryInterface.onDiscover(data);
    } else {
      log.error("Error in Discover response from gateWay");
    }
  }

  public void initiateOnInit(InitResponse data) throws URISyntaxException, JsonProcessingException {
    if (data != null) {
      linkInterface.onInit(data);
    } else {
      log.error("Error in Init response from gateWay");
    }
  }

  public void startOnConfirmCall(ConfirmResponse data)
      throws URISyntaxException, JsonProcessingException {
    if (data != null) {
      linkInterface.onConfirm(data);
    } else {
      log.error("Error in Confirm response from gateWay");
    }
  }

  public AckRequest getCareContextRequestStatus(String requestId) {
    return AckRequest.builder().message(requestLogService.getStatus(requestId)).build();
  }

  // -------------------------HIP Linking-------------------//
  public AckRequest startHipAuthInit(LinkRecordsResponse data) {
    if (data != null) {
      return hipLinkInterface.hipAuthInit(data);
    } else {
      log.error("OnConfirm -> error due to response");
    }
    return null;
  }

  public void startHipConfirmCall(LinkOnInitResponse data) throws TimeoutException {
    if (data != null && data.getError() == null) {
      if (data.getAuth().getMode().equals("DEMOGRAPHICS")) hipLinkInterface.hipConfirmCall(data);
      else if (data.getAuth().getMode().equals("MOBILE_OTP"))
        requestLogService.setHipOnInitResponseOTP(data);
    } else if (data.getError() != null) {
      log.error("OnInit error" + data.getError().getMessage());
    } else {
      log.error("Oninit -> error due to response");
    }
  }

  public void startHipAddCareContext(LinkOnConfirmResponse data) {
    if (data != null && data.getError() == null) {
      hipLinkInterface.hipAddCareContext(data);
    } else if (data.getError() != null) {
      log.error("OnConfirm error" + data.getError().getMessage());
    } else {
      log.error("OnConfirm -> error due to response");
    }
  }

  public AckRequest storePatientInWrapper(Patient data) {
    return patientService.addPatientInWrapper(data);
  }

  public void startHipConfirmCallOTP(VerifyOtp data) {
    if (data != null) {
      hipLinkInterface.hipConfirmCallOtp(data);
    } else log.error("Error in response of VerifyOtp");
  }
}
