/* (C) 2024 */
package com.nha.abdm.wrapper.common.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacadeResponse {
  private String clientRequestId;
  private int code;
  private HttpStatusCode httpStatusCode;
  private String message;
  private ErrorResponse error;
}
