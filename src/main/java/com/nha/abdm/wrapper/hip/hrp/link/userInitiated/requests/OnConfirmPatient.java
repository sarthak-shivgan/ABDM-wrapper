/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.userInitiated.requests;

import com.nha.abdm.wrapper.common.models.CareContext;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnConfirmPatient {

  private String referenceNumber;
  private String display;
  private List<CareContext> careContexts;
}
