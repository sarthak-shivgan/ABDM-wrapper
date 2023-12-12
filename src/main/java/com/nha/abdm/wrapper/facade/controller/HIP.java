package com.nha.abdm.wrapper.facade.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.controller.GatewayController;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HIP {
	@Autowired
	GatewayController gatewayController;
	private static final Logger log = LogManager.getLogger(HIP.class);


	@PostMapping({"/link-records"})
	public ObjectNode hipInitiatedLinking(@RequestBody LinkRecordsResponse data) throws JsonProcessingException, URISyntaxException, FileNotFoundException {
		return this.gatewayController.linkRecords(data);
	}

	@PostMapping({"/getStatus"})
	public String getStatusOfCareContext(@RequestBody JsonNode data) throws JsonProcessingException, URISyntaxException, FileNotFoundException {
		return this.gatewayController.getStatus(data);
	}
	@PostMapping({"/v0.5/users/auth/on-fetch-modes"})
	public void fetchAuthModes(@RequestBody JsonNode data) {
		log.info(data.toPrettyString());
	}
}
