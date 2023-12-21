package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoCollection;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.OnAddCareContextResponse;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.OnInitResponse;
import com.nha.abdm.wrapper.hrp.mongo.tables.Logs;
import com.nha.abdm.wrapper.hrp.repository.LogsRepo;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogsTableService<T> {
    @Autowired
    public LogsRepo logsRepo;
    @Autowired
    MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    PatientTableService patientTableService;
    private static final Logger log = LogManager.getLogger(LogsTableService.class);


    @Transactional
    public void setRequestId(String requestId, String abhaAddress, String gatewayRequestId, String transactionId, String statusCode) {
        Query query = new Query(Criteria.where("clientRequestId").is(requestId));
        Logs existingRecord = mongoTemplate.findOne(query, Logs.class);
        if (existingRecord == null) {
            Logs newRecord = new Logs(requestId, gatewayRequestId, abhaAddress, transactionId, statusCode);
            mongoTemplate.insert(newRecord);
        } else {
            Update update = (new Update()).set("requestId", requestId)
                    .set("gatewayRequestId", gatewayRequestId);
            mongoTemplate.updateFirst(query, update, Logs.class);
        }

    }

    @Transactional
    public void addResponseDump(String transactionId, ObjectNode dump) {
        Query query = new Query(Criteria.where("transactionId").is(transactionId));
        Update update = (new Update()).addToSet("responseDump", dump);
        mongoTemplate.updateFirst(query, update, Logs.class);
    }

    public String getPatientId(String linkRefNumber) {
//        Query query = new Query(Criteria.where("linkRefNumber").is(linkRefNumber));
//        query.fields().include("abhaAddress");
//        Logs logs = mongoTemplate.findOne(query, Logs.class);

        Logs existingRecord=logsRepo.findByLinkRefNumber(linkRefNumber);
        InitResponse data=(InitResponse) existingRecord.getResponseDump().get("InitResponse");
        return data.getPatient().getId();
    }

    @Transactional
    public void setLinkRefId(String transactionId, String referenceNumber) {
        Query query = new Query(Criteria.where("transactionId").is(transactionId));
        Update update = (new Update()).set("linkRefNumber", referenceNumber);
        this.mongoTemplate.updateFirst(query, update, Logs.class);
    }

    public void setContent(T content, HttpEntity<ObjectNode> requestEntity,Class<T> contentType) {
        if(contentType == InitResponse.class){
            InitResponse data=(InitResponse) content;
            Logs newRecord=new Logs();
            newRecord.setClientRequestId(data.getRequestId());
            newRecord.setGatewayRequestId(requestEntity.getBody().get("requestId").asText());
            newRecord.setLinkRefNumber(requestEntity.getBody().path("link").get("referenceNumber").asText());
            HashMap<String,Object> map=new HashMap<>();
            map.put("InitResponse",data);
            newRecord.setResponseDump(map);
//                logsRepo.save(newRecord);
            mongoTemplate.save(newRecord);
//            Query query = new Query(Criteria.where("clientRequestId").is(data.getRequestId()));
//            Logs existingRecord = mongoTemplate.findOne(query, Logs.class);
//            if (existingRecord != null) {
//                Update update = (new Update()).set("linkRefNumber", requestEntity.getBody().get("linkRefNumber"))
//                        .push("responseDump."+"InitResponse",data)
//                        .set("clientRequestId", data.getRequestId()).set("linkRefNumber",requestEntity.getBody().path("link").get("referenceNumber").asText())
//                        .set("gatewayRequestId", requestEntity.getBody().get("requestId").asText());
//                mongoTemplate.updateFirst(query, update, Logs.class);
//            }
        }if(contentType == LinkRecordsResponse.class){
            LinkRecordsResponse data=(LinkRecordsResponse) content;
                Logs newRecord=new Logs();
                newRecord.setRequestId(data.getRequestId());
                newRecord.setClientRequestId(data.getRequestId());
                newRecord.setGatewayRequestId(requestEntity.getBody().get("requestId").asText());
                HashMap<String,Object> map=new HashMap<>();
                map.put("LinkRecordsResponse",data);
                newRecord.setResponseDump(map);
//                logsRepo.save(newRecord);
                mongoTemplate.save(newRecord);

        }if(contentType == Object.class){
            log.info("Inside setContent Method");
            LinkRecordsResponse data=(LinkRecordsResponse) content;
            Query query = new Query(Criteria.where("clientRequestId").is(data.getRequestId()));
            Logs existingRecord = mongoTemplate.findOne(query, Logs.class);
            if (existingRecord == null) {
                Logs newRecord=new Logs();
                newRecord.setClientRequestId(data.getRequestId());
                newRecord.setResponse(requestEntity.getBody().get("Error").asText());
                newRecord.setResponseDump((HashMap<String, Object>) new HashMap<>().put("LinkRecordsResponse",data));
                logsRepo.save(newRecord);
            }
        }if(contentType == OnInitResponse.class){
            OnInitResponse data=(OnInitResponse) content;
            Query query = new Query(Criteria.where("requestId").is(data.getResp().getRequestId()));
            Logs existingRecord = mongoTemplate.findOne(query, Logs.class);
            if (existingRecord != null) {
                Update update = (new Update())
                        .push("responseDump."+"OnInitResponse",data)
                        .set("requestId", data.getRequestId())
                        .set("gatewayRequestId", requestEntity.getBody().get("requestId").asText());
                mongoTemplate.updateFirst(query, update, Logs.class);
            }
        }


    }

    public List<LinkRecordsResponse.CareContext> getSelectedCareContexts(String linkRefNumber,String abhaAddress) {
//        Query query = new Query(Criteria.where("linkRefNumber").is(linkRefNumber));
//        Logs existingRecord = mongoTemplate.findOne(query, Logs.class);
        Logs existingRecord=logsRepo.findByLinkRefNumber(linkRefNumber);
        log.info("linkRefNum in getSelectedContexts : "+linkRefNumber);
        if (existingRecord != null) {
            ObjectNode dump =objectMapper.convertValue(existingRecord.getResponseDump().get("InitResponse"), ObjectNode.class);
            if (dump != null && dump.has("patient") && dump.get("patient").has("careContexts")) {
                ArrayNode careContexts = (ArrayNode)dump.path("patient").path("careContexts");
//                ArrayList<String> selectedList = new ArrayList();
//                Iterator list = careContexts.iterator();
//                while(list.hasNext()) {
//                    JsonNode careContext = (JsonNode)list.next();
//                    selectedList.add(careContext.path("referenceNumber").asText());
//                }
                List<String> selectedList = careContexts.findValuesAsText("referenceNumber");
                List<LinkRecordsResponse.CareContext> selectedCareContexts = patientTableService.getCareContexts(abhaAddress).stream()
                        .filter(careContext -> selectedList.contains(careContext.getReferenceNumber()))
                        .collect(Collectors.toList());
//                List<LinkRecordsResponse.CareContext> patientCareContext = this.patientTableService.getCareContexts(existingRecord.getAbhaAddress());
//                List<LinkRecordsResponse.CareContext> selectedCareContexts = selectedList.stream()
//                        .map(referenceNumber -> patientCareContext.stream()
//                                .filter(careContext -> referenceNumber.equals(careContext.getReferenceNumber()))
//                                .findFirst()
//                                .orElse(null))
//                        .filter(Objects::nonNull)
//                        .collect(Collectors.toList());
                log.info("Dump: {}", dump);
                log.info("Selected List: {}", selectedList);

                return selectedCareContexts;
            }
        }

        return null;
    }

    public void setStatus(OnAddCareContextResponse data) {
        Logs existingRecord=logsRepo.findByRequestId(data.getResp().getRequestId());
        if(existingRecord!=null){
            Query query = new Query(Criteria.where("requestId").is(data.getResp().getRequestId()));
            Update update = (new Update()).set("response", data.getAcknowledgement().getStatus());
            mongoTemplate.updateFirst(query, update, Logs.class);
        }

    }
    public String getStatus(JsonNode data) {
        Logs existingRecord=logsRepo.findByClientRequestId(data.get("requestId").asText());
        if(existingRecord!=null){
            return existingRecord.getResponse().toString();
        }
        return "Record failed but stored in database";
    }
}
