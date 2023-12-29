package com.nha.abdm.wrapper.hrp.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.services.DiscoverLinkingService;
import com.nha.abdm.wrapper.hrp.serviceImpl.LogsTableService;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkflowManager {
	private static final Logger log = LogManager.getLogger(WorkflowManager.class);
	@Autowired
	DiscoverLinkingService discoverLinkingService;
	@Autowired
	LogsTableService logsTableService;


	public void startOnDiscoverCall(DiscoverResponse data) throws URISyntaxException, JsonProcessingException {
		if(data!=null){
			discoverLinkingService.onDiscoverCall(data);
		}else{
			log.error("Error in Discover response from gateWay");
		}
	}

	public void startOnInitCall(InitResponse data) throws URISyntaxException, JsonProcessingException {
		if(data!=null){
			discoverLinkingService.onInitCall(data);
		}else{
			log.error("Error in Init response from gateWay");
		}
	}

	public void startOnConfirmCall(ConfirmResponse data) throws URISyntaxException, JsonProcessingException {
		if(data!=null){
			discoverLinkingService.onConfirmCall(data);
		}else{
			log.error("Error in Confirm response from gateWay");
		}
	}
	public String getCareContextRequestStatus(JsonNode data) {
		return logsTableService.getStatus(data);
	}
}
