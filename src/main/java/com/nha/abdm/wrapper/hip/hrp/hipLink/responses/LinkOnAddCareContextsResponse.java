/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.hipLink.responses;

import com.nha.abdm.wrapper.common.ErrorResponse;
import com.nha.abdm.wrapper.hip.hrp.discover.requests.Response;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.helpers.LinkAcknowledgement;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LinkOnAddCareContextsResponse implements Serializable {
  private static final long serialVersionUID = 165269402517398406L;
  //    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
  private String timestamp;
  private LinkAcknowledgement acknowledgement;
  private ErrorResponse error;
  private Response resp;
}
