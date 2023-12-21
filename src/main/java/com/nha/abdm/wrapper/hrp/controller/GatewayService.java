package com.nha.abdm.wrapper.hrp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.services.DiscoverLinkingService;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.OnConfirmResponse;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.services.HipInitiatedService;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.OnInitResponse;
import com.nha.abdm.wrapper.hrp.mongo.tables.Patient;
import com.nha.abdm.wrapper.hrp.repository.PatientRepo;
import com.nha.abdm.wrapper.hrp.serviceImpl.LogsTableService;
import com.nha.abdm.wrapper.hrp.serviceImpl.PatientTableService;
import com.nha.abdm.wrapper.hrp.serviceImpl.TokenManagementService;
import com.nha.abdm.wrapper.hrp.services.CommonServices;
import com.nha.abdm.wrapper.hrp.common.Utils;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Component
public class GatewayService {
	private static final Logger log = LogManager.getLogger(GatewayService.class);
	RestTemplate restTemplate;
	@Autowired
	Utils utils;
	@Autowired
	HipInitiatedService hipInitiatedService;
	@Autowired
	DiscoverLinkingService discoverLinkingService;
	@Autowired
	TokenManagementService tokenManagementService;
	@Autowired
	PatientTableService patientTableService;
	@Autowired
	PatientRepo patientRepo;
	@Autowired
	LogsTableService logsTableService;

	public String requestId = "";

	public Object linkRecords(LinkRecordsResponse data) throws JsonProcessingException, URISyntaxException, FileNotFoundException, ParseException {
		String accessToken=tokenManagementService.fetchToken(patientTableService.getAbhaAddress(data.getPatientReference()));
		if(accessToken!=null){
			Patient patient=patientRepo.findByPatientReference(data.getPatientReference());
			hipInitiatedService.addContextAccessToken(data,accessToken,patient);
		}
		return hipInitiatedService.authInit(data);
	}


	public void startConfirmCall(@RequestBody OnInitResponse data) throws FileNotFoundException, URISyntaxException, JsonProcessingException {
		ResponseEntity<ObjectNode> confirmCall = hipInitiatedService.confirmAuth(data);

	}

	public void startAddCareContextCall(OnConfirmResponse data) throws Exception {
		if (data.getError() == null) {
			ResponseEntity<ObjectNode> addCareContextCAll = hipInitiatedService.addContext(data);

		}
	}

	public void startOnDiscoverCall(DiscoverResponse data) throws URISyntaxException, JsonProcessingException {
		if(data!=null){
			discoverLinkingService.onDiscoverCall(data);
		}
	}

	public void startOnInitCall(InitResponse data) throws URISyntaxException, JsonProcessingException {
		if(data!=null){
			discoverLinkingService.onInitCall(data);
		}
	}

	public void startOnConfirmCall(ConfirmResponse data) throws URISyntaxException, JsonProcessingException {
		if(data!=null){
			discoverLinkingService.onConfirmCall(data);
		}
	}
		public String getCareContextRequestStatus(JsonNode data) {
		return logsTableService.getStatus(data);
	}
}
