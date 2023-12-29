package com.nha.abdm.wrapper.hrp.common;


import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CustomError {
    public int code;
    public String message;
}
