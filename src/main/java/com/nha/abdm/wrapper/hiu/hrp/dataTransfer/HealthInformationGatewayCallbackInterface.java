/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.requests.OnHealthInformationRequest;
import org.springframework.http.HttpStatus;

public interface HealthInformationGatewayCallbackInterface {
  HttpStatus onHealthInformationRequest(OnHealthInformationRequest onHealthInformationRequest)
      throws IllegalDataStateException;
}
