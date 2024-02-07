/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.requests.HealthInformationPushRequest;
import com.nha.abdm.wrapper.common.responses.GenericResponse;

public interface HealthInformationInterface {
  GenericResponse processEncryptedHealthInformation(
      HealthInformationPushRequest healthInformationPushRequest);
}
