package com.nha.abdm.wrapper.hrp.discoveryLinking.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.nha.abdm.wrapper.hrp.CareContextService;
import com.nha.abdm.wrapper.hrp.serviceImpl.LogsTableService;
import com.nha.abdm.wrapper.hrp.serviceImpl.PatientTableService;
import com.nha.abdm.wrapper.hrp.common.Utils;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component
public class OnConfirmRequest {
    @Autowired
    Utils utils;
    private static final Logger log = LogManager.getLogger(OnConfirmRequest.class);

    public HttpEntity<ObjectNode> makeRequest(ConfirmResponse data,String abhaAddress,String referenceNumber,String display,String linkRefNumber,List<LinkRecordsResponse.CareContext> careContexts) throws URISyntaxException, JsonProcessingException {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode requestBody = nodeFactory.objectNode();
        String requestId = UUID.randomUUID().toString();
        requestBody.put("requestId", requestId);
        requestBody.put("timestamp", utils.getCurrentTimeStamp().toString());
        String tokenNumber=data.getConfirmation().getToken();
        log.info("Token Number :"+tokenNumber);
        if(tokenNumber.equals("123456")){
            ObjectNode patientNode = nodeFactory.objectNode();
            log.info("linkRefNumber :"+linkRefNumber);
            log.info("onConfirm: Abha= "+abhaAddress);
            patientNode.put("referenceNumber", referenceNumber);
            patientNode.put("display", display);
            ArrayNode careContextArray = nodeFactory.arrayNode();
            Iterator list = careContexts.iterator();
            while(list.hasNext()) {
                LinkRecordsResponse.CareContext careContext = (LinkRecordsResponse.CareContext)list.next();
                ObjectNode careContextNode = nodeFactory.objectNode();
                careContextNode.put("referenceNumber", careContext.getReferenceNumber());
                careContextNode.put("display", careContext.getDisplay());
                careContextArray.add(careContextNode);
            }
            patientNode.set("careContexts", careContextArray);
            requestBody.put("patient",patientNode);
            requestBody.putNull("error");
        }else if(tokenNumber!="123456"){
            requestBody.put("error","Incorrect Otp");
        }
        ObjectNode respNode = nodeFactory.objectNode();
        respNode.put("requestId",data.getRequestId());
        requestBody.put("resp",respNode);
        log.info(requestBody.toPrettyString());
        HttpEntity<ObjectNode> requestEntity = new HttpEntity(requestBody, this.utils.initialiseHeadersForGateway());
        return requestEntity;
    }
}
