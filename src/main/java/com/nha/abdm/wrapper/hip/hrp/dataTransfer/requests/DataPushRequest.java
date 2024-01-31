/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests;

import com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.helpers.DataEntries;
import com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses.helpers.DataHiRequest.DatakeyMaterial.DataKeyMaterial;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataPushRequest {
  private int pageNumber;
  private int pageCount;
  private String transactionId;
  private List<DataEntries> entries;
  private DataKeyMaterial keyMaterial;
}
