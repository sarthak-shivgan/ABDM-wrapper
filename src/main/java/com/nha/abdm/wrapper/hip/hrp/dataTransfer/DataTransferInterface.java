/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer;

import com.nha.abdm.wrapper.common.exceptions.IllegalDataStateException;
import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.BundleResponseHIP;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.HIPHealthInformationRequest;

public interface DataTransferInterface {

  void requestOnReceived(HIPHealthInformationRequest data) throws IllegalDataStateException;

  void initiateBundleRequest(HIPHealthInformationRequest HIPHealthInformationRequest)
      throws IllegalDataStateException;

  FacadeResponse initiateDataTransfer(BundleResponseHIP bundleResponseHIP) throws Exception;
}
