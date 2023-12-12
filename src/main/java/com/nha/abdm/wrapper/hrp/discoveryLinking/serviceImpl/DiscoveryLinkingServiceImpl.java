package com.nha.abdm.wrapper.hrp.discoveryLinking.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.serviceImpl.LogsTableService;
import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnConfirmRequest;
import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnDiscoverRequest;
import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnInitRequest;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.services.DiscoverLinkingService;
import com.nha.abdm.wrapper.hrp.properties.ApplicationConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class DiscoveryLinkingServiceImpl implements DiscoverLinkingService {
    private static final Logger log = LogManager.getLogger(DiscoverLinkingService.class);
    @Autowired
    OnDiscoverRequest onDiscoverRequest;
    @Autowired
    OnInitRequest onInitRequest;

    @Autowired
    OnConfirmRequest onConfirmRequest;

    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    LogsTableService<InitResponse> logsTableService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    RestTemplate restTemplate = new RestTemplate();
    public void onDiscoverCall(DiscoverResponse data) throws URISyntaxException {
        HttpEntity<ObjectNode> requestEntity =onDiscoverRequest.makeRequest(data);
        ResponseEntity<ObjectNode> responseEntity = restTemplate.exchange(new URI(applicationConfig.onDiscover), HttpMethod.POST, requestEntity, ObjectNode.class);
        log.info("onDiscoverCall : "+responseEntity.getStatusCode());
        logsTableService.setRequestId(data.getRequestId(),data.getPatient().getId(),requestEntity.getBody().get("requestId").asText(), data.getTransactionId(),responseEntity.getStatusCode().toString());
//        logsTableService.addResponseDump(data.getTransactionId(),objectMapper.valueToTree(data));
    }
    public void onInitCall(InitResponse data) throws URISyntaxException {
        HttpEntity<ObjectNode> requestEntity =onInitRequest.makeRequest(data);
        ResponseEntity<ObjectNode> responseEntity = restTemplate.exchange(new URI(applicationConfig.onInit), HttpMethod.POST, requestEntity, ObjectNode.class);
        log.info("onInitCall : "+responseEntity.getStatusCode());
//        logsTableService.setRequestId(data.getRequestId(),"",requestEntity.getBody().get("requestId").asText(), data.getTransactionId(),responseEntity.getStatusCode().toString());
//        logsTableService.addResponseDump(data.getTransactionId(),objectMapper.valueToTree(data));
//        log.info("In Service : "+requestEntity.getBody().path("link").get("referenceNumber").asText());
//        logsTableService.setLinkRefId(data.getTransactionId(),requestEntity.getBody().path("link").get("referenceNumber").asText());
        logsTableService.setContent(data,requestEntity, InitResponse.class);
    }
    @Override
    public void onConfirmCall(ConfirmResponse data) throws URISyntaxException {
        HttpEntity<ObjectNode> requestEntity =onConfirmRequest.makeRequest(data);
        ResponseEntity<ObjectNode> responseEntity = restTemplate.exchange(new URI(applicationConfig.onConfirm), HttpMethod.POST, requestEntity, ObjectNode.class);
        log.info("onConfirmCall : "+responseEntity.getStatusCode());

    }
}
