/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.responses.GatewayCallbackResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.BundleResponseHIP;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.DataNotifyResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.DataRequestResponse;

public interface DataTransferInterface {
  GatewayCallbackResponse DataOnNotifyCall(DataNotifyResponse data);

  GatewayCallbackResponse DataOnRequestCall(DataRequestResponse data);

  GatewayCallbackResponse initiateBundleRequest(DataRequestResponse dataRequestResponse);

  GatewayCallbackResponse initiateDataTransfer(BundleResponseHIP bundleResponseHIP) throws Exception;
}
