/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.common.models.CareContext;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.RequestLog;
import com.nha.abdm.wrapper.hip.hrp.discover.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.requests.LinkAddCareContext;
import com.nha.abdm.wrapper.hip.hrp.hipLink.requests.LinkConfirm;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnAddCareContextsResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hip.hrp.link.responses.InitResponse;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.Exceptions;

@Service
public class RequestLogService<T> {
  @Autowired public LogsRepo logsRepo;
  @Autowired MongoTemplate mongoTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired PatientService patientService;
  private static final Logger log = LogManager.getLogger(RequestLogService.class);

  public String getPatientId(String linkRefNumber) {
    RequestLog existingRecord = logsRepo.findByLinkRefNumber(linkRefNumber);
    InitResponse data = (InitResponse) existingRecord.getRawResponse().get("InitResponse");
    return data.getPatient().getId();
  }

  public String getPatientReference(String linkRefNumber) {
    RequestLog existingRecord = logsRepo.findByLinkRefNumber(linkRefNumber);
    InitResponse data = (InitResponse) existingRecord.getRawResponse().get("InitResponse");
    return data.getPatient().getReferenceNumber();
  }

  @Transactional
  public void setLinkRefId(String transactionId, String referenceNumber) {
    Query query = new Query(Criteria.where("transactionId").is(transactionId));
    Update update = (new Update()).set("linkRefNumber", referenceNumber);
    this.mongoTemplate.updateFirst(query, update, RequestLog.class);
  }

  @Transactional
  public void setDiscoverResponse(DiscoverResponse discoverResponse) {
    if (Objects.isNull(discoverResponse)) {
      return;
    }
    RequestLog newRecord = new RequestLog();
    newRecord.setClientRequestId(discoverResponse.getRequestId());
    newRecord.setTransactionId(discoverResponse.getTransactionId());
    HashMap<String, Object> map = new HashMap<>();
    map.put("DiscoverResponse", discoverResponse);
    newRecord.setRawResponse(map);
    mongoTemplate.save(newRecord);
  }

  @Transactional
  public void setHipLinkResponse(LinkRecordsResponse linkRecordsResponse) {
    if (Objects.isNull(linkRecordsResponse)) {
      return;
    }
    RequestLog newRecord = new RequestLog();
    newRecord.setClientRequestId(linkRecordsResponse.getRequestId());
    newRecord.setGatewayRequestId(linkRecordsResponse.getRequestId());
    HashMap<String, Object> map = new HashMap<>();
    map.put("LinkRecordsResponse", linkRecordsResponse);
    newRecord.setRawResponse(map);
    mongoTemplate.save(newRecord);
  }

  @Transactional
  public void setLinkResponse(InitResponse initResponse, String requestId, String referenceNumber) {
    if (Objects.isNull(initResponse)) {
      return;
    }

    Query query = new Query(Criteria.where("transactionId").is(initResponse.getTransactionId()));
    RequestLog existingRecord = mongoTemplate.findOne(query, RequestLog.class);
    if (existingRecord == null) {
      RequestLog newRecord =
          new RequestLog(
              initResponse.getRequestId(),
              requestId,
              initResponse.getPatient().getId(),
              initResponse.getTransactionId());
      mongoTemplate.insert(newRecord);
    } else {
      Map<String, Object> map = existingRecord.getRawResponse();
      map.put("InitResponse", initResponse);
      Update update =
          (new Update())
              .set("clientRequestId", initResponse.getRequestId())
              .set("gatewayRequestId", requestId)
              .set("linkRefNumber", referenceNumber)
              .set("rawResponse", map);
      mongoTemplate.updateFirst(query, update, RequestLog.class);
    }
  }

  public List<CareContext> getSelectedCareContexts(
      String linkRefNumber, List<CareContext> careContextsList) {
    RequestLog existingRecord = logsRepo.findByLinkRefNumber(linkRefNumber);
    log.info("linkRefNum in getSelectedContexts : " + linkRefNumber);
    if (existingRecord != null) {
      ObjectNode dump =
          objectMapper.convertValue(
              existingRecord.getRawResponse().get("InitResponse"), ObjectNode.class);
      if (dump != null && dump.has("patient") && dump.get("patient").has("careContexts")) {
        ArrayNode careContexts = (ArrayNode) dump.path("patient").path("careContexts");
        List<String> selectedList = careContexts.findValuesAsText("referenceNumber");

        List<CareContext> selectedCareContexts =
            careContextsList.stream()
                .filter(careContext -> selectedList.contains(careContext.getReferenceNumber()))
                .collect(Collectors.toList());
        log.info("Dump: {}", dump);
        log.info("Selected List: {}", selectedList);

        return selectedCareContexts;
      }
    }
    return null;
  }

