/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent.responses;

import com.nha.abdm.wrapper.common.models.Consent;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.helpers.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentResponse {
  private RequestStatus status;
  private String error;
  private HttpStatusCode httpStatusCode;
  private Consent consent;
}
