/* (C) 2024 */
package com.nha.abdm.wrapper.hrp.discoveryLinking.responses.helpers;

import java.util.List;
import lombok.Data;

@Data
public class PatientWithCareContexts {

  private String id;

  private String referenceNumber;

  private List<InitCareContextList> careContexts;
}
