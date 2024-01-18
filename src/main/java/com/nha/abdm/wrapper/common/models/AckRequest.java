/* (C) 2024 */
package com.nha.abdm.wrapper.common.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nha.abdm.wrapper.common.ErrorResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AckRequest {
  private String requestId;
  private int code;
  private String message;
  private ErrorResponse error;
}