  public String getStatus(String requestId) {
    RequestLog existingRecord = logsRepo.findByClientRequestId(requestId);
    if (existingRecord != null && existingRecord.getLinkStatus() != null) {
      return existingRecord.getLinkStatus();
    }
    return "Record failed but stored in database";
  }

  @Transactional
  public void setHipOnInitResponse(LinkOnInitResponse data, LinkConfirm linkConfirm) {
    Query query = new Query(Criteria.where("gatewayRequestId").is(data.getResp().getRequestId()));
    RequestLog existingRecord = mongoTemplate.findOne(query, RequestLog.class);
    HashMap<String, Object> map = existingRecord.getRawResponse();
    map.put("HIPOnInitResponse", data);
    if (existingRecord != null) {
      Update update =
          (new Update())
              .set("rawResponse", map)
              .set("gatewayRequestId", linkConfirm.getRequestId());
      mongoTemplate.updateFirst(query, update, RequestLog.class);
    }
  }

  @Transactional
  public void setHipOnConfirmResponse(
      LinkOnConfirmResponse data, LinkAddCareContext linkAddCareContext) {
    Query query = new Query(Criteria.where("gatewayRequestId").is(data.getResp().getRequestId()));
    RequestLog existingRecord = mongoTemplate.findOne(query, RequestLog.class);
    HashMap<String, Object> map = existingRecord.getRawResponse();
    map.put("HIPOnConfirm", data);
    if (existingRecord != null) {
      Update update =
          (new Update())
              .set("rawResponse", map)
              .set("gatewayRequestId", linkAddCareContext.getRequestId());
      mongoTemplate.updateFirst(query, update, RequestLog.class);
    }
  }

  @Transactional
  public void setHipOnInitResponseOTP(LinkOnInitResponse data) {
    Query query = new Query(Criteria.where("gatewayRequestId").is(data.getResp().getRequestId()));
    RequestLog existingRecord = mongoTemplate.findOne(query, RequestLog.class);
    HashMap<String, Object> map = existingRecord.getRawResponse();
    map.put("HIPOnInitOtp", data);
    if (existingRecord != null) {
      Update update = (new Update()).set("rawResponse", map);
      mongoTemplate.updateFirst(query, update, RequestLog.class);
    }
  }

  public void updateOnInitResponseOTP(String clientRequestId, String gateWayRequestId) {
    Query query = new Query(Criteria.where("clientRequestId").is(clientRequestId));
    RequestLog existingRecord = mongoTemplate.findOne(query, RequestLog.class);
    if (existingRecord != null) {
      Update update = (new Update()).set("gatewayRequestId", gateWayRequestId);
      mongoTemplate.updateFirst(query, update, RequestLog.class);
    }
  }

  public void setHipOnAddCareContextResponse(@RequestBody LinkOnAddCareContextsResponse data) {
    RequestLog existingRecord = logsRepo.findByGatewayRequestId(data.getResp().getRequestId());
    try {
      if (existingRecord != null) {
        HashMap<String, Object> map = existingRecord.getRawResponse();
        map.put("HIPOnAddCareContext", data);
        Query query =
            new Query(Criteria.where("gatewayRequestId").is(data.getResp().getRequestId()));
        Update update =
            (new Update())
                .set(
                    "linkStatus",
                    data.getAcknowledgement() == null
                        ? data.getError().getMessage()
                        : data.getAcknowledgement().getStatus())
                .set("rawResponse", map);
        mongoTemplate.updateFirst(query, update, RequestLog.class);
        LinkRecordsResponse linkRecordsResponse =
            (LinkRecordsResponse) existingRecord.getRawResponse().get("LinkRecordsResponse");
        patientService.updateCareContextStatus(
            linkRecordsResponse.getAbhaAddress(),
            linkRecordsResponse.getPatient().getCareContexts());
      }
    } catch (Exception e) {
      log.error(Exceptions.unwrap(e));
    }
  }
}
