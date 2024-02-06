/* (C) 2024 */
package com.nha.abdm.wrapper.hip.facade.dataTransfer;

import com.nha.abdm.wrapper.common.responses.FacadeResponse;
import com.nha.abdm.wrapper.hip.hrp.WorkflowManager;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.BundleResponseHIP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/data-transfer")
public class HIPFacadeDataController {
  private static final Logger log = LogManager.getLogger(HIPFacadeDataController.class);
  @Autowired WorkflowManager workflowManager;

  @PostMapping({"/records"})
  public FacadeResponse bundleTransfer(@RequestBody BundleResponseHIP bundleResponseHIP)
      throws Exception {
    return workflowManager.initiateDataTransfer(bundleResponseHIP);
  }
}
