/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.requests.OnHealthInformationRequest;
import com.nha.abdm.wrapper.common.responses.GatewayCallbackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HIUHealthInformationGatewayCallbackController {

  @Autowired
  private HealthInformationGatewayCallbackInterface healthInformationGatewayCallbackInterface;

  @PostMapping({"/v0.5/health-information/cm/on-request"})
  public ResponseEntity<GatewayCallbackResponse> onHealthInformationRequest(
      @RequestBody OnHealthInformationRequest onHealthInformationRequest)
      throws IllegalDataStateException {
    HttpStatus httpStatus =
        healthInformationGatewayCallbackInterface.onHealthInformationRequest(
            onHealthInformationRequest);
    return new ResponseEntity<>(GatewayCallbackResponse.builder().build(), httpStatus);
  }
}
