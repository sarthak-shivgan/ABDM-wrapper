/* (C) 2024 */
package com.nha.abdm.wrapper.common.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HealthInformationEntry {
  private String content;
  private String media;
  private String checksum;
  private String careContextReference;
}
