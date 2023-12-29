package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.common.CareContextBuilder;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
//import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
//import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.OnAddCareContextResponse;
//import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.OnInitResponse;
import com.nha.abdm.wrapper.hrp.mongo.tables.RequestLogs;
import com.nha.abdm.wrapper.hrp.repository.LogsRepo;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        RequestLogs existingRecord = mongoTemplate.findOne(query, RequestLogs.class);
        if (existingRecord == null) {
            RequestLogs newRecord = new RequestLogs(requestId, gatewayRequestId, abhaAddress, transactionId, statusCode);
            mongoTemplate.insert(newRecord);
        } else {
            Update update = (new Update()).set("requestId", requestId)
                    .set("gatewayRequestId", gatewayRequestId);
            mongoTemplate.updateFirst(query, update, RequestLogs.class);
        }

    }
    @Transactional
    public void addResponseDump(String transactionId, ObjectNode dump) {
        Query query = new Query(Criteria.where("transactionId").is(transactionId));
        Update update = (new Update()).addToSet("rawResponse", dump);
        mongoTemplate.updateFirst(query, update, RequestLogs.class);
    }

    public String getPatientId(String linkRefNumber) {
        RequestLogs existingRecord=logsRepo.findByLinkRefNumber(linkRefNumber);
        InitResponse data=(InitResponse) existingRecord.getRawResponse().get("InitResponse");
        return data.getPatient().getId();
    }

    @Transactional
    public void setLinkRefId(String transactionId, String referenceNumber) {
        Query query = new Query(Criteria.where("transactionId").is(transactionId));
        Update update = (new Update()).set("linkRefNumber", referenceNumber);
        this.mongoTemplate.updateFirst(query, update, RequestLogs.class);
    }

    public void setContent(T content, HttpEntity<ObjectNode> requestEntity,Class<T> contentType) {
        if(contentType == InitResponse.class){
            InitResponse data=(InitResponse) content;
            RequestLogs newRecord=new RequestLogs();
            newRecord.setClientRequestId(data.getRequestId());
            newRecord.setGatewayRequestId(requestEntity.getBody().get("requestId").asText());
            newRecord.setLinkRefNumber(requestEntity.getBody().path("link").get("referenceNumber").asText());
            HashMap<String,Object> map=new HashMap<>();
            map.put("InitResponse",data);
            newRecord.setRawResponse(map);
            mongoTemplate.save(newRecord);
        }
    }

    public List<CareContextBuilder> getSelectedCareContexts(String linkRefNumber, String abhaAddress) {
        RequestLogs existingRecord=logsRepo.findByLinkRefNumber(linkRefNumber);
        log.info("linkRefNum in getSelectedContexts : "+linkRefNumber);
        if (existingRecord != null) {
            ObjectNode dump =objectMapper.convertValue(existingRecord.getRawResponse().get("InitResponse"), ObjectNode.class);
            if (dump != null && dump.has("patient") && dump.get("patient").has("careContexts")) {
                ArrayNode careContexts = (ArrayNode)dump.path("patient").path("careContexts");
                List<String> selectedList = careContexts.findValuesAsText("referenceNumber");
                List<CareContextBuilder> selectedCareContexts = patientTableService.getCareContexts(abhaAddress).stream()
                        .filter(careContext -> selectedList.contains(careContext.getReferenceNumber()))
                        .collect(Collectors.toList());
                log.info("Dump: {}", dump);
                log.info("Selected List: {}", selectedList);

                return selectedCareContexts;
            }
        }

        return null;
    }
    public String getStatus(JsonNode data) {
        RequestLogs existingRecord=logsRepo.findByClientRequestId(data.get("requestId").asText());
        if(existingRecord!=null){
            return existingRecord.getResponse().toString();
        }
        return "Record failed but stored in database";
    }
}
