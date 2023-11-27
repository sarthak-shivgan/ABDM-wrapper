//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URISyntaxException;

public interface CommonServices {
	String startSession() throws JsonProcessingException, URISyntaxException;

	String getStatus(JsonNode data);
}
