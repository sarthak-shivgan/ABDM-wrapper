//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.ServiceImpl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.Properties.ApplicationConstantConfig;
import com.wrapper.RequestBody.ConfirmAuthBody;
import com.wrapper.ResponseController.OnInitResponse;
import com.wrapper.Services.ConfirmAuth;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Service
public class ConfirmAuthImpl implements ConfirmAuth {
	private static final Logger log = LogManager.getLogger(ConfirmAuthImpl.class);
	RestTemplate restTemplate = new RestTemplate();
	@Autowired
	ApplicationConstantConfig applicationConstantConfig;
	@Autowired
	ConfirmAuthBody confirmAuthBody;

	public ConfirmAuthImpl() {
	}

	public ResponseEntity<ObjectNode> getConfirmAuth(@RequestBody OnInitResponse data) throws URISyntaxException, FileNotFoundException {
		ResponseEntity<ObjectNode> responseEntity = this.restTemplate.exchange(new URI(this.applicationConstantConfig.link_ConfirmAuth), HttpMethod.POST, this.confirmAuthBody.makeRequest(data), ObjectNode.class);
		log.info("getConfirmAuth :" + responseEntity.getStatusCode());
		return responseEntity;
	}
}
