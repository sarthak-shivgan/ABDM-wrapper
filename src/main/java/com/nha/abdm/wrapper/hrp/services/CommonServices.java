
package com.nha.abdm.wrapper.hrp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URISyntaxException;

public interface CommonServices {
	String startSession() throws JsonProcessingException, URISyntaxException;

//	String getCareContextRequestStatus(JsonNode data);
}
