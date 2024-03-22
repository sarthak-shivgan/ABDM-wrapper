/* (C) 2024 */
package com.nha.abdm.wrapper.common.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  public String code;
  public String message;
}
