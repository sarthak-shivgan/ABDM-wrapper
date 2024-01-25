/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.hipInitiated;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.models.FacadeResponse;
import com.nha.abdm.wrapper.common.models.VerifyOTP;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkRecordsRequest;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnInitResponse;

public interface HipLinkInterface {
  FacadeResponse hipAuthInit(LinkRecordsRequest linkRecordsRequest);

  void confirmAuthDemographics(LinkOnInitResponse data) throws IllegalDataStateException;

  FacadeResponse confirmAuthOtp(VerifyOTP data) throws IllegalDataStateException;

  void hipAddCareContext(LinkOnConfirmResponse data) throws IllegalDataStateException;
}
