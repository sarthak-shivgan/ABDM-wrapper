/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.hipInitiated;

import com.nha.abdm.wrapper.common.models.FacadeResponse;
import com.nha.abdm.wrapper.common.models.VerifyOTP;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkRecordsRequest;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnInitResponse;

public interface HipLinkInterface {
  FacadeResponse hipAuthInit(LinkRecordsRequest linkRecordsRequest);

  void hipConfirmCall(LinkOnInitResponse data);

  void hipAddCareContext(LinkOnConfirmResponse data);

  FacadeResponse hipConfirmCallOtp(VerifyOTP data);
}
