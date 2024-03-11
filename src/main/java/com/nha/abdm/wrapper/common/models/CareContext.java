/* (C) 2024 */
package com.nha.abdm.wrapper.common.models;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CareContext {
  public String referenceNumber;
  public String display;
}
