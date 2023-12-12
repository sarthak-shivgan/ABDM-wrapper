package com.nha.abdm.wrapper.hrp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.services.DiscoverLinkingService;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.services.HipInitiatedService;
import com.nha.abdm.wrapper.hrp.properties.ApplicationConfig;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.OnConfirmResponse;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.OnInitResponse;
import com.nha.abdm.wrapper.hrp.services.CommonServices;
import com.nha.abdm.wrapper.hrp.common.Utils;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class GatewayController {
	private static final Logger log = LogManager.getLogger(GatewayController.class);
	RestTemplate restTemplate;
	@Autowired
	ApplicationConfig applicationConfig;
	@Autowired
	Utils utils;
	@Autowired
	HipInitiatedService hipInitiatedService;
	@Autowired
	DiscoverLinkingService discoverLinkingService;
	@Autowired
	CommonServices commonServices;

	public String requestId = "";

	public ObjectNode linkRecords(LinkRecordsResponse data) throws JsonProcessingException, URISyntaxException, FileNotFoundException {
		if (this.utils.accessToken == null) {
			this.commonServices.startSession();
		}

		return hipInitiatedService.authInit(data);
	}

	public String getStatus(JsonNode data) throws JsonProcessingException, URISyntaxException, FileNotFoundException {
		return this.commonServices.getStatus(data);
	}

	public String startConfirmCall(@RequestBody OnInitResponse data) throws FileNotFoundException, URISyntaxException {
		ResponseEntity<ObjectNode> confirmCall = hipInitiatedService.getConfirmAuth(data);
		if (!confirmCall.getStatusCode().is2xxSuccessful()) {
			return "Failed to initiate ConfirmAuth api";
		} else {
			log.info("Completed confirm call");
			return "Completed ConfirmAuth Api";
		}
	}

	public String startAddCareContextCall(OnConfirmResponse data) throws FileNotFoundException, URISyntaxException {
		if (data.getError() == null) {
			ResponseEntity<ObjectNode> addCareContextCAll = hipInitiatedService.addContext(data);
			if (!addCareContextCAll.getStatusCode().is2xxSuccessful()) {
				return "Failed at Adding care context";
			}
		}

		return "Success";
	}

	public void startOnDiscoverCall(DiscoverResponse data) throws URISyntaxException, JsonProcessingException {
		if (utils.accessToken == null) {commonServices.startSession();}
		if(data!=null){
			discoverLinkingService.onDiscoverCall(data);
		}
	}

	public void startOnInitCall(InitResponse data) throws URISyntaxException, JsonProcessingException {
		if (utils.accessToken == null) {commonServices.startSession();}
		if(data!=null){
			discoverLinkingService.onInitCall(data);
		}
	}

	public void startOnConfirmCall(ConfirmResponse data) throws URISyntaxException, JsonProcessingException {
		if (utils.accessToken == null) {commonServices.startSession();}
		if(data!=null){
			discoverLinkingService.onConfirmCall(data);
		}
	}
}
