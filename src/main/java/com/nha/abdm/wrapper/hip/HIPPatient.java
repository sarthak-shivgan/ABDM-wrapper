/* (C) 2024 */
package com.nha.abdm.wrapper.hip;

import com.nha.abdm.wrapper.common.models.CareContext;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class HIPPatient {
  private String abhaAddress;
  private String name;
  private String gender;
  private String dateOfBirth;
  private String patientReference;
  private String patientDisplay;
  private String patientMobile;
  private List<CareContext> careContexts;
}
