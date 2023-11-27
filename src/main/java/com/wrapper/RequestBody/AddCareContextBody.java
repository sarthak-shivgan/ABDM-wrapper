//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wrapper.RequestBody;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrapper.ResponseController.LinkRecordsResponse;
import com.wrapper.ResponseController.OnConfirmResponse;
import com.wrapper.ServiceImpl.CareContextTableService;
import com.wrapper.Utility.CommonUtility;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class AddCareContextBody {
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	CareContextTableService careContextTableService;
	private static final Logger log = LogManager.getLogger(AddCareContextBody.class);

	public AddCareContextBody() {
	}

	public HttpEntity<ObjectNode> makeRequest(@RequestBody OnConfirmResponse data) throws FileNotFoundException {
		log.info("Add CareContextBody: " + data.printData());
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode requestBody = nodeFactory.objectNode();
		String careContextRequestID = UUID.randomUUID().toString();
		requestBody.put("requestId", careContextRequestID);
		requestBody.put("timestamp", this.commonUtility.getCurrentTimeStamp());
		ObjectNode linkNode = nodeFactory.objectNode();
		log.info("Waiting for Transaction Id");
		String requestId = data.getResp().getRequestId();
		this.careContextTableService.setCareContextRequestId(requestId, careContextRequestID);
		log.info("RequestId in add CareContext :" + requestId);
		linkNode.put("accessToken", data.getAuth().getAccessToken());
		Logger var10000 = log;
		String var10001 = this.careContextTableService.getPatientReferenceNumber(requestId);
		var10000.info("Reference Number: " + var10001);
		var10000 = log;
		var10001 = this.careContextTableService.getPatientDisplay(requestId);
		var10000.info("Display: " + var10001);
		ObjectNode patientNode = nodeFactory.objectNode();
		patientNode.put("referenceNumber", this.careContextTableService.getPatientReferenceNumber(requestId));
		patientNode.put("display", this.careContextTableService.getPatientDisplay(requestId));
		List<LinkRecordsResponse.CareContext> careContexts = this.careContextTableService.getCareContext(requestId);
		ArrayNode careContextArray = nodeFactory.arrayNode();
		Iterator var10 = careContexts.iterator();

		while(var10.hasNext()) {
			LinkRecordsResponse.CareContext careContext = (LinkRecordsResponse.CareContext)var10.next();
			ObjectNode careContextNode = nodeFactory.objectNode();
			careContextNode.put("referenceNumber", careContext.getReferenceNumber());
			careContextNode.put("display", careContext.getDisplay());
			careContextArray.add(careContextNode);
		}

		patientNode.set("careContexts", careContextArray);
		linkNode.set("patient", patientNode);
		requestBody.set("link", linkNode);
		HttpEntity<ObjectNode> requestEntity = new HttpEntity(requestBody, this.commonUtility.getHeaders());
		log.info(requestEntity.getHeaders());
		log.info(requestEntity.getBody());
		return requestEntity;
	}
}
