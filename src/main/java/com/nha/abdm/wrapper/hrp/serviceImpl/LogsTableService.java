package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.mongo.tables.LogsTable;
import com.nha.abdm.wrapper.hrp.repository.LogsRepo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
    public void setRequestId(String clientRequestId, String abhaAddress, String gatewayRequestId, String transactionId, String statusCode) {
        Query query = new Query(Criteria.where("clientRequestId").is(clientRequestId));
        LogsTable existingRecord = mongoTemplate.findOne(query, LogsTable.class);
        if (existingRecord == null) {
            LogsTable newRecord = new LogsTable(clientRequestId, gatewayRequestId, abhaAddress, transactionId, statusCode);
            mongoTemplate.insert(newRecord);
        } else {
            Update update = (new Update()).set("clientRequestId", clientRequestId).set("gatewayRequestId", gatewayRequestId);
            mongoTemplate.updateFirst(query, update, LogsTable.class);
        }

    }

    @Transactional
    public void addResponseDump(String transactionId, ObjectNode dump) {
        Query query = new Query(Criteria.where("transactionId").is(transactionId));
        Update update = (new Update()).addToSet("responseDump", dump);
        mongoTemplate.updateFirst(query, update, LogsTable.class);
    }

    public String getPatientId(String linkRefNumber) {
        Query query = new Query(Criteria.where("linkRefNumber").is(linkRefNumber));
        query.fields().include("abhaAddress");
        LogsTable logsTable = mongoTemplate.findOne(query, LogsTable.class);
        return logsTable != null ? logsTable.getAbhaAddress() : null;
    }

    @Transactional
    public void setLinkRefId(String transactionId, String referenceNumber) {
        Query query = new Query(Criteria.where("transactionId").is(transactionId));
        Update update = (new Update()).set("linkRefNumber", referenceNumber);
        this.mongoTemplate.updateFirst(query, update, LogsTable.class);
    }

    public void setContent(T content, HttpEntity<ObjectNode> requestEntity,Class<T> contentType) {
        if(contentType == InitResponse.class){
            InitResponse data=(InitResponse) content;
            Query query = new Query(Criteria.where("transactionId").is(data.getTransactionId()));
            LogsTable existingRecord = mongoTemplate.findOne(query, LogsTable.class);
            if (existingRecord != null) {
                Update update = (new Update()).set("linkRefNumber", requestEntity.getBody().get("linkRefNumber"))
                        .set("responseDump." + data.getClass().getSimpleName(), data)
                        .set("clientRequestId", data.getRequestId()).set("linkRefNumber",requestEntity.getBody().path("link").get("referenceNumber").asText())
                        .set("gatewayRequestId", requestEntity.getBody().get("requestId").asText());
                mongoTemplate.updateFirst(query, update, LogsTable.class);
            }
        }else if(contentType== LinkRecordsResponse.class){
            LinkRecordsResponse data=(LinkRecordsResponse) content;
            Query query = new Query(Criteria.where("clientRequestId").is(data.getRequestId()));
            LogsTable existingRecord = mongoTemplate.findOne(query, LogsTable.class);
            if (existingRecord != null) {
                Update update = (new Update()).set("linkRefNumber", requestEntity.getBody().get("linkRefNumber"))
                        .set("responseDump." + data.getClass().getSimpleName(), data)
                        .set("clientRequestId", data.getRequestId()).set("linkRefNumber",requestEntity.getBody().path("link").get("referenceNumber").asText())
                        .set("gatewayRequestId", requestEntity.getBody().get("requestId").asText());
                mongoTemplate.updateFirst(query, update, LogsTable.class);
            }

        }


    }

    public List<LinkRecordsResponse.CareContext> getSelectedCareContexts(String linkRefNumber) {
        Query query = new Query(Criteria.where("linkRefNumber").is(linkRefNumber));
        LogsTable existingRecord = mongoTemplate.findOne(query, LogsTable.class);
        if (existingRecord != null) {
            ObjectNode dump =objectMapper.convertValue(existingRecord.getResponseDump().get("InitResponse"), ObjectNode.class);
            if (dump != null && dump.has("patient") && dump.get("patient").has("careContexts")) {
                ArrayNode careContexts = (ArrayNode)dump.path("patient").path("careContexts");
                ArrayList<String> selectedList = new ArrayList();
                Iterator list = careContexts.iterator();
                while(list.hasNext()) {
                    JsonNode careContext = (JsonNode)list.next();
                    selectedList.add(careContext.path("referenceNumber").asText());
                }

                List<LinkRecordsResponse.CareContext> patientCareContext = this.patientTableService.getCareContexts(existingRecord.getAbhaAddress());
                List<LinkRecordsResponse.CareContext> selectedCareContexts = selectedList.stream()
                        .map(referenceNumber -> patientCareContext.stream()
                                .filter(careContext -> referenceNumber.equals(careContext.getReferenceNumber()))
                                .findFirst()
                                .orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                return selectedCareContexts;
            }
        }

        return null;
    }
}
