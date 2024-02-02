/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests.callback.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataEntries {
  private String content;
  private String media;
  private String checksum;
  private String careContextReference;
}
