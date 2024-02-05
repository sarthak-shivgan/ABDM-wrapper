/* (C) 2024 */
package com.nha.abdm.wrapper.hiu.hrp.consent.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purpose {
  private String text;
  private String code;
  private String refUri;
}
