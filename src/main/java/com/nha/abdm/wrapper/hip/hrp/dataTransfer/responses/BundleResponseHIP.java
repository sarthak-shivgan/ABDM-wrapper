/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.dataTransfer.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BundleResponseHIP {
  String consentId;
  String bundleContent;
}
