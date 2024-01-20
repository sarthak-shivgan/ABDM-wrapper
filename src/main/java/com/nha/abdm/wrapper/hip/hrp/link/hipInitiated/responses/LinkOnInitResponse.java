/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses;

import com.nha.abdm.wrapper.common.ErrorResponse;
import com.nha.abdm.wrapper.hip.hrp.discover.requests.Response;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.helpers.LinkAuthData;
import java.io.Serializable;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class LinkOnInitResponse implements Serializable {
  private static final long serialVersionUID = 165269402517398406L;
  private String requestId;
  private String timestamp;
  private LinkAuthData auth;
  private Response resp;
  private ErrorResponse error;
}
