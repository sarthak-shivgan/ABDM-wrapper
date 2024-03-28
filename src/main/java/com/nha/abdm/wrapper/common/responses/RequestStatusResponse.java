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
public class RequestStatusResponse {
  private String requestId;
  private String status;
  private ErrorResponse error;
}
