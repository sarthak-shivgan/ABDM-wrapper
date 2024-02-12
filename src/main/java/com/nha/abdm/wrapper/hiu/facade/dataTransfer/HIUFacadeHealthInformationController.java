/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.facade.dataTransfer;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hiu.hrp.consent.responses.HealthInformationResponse;
import com.nha.abdm.wrapper.hiu.hrp.dataTransfer.HIUFacadeHealthInformationInterface;
import com.nha.abdm.wrapper.hiu.hrp.dataTransfer.requests.HIUClientHealthInformationRequest;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/health-information")
public class HIUFacadeHealthInformationController {

  @Autowired private HIUFacadeHealthInformationInterface hiuFacadeHealthInformationInterface;

  @PostMapping({"/fetch-records"})
  public ResponseEntity<FacadeResponse> healthInformation(
      @RequestBody HIUClientHealthInformationRequest hiuClientHealthInformationRequest)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException,
          IllegalDataStateException {
    FacadeResponse facadeResponse =
        hiuFacadeHealthInformationInterface.healthInformation(hiuClientHealthInformationRequest);
    return new ResponseEntity<>(facadeResponse, facadeResponse.getHttpStatusCode());
  }

  @GetMapping({"/status/{requestId}"})
  public ResponseEntity<HealthInformationResponse> getHealthInformationRequestStatus(
      @PathVariable("requestId") String requestId)
      throws IllegalDataStateException, InvalidCipherTextException, NoSuchAlgorithmException,
          InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
    HealthInformationResponse healthInformationResponse =
        hiuFacadeHealthInformationInterface.getHealthInformation(requestId);
    return new ResponseEntity<>(
        healthInformationResponse, healthInformationResponse.getHttpStatusCode());
  }
}
