//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.ServiceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.Properties.ApplicationConstantConfig;
import com.wrapper.Services.CommonServices;
import com.wrapper.Utility.CommonUtility;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommonServiceImpl implements CommonServices {
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	ApplicationConstantConfig applicationConstantConfig;
	@Autowired
	CareContextTableService careContextTableService;
	private static final Logger log = LogManager.getLogger(CommonServiceImpl.class);

	public CommonServiceImpl() {
	}

	public String startSession() throws JsonProcessingException, URISyntaxException {
		RestTemplate template = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode requestBody = mapper.createObjectNode();
		requestBody.put("clientId", this.applicationConstantConfig.clientId);
		requestBody.put("clientSecret", this.applicationConstantConfig.clientSecret);
		String json = mapper.writeValueAsString(requestBody);
		HttpEntity<String> requestEntity = new HttpEntity(json, headers);
		ResponseEntity<JsonNode> responseEntity = template.exchange(new URI(this.applicationConstantConfig.url), HttpMethod.POST, requestEntity, JsonNode.class);
		this.commonUtility.setAccessToken(((JsonNode)responseEntity.getBody()).findValue("accessToken"));
		return this.commonUtility.getAccessToken();
	}

	public String getStatus(JsonNode data) {
		return this.careContextTableService.getStatus(data.get("requestId").asText());
	}
}
