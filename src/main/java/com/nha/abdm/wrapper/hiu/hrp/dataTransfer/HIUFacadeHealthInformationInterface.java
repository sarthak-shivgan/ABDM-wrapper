/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hiu.hrp.dataTransfer.requests.HIUClientHealthInformationRequest;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface HIUFacadeHealthInformationInterface {
  FacadeResponse healthInformation(
      HIUClientHealthInformationRequest hiuClientHealthInformationRequest)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
}
