//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.ServiceImpl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.Properties.ApplicationConstantConfig;
import com.wrapper.RequestBody.AuthInitBody;
import com.wrapper.ResponseController.LinkRecordsResponse;
import com.wrapper.Services.Link_init;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Link_authInitImpl implements Link_init {
	private static final Logger log = LogManager.getLogger(Link_authInitImpl.class);
	RestTemplate restTemplate = new RestTemplate();
	@Autowired
	ApplicationConstantConfig applicationConstantConfig;
	@Autowired
	AuthInitBody authInitBody;

	public Link_authInitImpl() {
	}

	public ObjectNode authInit(LinkRecordsResponse data) throws URISyntaxException {
		HttpEntity<ObjectNode> requestEntity = this.authInitBody.makeRequest(data);
		ResponseEntity<ObjectNode> responseEntity = this.restTemplate.exchange(new URI(this.applicationConstantConfig.link_authInit), HttpMethod.POST, requestEntity, ObjectNode.class);
		log.info(responseEntity.getStatusCode());
		ObjectNode responseData = new ObjectNode(JsonNodeFactory.instance);
		responseData.put("Status Code : ", responseEntity.getStatusCode().toString());
		responseData.put("initRequestId :", ((ObjectNode)requestEntity.getBody()).get("requestId"));
		return responseData;
	}
}
