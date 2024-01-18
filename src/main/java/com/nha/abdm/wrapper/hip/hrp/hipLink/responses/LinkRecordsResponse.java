/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.hipLink.responses;

import com.nha.abdm.wrapper.hip.hrp.link.responses.helpers.PatientWithCareContexts;
import java.io.Serializable;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Data
@Component
@NonNull public class LinkRecordsResponse implements Serializable {
  private static final long serialVersionUID = 165269402517398406L;

  public String requestId;
  private String requesterId;
  private String abhaAddress;
  private String authMode;
  private PatientWithCareContexts patient;
}
