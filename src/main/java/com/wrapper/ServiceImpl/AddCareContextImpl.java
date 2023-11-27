//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.ServiceImpl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.Properties.ApplicationConstantConfig;
import com.wrapper.RequestBody.AddCareContextBody;
import com.wrapper.ResponseController.OnConfirmResponse;
import com.wrapper.Services.AddCareContext;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AddCareContextImpl implements AddCareContext {
	private static final Logger log = LogManager.getLogger(AddCareContextImpl.class);
	RestTemplate restTemplate = new RestTemplate();
	@Autowired
	ApplicationConstantConfig applicationConstantConfig;
	@Autowired
	AddCareContextBody addCareContextBody;

	public AddCareContextImpl() {
	}

	public ResponseEntity addContext(OnConfirmResponse data) throws URISyntaxException, FileNotFoundException {
		ResponseEntity<ObjectNode> responseEntity = this.restTemplate.exchange(new URI(this.applicationConstantConfig.link_addCareContext), HttpMethod.POST, this.addCareContextBody.makeRequest(data), ObjectNode.class);
		return responseEntity;
	}
}
