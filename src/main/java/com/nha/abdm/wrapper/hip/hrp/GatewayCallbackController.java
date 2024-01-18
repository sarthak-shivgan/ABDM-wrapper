/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.discover.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnAddCareContextsResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.link.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.responses.InitResponse;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class GatewayCallbackController {

  @Autowired WorkflowManager workflowManager;
  @Autowired RequestLogService requestLogService;

  private static final Logger log = LogManager.getLogger(GatewayCallbackController.class);

  @PostMapping("/v0.5/care-contexts/discover")
  public void discoverCall(@RequestBody DiscoverResponse data)
      throws URISyntaxException, JsonProcessingException {
    if (data != null && data.getError() == null) {
      log.info("/v0.5/care-contexts/discover :" + data.toString());
      workflowManager.initiateOnDiscover(data);
    } else {
      log.error("/v0.5/care-contexts/discover :" + data.getError().getMessage());
    }
  }

  @PostMapping("/v0.5/links/link/init")
  public void initCall(@RequestBody InitResponse data)
      throws URISyntaxException, JsonProcessingException {
    if (data != null && data.getError() == null) {
      log.info("/v0.5/links/link/init :" + data.toString());
      workflowManager.initiateOnInit(data);
    } else {
      log.error("/v0.5/links/link/init :" + data.getError().getMessage());
    }
  }

  @PostMapping("/v0.5/links/link/confirm")
  public void confirmCall(@RequestBody ConfirmResponse data)
      throws URISyntaxException, JsonProcessingException {
    if (data != null && data.getError() == null) {
      log.info("/v0.5/links/link/confirm : " + data.toString());
      workflowManager.startOnConfirmCall(data);
    } else {
      log.error("/v0.5/links/link/confirm : " + data.getError().getMessage());
    }
  }

  // --------------------HIP Linking--------------------//
  @PostMapping({"/v0.5/users/auth/on-init"})
  public void onAuthInitCall(@RequestBody LinkOnInitResponse data)
      throws URISyntaxException, JsonProcessingException, TimeoutException {
    if (data != null && data.getError() == null) {
      log.info(data.toString());
      this.workflowManager.startHipConfirmCall(data);
    } else if (data.getError() != null) {
      log.info("got Error in OnInitRequest callback: " + data.getError().toString());
    } else {
      log.error("got Error in OnInitRequest callback");
    }
  }

  @PostMapping({"/v0.5/users/auth/on-confirm"})
  public void onAuthConfirmCall(@RequestBody LinkOnConfirmResponse data)
      throws URISyntaxException, JsonProcessingException, TimeoutException {
    if (data != null && data.getError() == null) {
      log.info(data.toString());
      this.workflowManager.startHipAddCareContext(data);
    } else if (data.getError() != null) {
      log.info("gotError in OnInitRequest callback: " + data.getError().toString());
    } else {
      log.error("gotError in OnInitRequest callback");
    }
  }

  @PostMapping({"/v0.5/links/link/on-add-contexts"})
  public void onAddCareContext(@RequestBody LinkOnAddCareContextsResponse data) {
    log.info(data.toString());
    if (data != null) {
      if (data.getError() != null)
        log.error("/v0.5/links/link/on-add-contexts error: " + data.getError().getMessage());
      requestLogService.setHipOnAddCareContextResponse(data);
    } else {
      log.info("Failed to add Context");
    }
  }
}
