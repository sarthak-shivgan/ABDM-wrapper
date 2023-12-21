package com.nha.abdm.wrapper.hrp.discoveryLinking.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.common.GatewayApi;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.repository.PatientRepo;
import com.nha.abdm.wrapper.hrp.serviceImpl.LogsTableService;
import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnConfirmRequest;
import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnDiscoverRequest;
import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnInitRequest;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.services.DiscoverLinkingService;
import com.nha.abdm.wrapper.hrp.properties.ApplicationConfig;
import com.nha.abdm.wrapper.hrp.serviceImpl.PatientTableService;
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
import java.util.ArrayList;
import java.util.List;

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
    GatewayApi gatewayApi;
    @Autowired
    PatientTableService patientTableService;
    @Autowired
    PatientRepo patientRepo;

    @Autowired
    LogsTableService<InitResponse> logsTableService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    RestTemplate restTemplate = new RestTemplate();
    public void onDiscoverCall(DiscoverResponse data) throws URISyntaxException, JsonProcessingException {
        String abhaAddress=data.getPatient().getId();
        log.info("AbhaAddress: "+abhaAddress);
        if(patientRepo.findByAbhaAddress(abhaAddress)!=null){
            String referenceNumber= patientTableService.getPatientReference(abhaAddress);
            String display= patientTableService.getPatientDisplay(abhaAddress);
            List<LinkRecordsResponse.CareContext> careContexts = patientTableService.getCareContexts(abhaAddress);
            HttpEntity<ObjectNode> requestEntity =onDiscoverRequest.makeRequest(data,abhaAddress,referenceNumber,display,careContexts);
            try {
                ResponseEntity<ObjectNode> responseEntity = restTemplate.exchange(new URI(gatewayApi.onDiscover), HttpMethod.POST, requestEntity, ObjectNode.class);
                log.info("onDiscoverCall : "+responseEntity.getStatusCode());
                logsTableService.setRequestId(data.getRequestId(),data.getPatient().getId(),requestEntity.getBody().get("requestId").asText(), data.getTransactionId(),responseEntity.getStatusCode().toString());
            }catch(Exception e){
                log.info("Error : "+e);

            }
        }else{
            try {
                HttpEntity<ObjectNode> requestEntity =onDiscoverRequest.makeRequest(data,abhaAddress,null,null,new ArrayList<>());
                ResponseEntity<ObjectNode> responseEntity = restTemplate.exchange(new URI(gatewayApi.onDiscover), HttpMethod.POST, requestEntity, ObjectNode.class);
                log.info("Discover : " +"requestId : "+data.requestId+": Patient not found");
            }catch(Exception e){
                log.info(e);

            }
        }

    }
    public void onInitCall(InitResponse data) throws URISyntaxException, JsonProcessingException {
        HttpEntity<ObjectNode> requestEntity =onInitRequest.makeRequest(data);
        try{
            ResponseEntity<ObjectNode> responseEntity = restTemplate.exchange(new URI(gatewayApi.onInit), HttpMethod.POST, requestEntity, ObjectNode.class);
            log.info("onInitCall : "+responseEntity.getStatusCode());
        }catch(Exception e){
            log.info("OnInitCall -> Error : "+e);
        }
        try{
            logsTableService.setContent(data,requestEntity, InitResponse.class);
        }catch(Exception e){
            log.info("onInitCall -> Error: unable to set content : "+e);
        }

    }
    @Override
    public void onConfirmCall(ConfirmResponse data) throws URISyntaxException, JsonProcessingException {
        String linkRefNumber=data.getConfirmation().getLinkRefNumber();
        log.info("LinkRedNumber : "+linkRefNumber);
        String abhaAddress=logsTableService.getPatientId(linkRefNumber);
        log.info("onConfirm: Abha= "+abhaAddress);
        if(abhaAddress==null){
            log.info(" OnconfirmCall -> patient with abhaAddress not found in logs :"+ abhaAddress);
        }
        String referenceNumber=patientTableService.getPatientReference(abhaAddress);
        String display=patientTableService.getPatientDisplay(abhaAddress);
        List<LinkRecordsResponse.CareContext> careContexts = logsTableService.getSelectedCareContexts(linkRefNumber,abhaAddress);
        try {
        HttpEntity<ObjectNode> requestEntity =onConfirmRequest.makeRequest(data,abhaAddress,referenceNumber,display,linkRefNumber,careContexts);
        ResponseEntity<ObjectNode> responseEntity = restTemplate.exchange(new URI(gatewayApi.onConfirm), HttpMethod.POST, requestEntity, ObjectNode.class);
        log.info("onConfirmCall : "+responseEntity.getStatusCode());}
        catch (Exception e){
            log.info("OnConfirmCall -> Error :"+ e);
        }

    }
}
