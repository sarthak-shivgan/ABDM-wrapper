package com.nha.abdm.wrapper.hrp.discoveryLinking.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.common.CareContextBuilder;
import com.nha.abdm.wrapper.hrp.common.SessionManager;
import com.nha.abdm.wrapper.hrp.common.Utils;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.*;

@Builder
@Data
public class OnDiscoverRequest {
    private SessionManager sessionManager;

    private DiscoverResponse data;
    private String abhaAddress;
    private String referenceNumber;
    private String display;
    private List<CareContextBuilder> careContexts;

    public static final Logger log = LogManager.getLogger(OnDiscoverRequest.class);

    public HttpEntity<ObjectNode> makeRequest() throws URISyntaxException, JsonProcessingException {

        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode requestBody = nodeFactory.objectNode();
        String requestId = UUID.randomUUID().toString();
        requestBody.put("requestId", requestId);
        requestBody.put("timestamp", Utils.getCurrentTimeStamp().toString());
        requestBody.put("transactionId",data.getTransactionId());
        log.info("InBody of onDiscover patientReference : "+referenceNumber);
        if(referenceNumber!=null){
            ObjectNode patientNode = nodeFactory.objectNode();
            patientNode.put("referenceNumber",referenceNumber);
            patientNode.put("display", display);
            ArrayNode careContextArray = nodeFactory.arrayNode();
            Iterator list = careContexts.iterator();
            while(list.hasNext()) {
                CareContextBuilder careContext = (CareContextBuilder) list.next();
                ObjectNode careContextNode = nodeFactory.objectNode();
                careContextNode.put("referenceNumber", careContext.getReferenceNumber());
                careContextNode.put("display", careContext.getDisplay());
                careContextArray.add(careContextNode);
            }
            patientNode.set("careContexts", careContextArray);
            ArrayNode matchedBy = nodeFactory.arrayNode();
            matchedBy.add("MOBILE");
            patientNode.put("matchedBy",matchedBy);
            requestBody.put("patient",patientNode);
        }else{
            ObjectNode errorNode = nodeFactory.objectNode();
            errorNode.put("code",1000);
            errorNode.put("message","Patient not found");
            requestBody.put("error",errorNode);
        }

        ObjectNode respNode = nodeFactory.objectNode();
        respNode.put("requestId",data.getRequestId());
        requestBody.put("resp",respNode);

        HttpEntity<ObjectNode> requestEntity = new HttpEntity(requestBody, sessionManager.initialiseHeadersForGateway());
        log.info(requestEntity.getHeaders().toString());
        log.info(((ObjectNode)requestEntity.getBody()).toPrettyString());
        return requestEntity;
    }
}
