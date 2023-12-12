package com.nha.abdm.wrapper.hrp.mongo.tables;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "logs")
public class LogsTable {
    @Field("clientRequestId")
    public String clientRequestId;
    @Field("gatewayRequestId")
    public String gatewayRequestId;
    @Field("response")
    public String response;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getLinkRefNumber() {
        return linkRefNumber;
    }

    public void setLinkRefNumber(String linkRefNumber) {
        this.linkRefNumber = linkRefNumber;
    }
    @Field("transactionId")
    public String transactionId;
    @Field("linkRefNumber")
    public String linkRefNumber;

    @Field("abhaAddress")
    public String abhaAddress;

    public String getAbhaAddress() {
        return abhaAddress;
    }

    public void setAbhaAddress(String abhaAddress) {
        this.abhaAddress = abhaAddress;
    }

    public void setResponseDump(Map<String, Map<String, Object>> responseDump) {
        this.responseDump = responseDump;
    }

    public String getClientRequestId() {
        return clientRequestId;
    }

    public void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
    }

    public String getGatewayRequestId() {
        return gatewayRequestId;
    }

    public void setGatewayRequestId(String gatewayRequestId) {
        gatewayRequestId = gatewayRequestId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Map<String, Map<String, Object>> getResponseDump() {
        return responseDump;
    }

//    public void setResponseDump(HashMap<String,Object> responseDump) {
//        this.responseDump.putAll(responseDump);
//    }

    @Field("responseDump")
    public Map<String, Map<String, Object>> responseDump;
    public LogsTable(String clientRequestId,String gatewayRequestId,String abhaAddress,String transactionId,String response){
        this.clientRequestId=clientRequestId;
        this.gatewayRequestId=gatewayRequestId;
        this.abhaAddress=abhaAddress;
        this.transactionId=transactionId;
        this.response=response;
        this.responseDump=new HashMap<>();
    }

}
