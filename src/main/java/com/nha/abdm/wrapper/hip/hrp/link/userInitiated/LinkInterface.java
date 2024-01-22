/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.userInitiated;

import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.InitResponse;

public interface LinkInterface {
  void onInit(InitResponse initResponse);

  void onConfirm(ConfirmResponse confirmResponse);
}
