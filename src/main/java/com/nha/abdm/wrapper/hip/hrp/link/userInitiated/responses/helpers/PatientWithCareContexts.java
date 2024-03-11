/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.helpers;

import com.nha.abdm.wrapper.common.models.CareContext;
import java.util.List;
import lombok.Data;

@Data
public class PatientWithCareContexts {

  private String id;
  private String referenceNumber;
  private List<CareContext> careContexts;
}
