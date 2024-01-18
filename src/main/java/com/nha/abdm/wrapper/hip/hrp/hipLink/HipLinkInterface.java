/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.hipLink;

import com.nha.abdm.wrapper.common.models.AckRequest;
import com.nha.abdm.wrapper.common.models.VerifyOtp;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkRecordsResponse;
import java.util.concurrent.TimeoutException;

public interface HipLinkInterface {
  AckRequest hipAuthInit(LinkRecordsResponse linkRecordsResponse);

  void hipConfirmCall(LinkOnInitResponse data) throws TimeoutException;

  void hipAddCareContext(LinkOnConfirmResponse data);

  void hipConfirmCallOtp(VerifyOtp data);
}
