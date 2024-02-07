/* (C) 2024 */
package com.nha.abdm.wrapper.common.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GenericResponse {
  private HttpStatus httpStatus;
  private ErrorResponse errorResponse;
}
