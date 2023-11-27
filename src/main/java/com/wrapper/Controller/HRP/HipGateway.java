package com.wrapper.Controller.HRP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.Properties.ApplicationConstantConfig;
import com.wrapper.ResponseController.LinkRecordsResponse;
import com.wrapper.ResponseController.OnConfirmResponse;
import com.wrapper.ResponseController.OnInitResponse;
import com.wrapper.ServiceImpl.CareContextTableService;
import com.wrapper.Services.AddCareContext;
import com.wrapper.Services.CommonServices;
import com.wrapper.Services.ConfirmAuth;
import com.wrapper.Services.Link_init;
import com.wrapper.Utility.CommonUtility;
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
public class HipGateway {
	private static final Logger log = LogManager.getLogger(HipGateway.class);
	RestTemplate restTemplate;
	@Autowired
	ApplicationConstantConfig applicationConstantConfig;
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	Link_init linkInit;
	@Autowired
	ConfirmAuth confirmAuth;
	@Autowired
	AddCareContext addCareContext;
	@Autowired
	CommonServices commonServices;
	@Autowired
	CareContextTableService careContextTableService;
	public String requestId = "";

	public HipGateway() {
	}

	public ObjectNode linkRecords(LinkRecordsResponse data) throws JsonProcessingException, URISyntaxException, FileNotFoundException {
		if (this.commonUtility.accessToken == null) {
			this.commonServices.startSession();
		}

		return this.linkInit.authInit(data);
	}

	public String getStatus(JsonNode data) throws JsonProcessingException, URISyntaxException, FileNotFoundException {
		return this.commonServices.getStatus(data);
	}

	public String startConfirmCall(@RequestBody OnInitResponse data) throws FileNotFoundException, URISyntaxException {
		ResponseEntity<ObjectNode> confirmCall = this.confirmAuth.getConfirmAuth(data);
		if (!confirmCall.getStatusCode().is2xxSuccessful()) {
			return "Failed to initiate ConfirmAuth api";
		} else {
			log.info("Completed confirm call");
			return "Completed ConfirmAuth Api";
		}
	}

	public String startAddCareContextCall(OnConfirmResponse data) throws FileNotFoundException, URISyntaxException {
		if (data.getError() == null) {
			ResponseEntity<ObjectNode> addCareContextCAll = this.addCareContext.addContext(data);
			if (!addCareContextCAll.getStatusCode().is2xxSuccessful()) {
				return "Failed at Adding care context";
			}
		}

		return "Success";
	}
}
