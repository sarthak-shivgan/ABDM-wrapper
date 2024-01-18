/* (C) 2024 */
package com.nha.abdm.wrapper.hip.facade.link;

import com.nha.abdm.wrapper.common.models.AckRequest;
import com.nha.abdm.wrapper.common.models.VerifyOtp;
import com.nha.abdm.wrapper.hip.hrp.WorkflowManager;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkRecordsResponse;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/care-contexts")
public class HIPFacadeLinkController {
  private static final Logger log = LogManager.getLogger(HIPFacadeLinkController.class);
  @Autowired WorkflowManager workflowManager;

  @GetMapping({"/link-status/{requestId}"})
  public AckRequest fetchCareContextStatus(@PathVariable String requestId) {
    return workflowManager.getCareContextRequestStatus(requestId);
  }

  @PostMapping({"/link-records"})
  public AckRequest linkRecords(@RequestBody LinkRecordsResponse data) {
    return workflowManager.startHipAuthInit(data);
  }

  @PostMapping({"/verifyOtp"})
  public void verifyOtp(@RequestBody VerifyOtp data) {
    log.info(data.toString());
    if (Objects.equals(data.getLoginHint(), "hipLinking")) {
      workflowManager.startHipConfirmCallOTP(data);
    }
  }

  @PostMapping({"/add-patient"})
  public AckRequest storePatient(@RequestBody Patient data) {
    return workflowManager.storePatientInWrapper(data);
  }
}
