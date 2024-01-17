/* (C) 2024 */
package com.nha.abdm.wrapper.common;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ErrorResponse {
  public int code;
  public String message;
}
