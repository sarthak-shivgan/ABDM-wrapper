/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent.requests;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitConsentRequest {
  private String requestId;
  private String timestamp;
  private ConsentRequest consent;
  private ConsentPatient patient;
  private ConsentHIP hip;
  private List<ConsentCareContexts> careContexts;
  private ConsentHIU hiu;
  private ConsentRequester requester;
  private List<String> hiTypes;
  private Frequency frequency;
}
