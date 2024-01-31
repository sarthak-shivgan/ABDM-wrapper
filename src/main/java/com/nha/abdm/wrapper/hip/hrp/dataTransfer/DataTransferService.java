/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.responses.GatewayCallbackResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.BundleResponseHIP;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.DataNotifyResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.DataRequestResponse;
import org.springframework.stereotype.Service;

@Service
public class DataTransferService implements DataTransferInterface {
  @Override
  public GatewayCallbackResponse DataOnNotifyCall(DataNotifyResponse dataNotifyResponse) {
    // TODO Implement onNotify workflow logic.
    return null;
  }

  @Override
  public GatewayCallbackResponse DataOnRequestCall(DataRequestResponse dataRequestResponse) {
    // TODO Implement onRequest workflow logic.
    return null;
  }

  @Override
  public GatewayCallbackResponse initiateBundleRequest(DataRequestResponse dataRequestResponse) {
    // TODO Implement bundle request to HIP workflow logic.
    return null;
  }

  @Override
  public GatewayCallbackResponse initiateDataTransfer(BundleResponseHIP bundleResponseHIP)
      throws Exception {
    // TODO Implement dataPush workflow logic.
    return null;
  }
}
