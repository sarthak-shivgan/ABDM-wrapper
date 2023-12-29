package com.nha.abdm.wrapper.hrp.discoveryLinking.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.common.CareContextBuilder;
import com.nha.abdm.wrapper.hrp.common.GatewayApiPaths;
//import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
//import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.common.SessionManager;
import com.nha.abdm.wrapper.hrp.common.Utils;
import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnConfirmRequest;
import com.nha.abdm.wrapper.hrp.repository.PatientRepo;
import com.nha.abdm.wrapper.hrp.serviceImpl.LogsTableService;
//import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnConfirmRequest;
import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnDiscoverRequest;
import com.nha.abdm.wrapper.hrp.discoveryLinking.requests.OnInitRequest;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.ConfirmResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.services.DiscoverLinkingService;
import com.nha.abdm.wrapper.hrp.properties.ApplicationConfig;
import com.nha.abdm.wrapper.hrp.serviceImpl.PatientTableService;
import lombok.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.lang.String;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.String;

@Service
public class DiscoveryLinkingServiceImpl implements DiscoverLinkingService {
    private static final Logger log = LogManager.getLogger(DiscoverLinkingService.class);
    @Autowired
    SessionManager sessionManager;
    @Autowired
    PatientTableService patientTableService;
    @Autowired
    PatientRepo patientRepo;
    @Autowired
    Utils utils;

    @Autowired
    LogsTableService<InitResponse> logsTableService;


    public void onDiscoverCall(DiscoverResponse data) throws URISyntaxException, JsonProcessingException {
        String abhaAddress = data.getPatient().getId();
        log.info("AbhaAddress: " + abhaAddress);
        if (patientRepo.findByAbhaAddress(abhaAddress) != null) {
            String referenceNumber = patientTableService.getPatientReference(abhaAddress);
            String display = patientTableService.getPatientDisplay(abhaAddress);
            List<CareContextBuilder> careContexts = patientTableService.getCareContexts(abhaAddress);
            HttpEntity<ObjectNode> requestEntity = OnDiscoverRequest.builder()
                    .data(data)
                    .abhaAddress(abhaAddress)
                    .referenceNumber(referenceNumber)
                    .display(display)
                    .careContexts(careContexts)
                    .sessionManager(sessionManager)
                    .build()
                    .makeRequest();
            try {
                WebClient.Builder webClientBuilder = WebClient.builder();
                ResponseEntity<ObjectNode> responseEntity = webClientBuilder
                        .build()
                        .post()
                        .uri(GatewayApiPaths.ON_DISCOVER)
                        .headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
                        .body(BodyInserters.fromValue(requestEntity.getBody()))
                        .retrieve()
                        .toEntity(ObjectNode.class)
                        .block();
                log.info(GatewayApiPaths.ON_DISCOVER+" : onDiscoverCall: " + responseEntity.getStatusCode());
                logsTableService.setRequestId(
                        data.getRequestId(),
                        data.getPatient().getId(),
                        requestEntity.getBody().get("requestId").asText(),
                        data.getTransactionId(),
                        responseEntity.getStatusCode().toString()
                );
            } catch (Exception e) {
                log.info("Error: " + e);
            }
        } else {
            try {
                HttpEntity<ObjectNode> requestEntity = OnDiscoverRequest.builder()
                        .data(data)
                        .abhaAddress(abhaAddress)
                        .referenceNumber(null)
                        .display(null)
                        .careContexts(Collections.emptyList())
                        .sessionManager(sessionManager)
                        .build()
                        .makeRequest();
                WebClient.Builder webClientBuilder = WebClient.builder();
                ResponseEntity<ObjectNode> responseEntity = webClientBuilder
                        .build()
                        .post()
                        .uri(GatewayApiPaths.ON_DISCOVER)
                        .headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
                        .body(BodyInserters.fromValue(requestEntity.getBody()))
                        .retrieve()
                        .toEntity(ObjectNode.class)
                        .block();

                log.info("Discover: requestId : " + data.getRequestId() + ": Patient not found");
            } catch (Exception e) {
                log.info(e);
            }
        }
    }
    public void onInitCall(InitResponse data) throws URISyntaxException, JsonProcessingException {

        HttpEntity<ObjectNode> requestEntity = OnInitRequest.builder()
                .data(data)
                .sessionManager(sessionManager)
                .build().makeRequest();
        try{
            WebClient.Builder webClientBuilder = WebClient.builder();
            ResponseEntity<ObjectNode> responseEntity = webClientBuilder
                    .build()
                    .post()
                    .uri(GatewayApiPaths.ON_INIT)
                    .headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
                    .body(BodyInserters.fromValue(requestEntity.getBody()))
                    .retrieve()
                    .toEntity(ObjectNode.class)
                    .block();
            log.info(GatewayApiPaths.ON_INIT+" : onInitCall: " + responseEntity.getStatusCode());
        }catch(Exception e){
            log.info(GatewayApiPaths.ON_INIT+" : OnInitCall -> Error : "+e);
        }
        try{
            logsTableService.setContent(data,requestEntity, InitResponse.class);
        }catch(Exception e){
            log.info("onInitCall -> Error: unable to set content : "+e);
        }
    }

    public void onConfirmCall(ConfirmResponse data) throws URISyntaxException, JsonProcessingException {
        String linkRefNumber=data.getConfirmation().getLinkRefNumber();
        log.info("LinkRedNumber : "+linkRefNumber);
        String abhaAddress=logsTableService.getPatientId(linkRefNumber);
        log.info("onConfirm: Abha= "+abhaAddress);
        if(abhaAddress==null){
            log.info("OnconfirmCall -> patient with abhaAddress not found in logs :"+ abhaAddress);
        }
        String referenceNumber=patientTableService.getPatientReference(abhaAddress);
        String display=patientTableService.getPatientDisplay(abhaAddress);
        List<CareContextBuilder> careContexts = logsTableService.getSelectedCareContexts(linkRefNumber,abhaAddress);
        try {
        HttpEntity<ObjectNode> requestEntity = OnConfirmRequest.builder()
                .data(data)
                .abhaAddress(abhaAddress)
                .referenceNumber(referenceNumber)
                .display(display)
                .careContexts(careContexts)
                .linkRefNumber(linkRefNumber)
                .sessionManager(sessionManager)
                .build()
                .makeRequest();

            WebClient.Builder webClientBuilder = WebClient.builder();
            ResponseEntity<ObjectNode> responseEntity = webClientBuilder
                    .build()
                    .post()
                    .uri(GatewayApiPaths.ON_CONFIRM)
                    .headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()))
                    .body(BodyInserters.fromValue(requestEntity.getBody()))
                    .retrieve()
                    .toEntity(ObjectNode.class)
                    .block();
        log.info(GatewayApiPaths.ON_CONFIRM+" : onConfirmCall: " + responseEntity.getStatusCode());}
        catch (Exception e){
            log.info(GatewayApiPaths.ON_CONFIRM+" : OnConfirmCall -> Error :"+ e);
        }
    }
}