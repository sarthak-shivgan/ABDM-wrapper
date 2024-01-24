/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.common.models.CareContext;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.RequestLog;
import com.nha.abdm.wrapper.hip.hrp.discover.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkAddCareContext;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkConfirm;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.requests.LinkRecordsRequest;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnAddCareContextsResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnConfirmResponse;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkOnInitResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.InitResponse;
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

  /**
   * Fetch of patient abhaAddress from requestLogs.
   *
   * @param linkRefNumber identifier for list of careContexts for linking.
   * @return abhaAddress
   */
  public String getPatientId(String linkRefNumber) {
    RequestLog existingRecord = logsRepo.findByLinkRefNumber(linkRefNumber);
    InitResponse data = (InitResponse) existingRecord.getRawResponse().get("InitResponse");
    return data.getPatient().getId();
  }
  /**
   * Fetch of patientReferenceNumber from requestLogs.
   *
   * @param linkRefNumber identifier for list of careContexts for linking.
   * @return patientReference
   */
  public String getPatientReference(String linkRefNumber) {
    RequestLog existingRecord = logsRepo.findByLinkRefNumber(linkRefNumber);
    InitResponse data = (InitResponse) existingRecord.getRawResponse().get("InitResponse");
    return data.getPatient().getReferenceNumber();
  }

  /**
   * <B>discovery</B>
   *
   * <p>Adding discoverResponseDump into db.
   *
   * @param discoverResponse Response from ABDM gateway for discovery
   */
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

  /**
   * <B>discovery</B>
   *
   * <p>Adding initResponse dump into db.
   *
   * @param initResponse Response from ABDM gateway for linking particular careContexts.
   */
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

  /**
   * Select the careContexts according to the careContexts referenceNumbers of the response
   *
   * @param linkRefNumber identifier for list of careContexts for linking.
   * @param careContextsList list of careContexts in response of /link/init.
   * @return the selected careContexts.
   */
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
  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>Checking the status of hipLinking
   *
   * @param requestId Response from ABDM gateway for discovery.
   * @return status of linking after /on-add-contexts acknowledgment.
   */
  public String getStatus(String requestId) {
    RequestLog existingRecord = logsRepo.findByClientRequestId(requestId);
    if (existingRecord != null && existingRecord.getLinkStatus() != null) {
      return existingRecord.getLinkStatus();
    }
    return "Record failed to link with abhaAddress";
  }
  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>Adding linkRecordsResponse dump into db.
   *
   * @param linkRecordsRequest Response received to facade for hipLinking.
   */
  @Transactional
  public void setHipLinkResponse(LinkRecordsRequest linkRecordsRequest) {
    if (Objects.isNull(linkRecordsRequest)) {
      return;
    }
    RequestLog newRecord = new RequestLog();
    newRecord.setClientRequestId(linkRecordsRequest.getRequestId());
    newRecord.setGatewayRequestId(linkRecordsRequest.getRequestId());
    HashMap<String, Object> map = new HashMap<>();
    map.put("LinkRecordsResponse", linkRecordsRequest);
    newRecord.setRawResponse(map);
    mongoTemplate.save(newRecord);
  }
  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>Adding linkOnInitResponse dump into db.
   *
   * @param linkOnInitResponse Response from ABDM gateway after successful auth/init.
   */
  @Transactional
  public void setHipOnInitResponse(LinkOnInitResponse linkOnInitResponse, LinkConfirm linkConfirm) {
    Query query =
        new Query(
            Criteria.where("gatewayRequestId").is(linkOnInitResponse.getResp().getRequestId()));
    RequestLog existingRecord = mongoTemplate.findOne(query, RequestLog.class);
    HashMap<String, Object> map = existingRecord.getRawResponse();
    map.put("HIPOnInitResponse", linkOnInitResponse);
    if (existingRecord != null) {
      Update update =
          (new Update())
              .set("rawResponse", map)
              .set("gatewayRequestId", linkConfirm.getRequestId());
      mongoTemplate.updateFirst(query, update, RequestLog.class);
    }
  }
  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>Adding linkOnConfirmResponse dump into db.
   *
   * @param linkOnConfirmResponse Response from ABDM gateway for successful auth/on-confirm.
   */
  @Transactional
  public void setHipOnConfirmResponse(
      LinkOnConfirmResponse linkOnConfirmResponse, LinkAddCareContext linkAddCareContext) {
    Query query =
        new Query(
            Criteria.where("gatewayRequestId").is(linkOnConfirmResponse.getResp().getRequestId()));
    RequestLog existingRecord = mongoTemplate.findOne(query, RequestLog.class);
    HashMap<String, Object> map = existingRecord.getRawResponse();
    map.put("HIPOnConfirm", linkOnConfirmResponse);
    if (existingRecord != null) {
      Update update =
          (new Update())
              .set("rawResponse", map)
              .set("gatewayRequestId", linkAddCareContext.getRequestId());
      mongoTemplate.updateFirst(query, update, RequestLog.class);
    }
  }

  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>Adding only linkOnInitResponse dump into db if authMode is OTP .
   *
   * @param linkOnInitResponse Response from ABDM gateway for successful auth/init.
   */
  @Transactional
  public void setHipOnInitResponseOTP(LinkOnInitResponse linkOnInitResponse) {
    Query query =
        new Query(
            Criteria.where("gatewayRequestId").is(linkOnInitResponse.getResp().getRequestId()));
    RequestLog existingRecord = mongoTemplate.findOne(query, RequestLog.class);
    HashMap<String, Object> map = existingRecord.getRawResponse();
    map.put("HIPOnInitOtp", linkOnInitResponse);
    if (existingRecord != null) {
      Update update = (new Update()).set("rawResponse", map);
      mongoTemplate.updateFirst(query, update, RequestLog.class);
    }
  }
  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>Updating gatewayRequestId if authMode is OTP since the dump is already stored in db.
   *
   * @param gateWayRequestId requestId in auth/confirm.
   * @param clientRequestId requestId in auth/on-init.
   */
  public void updateOnInitResponseOTP(String clientRequestId, String gateWayRequestId) {
    Query query = new Query(Criteria.where("clientRequestId").is(clientRequestId));
    RequestLog existingRecord = mongoTemplate.findOne(query, RequestLog.class);
    if (existingRecord != null) {
      Update update = (new Update()).set("gatewayRequestId", gateWayRequestId);
      mongoTemplate.updateFirst(query, update, RequestLog.class);
    }
  }

  /**
   * <B>hipInitiatedLinking</B>
   *
   * <p>Adding linkOnAddCareContextsResponse dump into db.
   *
   * @param linkOnAddCareContextsResponse Acknowledgement from ABDM gateway for HipLinking.
   */
  public void setHipOnAddCareContextResponse(
      @RequestBody LinkOnAddCareContextsResponse linkOnAddCareContextsResponse) {
    RequestLog existingRecord =
        logsRepo.findByGatewayRequestId(linkOnAddCareContextsResponse.getResp().getRequestId());
    try {
      if (existingRecord != null) {
        HashMap<String, Object> map = existingRecord.getRawResponse();
        map.put("HIPOnAddCareContext", linkOnAddCareContextsResponse);
        Query query =
            new Query(
                Criteria.where("gatewayRequestId")
                    .is(linkOnAddCareContextsResponse.getResp().getRequestId()));
        Update update =
            (new Update())
                .set(
                    "linkStatus",
                    linkOnAddCareContextsResponse.getAcknowledgement() == null
                        ? linkOnAddCareContextsResponse.getError().getMessage()
                        : linkOnAddCareContextsResponse.getAcknowledgement().getStatus())
                .set("rawResponse", map);
        mongoTemplate.updateFirst(query, update, RequestLog.class);
        LinkRecordsRequest linkRecordsRequest =
            (LinkRecordsRequest) existingRecord.getRawResponse().get("LinkRecordsResponse");
        patientService.addPatientCareContexts(linkRecordsRequest);
      }
    } catch (Exception e) {
      log.error(Exceptions.unwrap(e));
    }
  }
}
