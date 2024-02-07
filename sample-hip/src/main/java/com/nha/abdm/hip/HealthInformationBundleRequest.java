/* (C) 2024 */
package com.nha.abdm.hip;


import java.io.Serializable;
import java.util.List;

public class HealthInformationBundleRequest implements Serializable {
  private static final long serialVersionUID = 165269402517398406L;
  private List<ConsentCareContexts> careContextsWithPatientReferences;
}
