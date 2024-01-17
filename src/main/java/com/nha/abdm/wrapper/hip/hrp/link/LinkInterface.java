/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link;

import com.nha.abdm.wrapper.hip.hrp.link.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.responses.InitResponse;

public interface LinkInterface {
  void onInit(InitResponse initResponse);

  void onConfirm(ConfirmResponse confirmResponse);
}
