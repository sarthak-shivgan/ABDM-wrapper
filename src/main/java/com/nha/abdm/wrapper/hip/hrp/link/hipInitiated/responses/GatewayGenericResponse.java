/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses;

import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GatewayGenericResponse {
  private HttpStatus httpStatus;
  private ErrorResponse errorResponse;
}
