/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.responses.helpers;

import java.util.List;
import lombok.Data;

@Data
public class PatientDemographicDetails {
  public String name;
  public String gender;
  public String id;

  public String yearOfBirth;
  public List<PatientVerifiedIdentifiers> verifiedIdentifiers;
  public List<PatientUnverifiedIdentifiers> unverifiedIdentifiers;
}
