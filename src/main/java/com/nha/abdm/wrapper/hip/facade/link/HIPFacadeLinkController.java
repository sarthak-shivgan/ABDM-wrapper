/* (C) 2024 */
package com.nha.abdm.wrapper.hip.facade.link;

import com.nha.abdm.wrapper.hip.hrp.WorkflowManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/care-contexts")
public class HIPFacadeLinkController {
  @Autowired WorkflowManager workflowManager;

  @GetMapping({"/{requestId}"})
  public String fetchCareContextStatus(@PathVariable String requestId) {
    return workflowManager.getCareContextRequestStatus(requestId);
  }
}
