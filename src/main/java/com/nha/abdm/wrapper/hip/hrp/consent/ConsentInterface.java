/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.consent;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.hip.hrp.consent.requests.HIPNotifyRequest;

public interface ConsentInterface {
  void hipNotify(HIPNotifyRequest hipNotifyRequest) throws IllegalDataStateException;
}
