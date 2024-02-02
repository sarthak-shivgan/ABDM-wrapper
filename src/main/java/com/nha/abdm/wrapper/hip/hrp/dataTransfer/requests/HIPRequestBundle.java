package com.nha.abdm.wrapper.hip.hrp.dataTransfer.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HIPRequestBundle {
    public List<String> careContextReference;

}
