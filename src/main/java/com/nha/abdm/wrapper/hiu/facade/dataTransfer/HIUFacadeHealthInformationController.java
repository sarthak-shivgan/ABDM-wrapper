/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.facade.dataTransfer;

import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hiu.hrp.dataTransfer.HIUFacadeHealthInformationInterface;
import com.nha.abdm.wrapper.hiu.hrp.dataTransfer.requests.HIUClientHealthInformationRequest;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/health-information")
public class HIUFacadeHealthInformationController {

  @Autowired private HIUFacadeHealthInformationInterface hiuFacadeHealthInformationInterface;

  @PostMapping({"/fetch-records"})
  public ResponseEntity<FacadeResponse> healthInformation(
      @RequestBody HIUClientHealthInformationRequest hiuClientHealthInformationRequest)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
    FacadeResponse facadeResponse =
        hiuFacadeHealthInformationInterface.healthInformation(hiuClientHealthInformationRequest);
    return new ResponseEntity<>(facadeResponse, facadeResponse.getHttpStatusCode());
  }
}
