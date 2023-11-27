//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.RequestBody;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.Controller.HRP.HipGateway;
import com.wrapper.ResponseController.LinkRecordsResponse;
import com.wrapper.ServiceImpl.CareContextTableService;
import com.wrapper.Utility.CommonUtility;
import java.util.Objects;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthInitBody {
	private static final Logger log = LogManager.getLogger(AuthInitBody.class);
	public String requestId;
	String timestamp;
	String id;
	String purpose;
	String authMode;
	String requesterType = "HIP";
	String requesterId;
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	HipGateway hipGateway;
	@Autowired
	CareContextTableService careContextTableService;

	public AuthInitBody() {
	}

	public HttpEntity<ObjectNode> makeRequest(LinkRecordsResponse data) {
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode requestBody = nodeFactory.objectNode();
		this.requestId = UUID.randomUUID().toString();
		requestBody.put("requestId", this.requestId);
		this.careContextTableService.setRequestId(this.requestId, data.getName(), data.getGender(), data.getDateOfBirth(), data);
		requestBody.put("timestamp", this.commonUtility.getCurrentTimeStamp());
		ObjectNode queryNode = nodeFactory.objectNode();
		queryNode.put("id", data.getAbhaAddress());
		queryNode.put("purpose", "KYC_AND_LINK");
		String authMode = data.getAuthMode();
		if (Objects.equals(authMode, "DEMOGRAPHICS")) {
			this.careContextTableService.setRequestId(this.requestId, data.getName(), data.getGender(), data.getDateOfBirth(), data);
		} else {
			this.careContextTableService.setRequestId(this.requestId, "", "", "", data);
		}

		queryNode.put("authMode", authMode);
		ObjectNode requesterNode = nodeFactory.objectNode();
		requesterNode.put("type", "HIP");
		requesterNode.put("id", data.getRequesterId());
		queryNode.set("requester", requesterNode);
		requestBody.set("query", queryNode);
		HttpEntity<ObjectNode> requestEntity = new HttpEntity(requestBody, this.commonUtility.getHeaders());
		log.info(requestEntity.getHeaders().toString());
		log.info(((ObjectNode)requestEntity.getBody()).toPrettyString());
		return requestEntity;
	}
}
