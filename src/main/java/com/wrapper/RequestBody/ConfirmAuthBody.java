//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.RequestBody;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.ResponseController.OnInitResponse;
import com.wrapper.ServiceImpl.CareContextTableService;
import com.wrapper.Utility.CommonUtility;
import java.io.FileNotFoundException;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

@Component
public class ConfirmAuthBody {
	private static final Logger log = LogManager.getLogger(ConfirmAuthBody.class);
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	CareContextTableService careContextTableService;

	public ConfirmAuthBody() {
	}

	public HttpEntity<ObjectNode> makeRequest(OnInitResponse data) throws FileNotFoundException {
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode requestBody = nodeFactory.objectNode();
		String confirmRequestId = UUID.randomUUID().toString();
		requestBody.put("requestId", confirmRequestId);
		requestBody.put("timestamp", this.commonUtility.getCurrentTimeStamp());
		String initRequestId = data.getResp().getRequestId();
		log.info("initRequestID: " + initRequestId + " confirmRequestId: " + confirmRequestId);
		this.careContextTableService.setConfirmRequestId(initRequestId, confirmRequestId);
		requestBody.put("transactionId", data.getAuth().getTransactionId());
		this.careContextTableService.setTransactionId(data);
		log.info("Started Confirm auth after storing transactionId");
		ObjectNode credentialNode = nodeFactory.objectNode();
		if (data.getAuth().getMode().equals("DEMOGRAPHICS")) {
			ObjectNode demographicNode = nodeFactory.objectNode();
			demographicNode.put("name", this.careContextTableService.getName(initRequestId));
			demographicNode.put("gender", this.careContextTableService.getGender(initRequestId));
			demographicNode.put("dateOfBirth", this.careContextTableService.getDateOfBirth(initRequestId));
			credentialNode.set("demographic", demographicNode);
		}

		requestBody.set("credential", credentialNode);
		log.info("ConfirmBody : " + requestBody.toPrettyString());
		HttpEntity<ObjectNode> requestEntity = new HttpEntity(requestBody, this.commonUtility.getHeaders());
		return requestEntity;
	}
}
