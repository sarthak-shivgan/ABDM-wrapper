package com.nha.abdm.wrapper.facade.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.controller.GatewayService;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;

import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.AddPatient;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.serviceImpl.PatientTableService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HIP {
	@Autowired
	GatewayService gatewayService;
	private static final Logger log = LogManager.getLogger(HIP.class);
	@Autowired
	PatientTableService patientTableService;


	@PostMapping({"/link-records"})
	public Object hipInitiatedLinking(@RequestBody LinkRecordsResponse data) throws JsonProcessingException, URISyntaxException, FileNotFoundException, ParseException {
		return gatewayService.linkRecords(data);
	}

	@PostMapping({"/get-status"})
	public String getStatusOfCareContext(@RequestBody JsonNode data) throws JsonProcessingException, URISyntaxException, FileNotFoundException {
		return gatewayService.getCareContextRequestStatus(data);
	}
	@PostMapping({"/v0.5/users/auth/on-fetch-modes"})
	public void fetchAuthModes(@RequestBody JsonNode data) {
		log.info(data.toPrettyString());
	}

	@PostMapping({"/set-bridge"})
	public void setBridgeUrl(@RequestBody JsonNode data){

	}
	@PostMapping({"/add-patient"})
	public void addPatient(@RequestBody AddPatient data){
		patientTableService.addPatient(data);
	}
}
