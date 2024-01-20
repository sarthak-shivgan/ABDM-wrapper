/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.discover.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnAddCareContextsResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.InitResponse;
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

  /**
   * discovery
   *
   * <p>Routing to workFlowManager for using service interface.
   *
   * @param discoverResponse response body with demographic details and abhaAddress of patient.
   */
  @PostMapping("/v0.5/care-contexts/discover")
  public void discoverCall(@RequestBody DiscoverResponse discoverResponse)
      throws URISyntaxException, JsonProcessingException {
    if (discoverResponse != null && discoverResponse.getError() == null) {
      log.info("/v0.5/care-contexts/discover :" + discoverResponse.toString());
      workflowManager.initiateOnDiscover(discoverResponse);
    } else {
      log.error("/v0.5/care-contexts/discover :" + discoverResponse.getError().getMessage());
    }
  }

  /**
   * userInitiatedLinking
   *
   * <p>Routing to workFlowManager for using service interface.
   *
   * @param initResponse Response from ABDM gateway which has careContexts.
   */
  @PostMapping("/v0.5/links/link/init")
  public void initCall(@RequestBody InitResponse initResponse)
      throws URISyntaxException, JsonProcessingException {
    if (initResponse != null && initResponse.getError() == null) {
      log.info("/v0.5/links/link/init :" + initResponse.toString());
      workflowManager.initiateOnInit(initResponse);
    } else {
      log.error("/v0.5/links/link/init :" + initResponse.getError().getMessage());
    }
  }

  /**
   * userInitiatedLinking
   *
   * <p>Routing to workFlowManager for using service interface.
   *
   * @param confirmResponse Response from ABDM gateway which has OTP sent by facility to user for
   *     authentication.
   */
  @PostMapping("/v0.5/links/link/confirm")
  public void confirmCall(@RequestBody ConfirmResponse confirmResponse)
      throws URISyntaxException, JsonProcessingException {
    if (confirmResponse != null && confirmResponse.getError() == null) {
      log.info("/v0.5/links/link/confirm : " + confirmResponse.toString());
      workflowManager.initiateOnConfirmCall(confirmResponse);
    } else {
      log.error("/v0.5/links/link/confirm : " + confirmResponse.getError().getMessage());
    }
  }

  /**
   * hipInitiatedLinking
   *
   * <p>Routing to workFlowManager for using service interface.
   *
   * @param linkOnInitResponse Response from ABDM gateway after auth/init which has transactionId.
   */
  @PostMapping({"/v0.5/users/auth/on-init"})
  public void onAuthInitCall(@RequestBody LinkOnInitResponse linkOnInitResponse)
      throws URISyntaxException, JsonProcessingException, TimeoutException {
    if (linkOnInitResponse != null && linkOnInitResponse.getError() == null) {
      log.info(linkOnInitResponse.toString());
      this.workflowManager.initiateAuthConfirmDemographics(linkOnInitResponse);
    } else if (linkOnInitResponse.getError() != null) {
      log.info("got Error in OnInitRequest callback: " + linkOnInitResponse.getError().toString());
    } else {
      log.error("got Error in OnInitRequest callback");
    }
  }

  /**
   * hipInitiatedLinking
   *
   * <p>Routing to workFlowManager for using service interface
   *
   * @param linkOnConfirmResponse Response from ABDM gateway after successful auth/confirm which has
   *     linkToken to link careContext.
   */
  @PostMapping({"/v0.5/users/auth/on-confirm"})
  public void onAuthConfirmCall(@RequestBody LinkOnConfirmResponse linkOnConfirmResponse)
      throws URISyntaxException, JsonProcessingException, TimeoutException {
    if (linkOnConfirmResponse != null && linkOnConfirmResponse.getError() == null) {
      log.info(linkOnConfirmResponse.toString());
      this.workflowManager.addCareContext(linkOnConfirmResponse);
    } else if (linkOnConfirmResponse.getError() != null) {
      log.info(
          "gotError in OnInitRequest callback: " + linkOnConfirmResponse.getError().toString());
    } else {
      log.error("gotError in OnInitRequest callback");
    }
  }

  /**
   * hipInitiatedLinking
   *
   * <p>Gets the status of the linking of careContexts with abhadAddress.
   *
   * @param linkOnAddCareContextsResponse Response from ABDM gateway which has acknowledgement of
   *     linking.
   */
  @PostMapping({"/v0.5/links/link/on-add-contexts"})
  public void onAddCareContext(
      @RequestBody LinkOnAddCareContextsResponse linkOnAddCareContextsResponse) {
    log.info(linkOnAddCareContextsResponse.toString());
    if (linkOnAddCareContextsResponse != null) {
      if (linkOnAddCareContextsResponse.getError() != null)
        log.error(
            "/v0.5/links/link/on-add-contexts error: "
                + linkOnAddCareContextsResponse.getError().getMessage());
      requestLogService.setHipOnAddCareContextResponse(linkOnAddCareContextsResponse);
    } else {
      log.info("Failed to add Context");
    }
  }
}
