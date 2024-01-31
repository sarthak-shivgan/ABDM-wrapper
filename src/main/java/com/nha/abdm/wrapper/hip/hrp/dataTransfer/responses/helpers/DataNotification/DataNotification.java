/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.helpers.DataNotification;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.helpers.DataNotification.DataConsentdetail.DataConsentDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataNotification {
  public String consentId;
  public String grantAcknowledgement;
  public String signature;
  public String status;
  public DataConsentDetail consentDetail;
}
