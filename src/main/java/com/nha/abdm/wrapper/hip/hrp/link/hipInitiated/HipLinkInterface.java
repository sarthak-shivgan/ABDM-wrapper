/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.hipInitiated;

import com.nha.abdm.wrapper.common.models.FacadeResponse;
import com.nha.abdm.wrapper.common.models.VerifyOTP;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkRecordsResponse;
import java.util.concurrent.TimeoutException;

public interface HipLinkInterface {
  FacadeResponse hipAuthInit(LinkRecordsResponse linkRecordsResponse);

  void hipConfirmCall(LinkOnInitResponse data) throws TimeoutException;

  void hipAddCareContext(LinkOnConfirmResponse data);

  void hipConfirmCallOtp(VerifyOTP data);
}
