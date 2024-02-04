package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests;

import java.util.List;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataNotification.DataConsentdetail.DataCareContexts.CareContextsWithPatientReference;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HIPRequestBundle {
  public List<CareContextsWithPatientReference> careContextsWithPatientReferences;
  public List<String> careContextReference;
}
