/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.common.responses.GatewayCallbackResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HIUGatewayCallbackController {

  @Autowired private GatewayCallbackInterface gatewayCallback;

  @PostMapping({"/v0.5/consent-requests/on-init"})
  public ResponseEntity<GatewayCallbackResponse> onInitConsent(
      @RequestBody OnInitRequest onInitRequest) {
    gatewayCallback.onInitConsent(onInitRequest);
    return new ResponseEntity<>(GatewayCallbackResponse.builder().build(), HttpStatus.ACCEPTED);
  }

  @PostMapping({"/v0.5/consent-requests/on-status"})
  public ResponseEntity<GatewayCallbackResponse> consentOnStatus(
      @RequestBody HIUConsentOnStatusRequest HIUConsentOnStatusRequest)
      throws IllegalDataStateException {
    gatewayCallback.consentOnStatus(HIUConsentOnStatusRequest);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PostMapping({"/v0.5/consents/hiu/notify"})
  public ResponseEntity<GatewayCallbackResponse> onInitConsent(
      @RequestBody NotifyHIURequest notifyHIURequest) throws IllegalDataStateException {
    gatewayCallback.hiuNotify(notifyHIURequest);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PostMapping({"/v0.5/consents/on-fetch"})
  public ResponseEntity<GatewayCallbackResponse> onFetchConsent(
      @RequestBody OnFetchRequest onFetchRequest) throws IllegalDataStateException {
    gatewayCallback.consentOnFetch(onFetchRequest);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @ExceptionHandler(IllegalDataStateException.class)
  private ResponseEntity<GatewayCallbackResponse> handleIllegalDataStateException(
      IllegalDataStateException ex) {
    return new ResponseEntity<>(
        GatewayCallbackResponse.builder()
            .error(ErrorResponse.builder().message(ex.getMessage()).build())
            .build(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
