/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.hiu.hrp.consent.requests.callback.*;

public interface GatewayCallbackInterface {
  void onInitConsent(OnInitRequest onInitRequest);

  void consentOnStatus(HIUConsentOnStatusRequest HIUConsentOnStatusRequest)
      throws IllegalDataStateException;

  void hiuNotify(NotifyHIURequest notifyHIURequest) throws IllegalDataStateException;

  void consentOnFetch(OnFetchRequest onFetchRequest) throws IllegalDataStateException;
}
