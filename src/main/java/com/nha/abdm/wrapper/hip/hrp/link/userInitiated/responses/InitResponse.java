/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses;

import com.nha.abdm.wrapper.common.responses.ErrorResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.helpers.PatientWithCareContexts;
import java.io.Serializable;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class InitResponse implements Serializable {

  private static final long serialVersionUID = 165269402517398406L;

  public ErrorResponse error;

  public String requestId;

  public String transactionId;

  public PatientWithCareContexts patient;
}
