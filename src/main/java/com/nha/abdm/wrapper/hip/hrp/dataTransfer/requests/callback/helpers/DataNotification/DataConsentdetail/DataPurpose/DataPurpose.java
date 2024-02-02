/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers.DataNotification.DataConsentdetail.DataPurpose;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataPurpose {
  public String code;
  public String refUri;
  public String text;
}
