/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.discover.DiscoveryInterface;
import com.nha.abdm.wrapper.hip.hrp.discover.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hip.hrp.link.LinkInterface;
import com.nha.abdm.wrapper.hip.hrp.link.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.responses.InitResponse;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkflowManager {
  private static final Logger log = LogManager.getLogger(WorkflowManager.class);
  @Autowired DiscoveryInterface discoveryInterface;
  @Autowired LinkInterface linkInterface;
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

  public String getCareContextRequestStatus(String requestId) {
    return requestLogService.getStatus(requestId);
  }
}
