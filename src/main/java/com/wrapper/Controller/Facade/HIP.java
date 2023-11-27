package com.wrapper.Controller.Facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.Controller.HRP.HipGateway;
import com.wrapper.ResponseController.LinkRecordsResponse;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HIP {
	@Autowired
	HipGateway hipGateway;
	private static final Logger log = LogManager.getLogger(HIP.class);

	public HIP() {
	}

	@PostMapping({"/linkRecords"})
	public ObjectNode hipInitiatedLinking(@RequestBody LinkRecordsResponse data) throws JsonProcessingException, URISyntaxException, FileNotFoundException {
		return this.hipGateway.linkRecords(data);
	}

	@PostMapping({"/getStatus"})
	public String getStatusOfCareContext(@RequestBody JsonNode data) throws JsonProcessingException, URISyntaxException, FileNotFoundException {
		return this.hipGateway.getStatus(data);
	}

	@PostMapping({"/v0.5/users/auth/on-fetch-modes"})
	public void fetchAuthModes(@RequestBody JsonNode data) {
		log.info(data.toPrettyString());
	}
}
