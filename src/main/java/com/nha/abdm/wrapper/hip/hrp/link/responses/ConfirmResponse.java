/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.responses;

import com.nha.abdm.wrapper.common.ErrorResponse;
import com.nha.abdm.wrapper.hip.hrp.link.responses.helpers.Confirmation;
import java.io.Serializable;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ConfirmResponse implements Serializable {

  private static final long serialVersionUID = 165269402517398406L;

  public String requestId;

  public ErrorResponse error;

  public Confirmation confirmation;
}
