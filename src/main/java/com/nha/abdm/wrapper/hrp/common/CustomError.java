package com.nha.abdm.wrapper.hrp.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Data
public class CustomError {
    public int code;
    public String message;
}
