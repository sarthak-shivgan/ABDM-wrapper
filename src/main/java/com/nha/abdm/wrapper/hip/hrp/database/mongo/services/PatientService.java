/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.services;

import com.nha.abdm.wrapper.common.models.AckRequest;
import com.nha.abdm.wrapper.common.models.CareContext;
import com.nha.abdm.wrapper.hip.hrp.common.requests.CareContextRequest;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.hipLink.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hip.hrp.link.responses.InitResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Document(collection = "patients")
@Service
public class PatientService {
  private static final Logger log = LogManager.getLogger(PatientService.class);
  @Autowired private final PatientRepo patientRepo;
  @Autowired private LogsRepo logsRepo;

  @Autowired MongoTemplate mongoTemplate;

  @Autowired
  public PatientService(PatientRepo patientRepo) {
    this.patientRepo = patientRepo;
  }

  public String getPatientReference(String abhaAddress) {
    Patient existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
    return existingRecord != null ? existingRecord.getPatientReference() : "";
  }

  public String getPatientDisplay(String abhaAddress) {
    Patient existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
    return existingRecord != null ? existingRecord.getDisplay() : "";
  }

  public String getAbhaAddress(String patientReference) {
    Patient existingRecord = this.patientRepo.findByPatientReference(patientReference);
    return existingRecord != null ? existingRecord.getAbhaAddress() : "";
  }

  public void updateCareContextStatus(String abhaAddress, List<CareContextRequest> careContexts) {
    BulkOperations bulkOperations =
        mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Patient.class);

    for (CareContextRequest updatedCareContext : careContexts) {
      Query query =
          Query.query(
              Criteria.where("abhaAddress")
                  .is(abhaAddress)
                  .and("careContexts.referenceNumber")
                  .is(updatedCareContext.getReferenceNumber()));

      Update update = new Update().set("careContexts.$.isLinked", true);
      bulkOperations.updateOne(query, update);
    }

    bulkOperations.execute();
  }

  public boolean checkCareContexts(InitResponse data) {
    try {
      List<CareContext> patientCareContexts =
          patientRepo
              .findByPatientReference(data.getPatient().getReferenceNumber())
              .getCareContexts();
      Set<String> patientReferenceNumbers =
          patientCareContexts.stream()
              .map(CareContext::getReferenceNumber)
              .collect(Collectors.toSet());
      return data.getPatient().getCareContexts().stream()
          .allMatch(
              responseContext ->
                  patientReferenceNumbers.contains(responseContext.getReferenceNumber()));

    } catch (NullPointerException e) {
      log.error("Init CareContext verify failed -> mismatch of careContexts" + e);
    }
    return true;
  }

  public AckRequest addPatient(LinkRecordsResponse data) {
    String abhaAddress = data.getAbhaAddress();
    try {
      Patient existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
      if (existingRecord == null) {
        throw new RuntimeException("Patient not found");
      } else {
        List<Map<String, Object>> modifiedCareContexts =
            data.getPatient().getCareContexts().stream()
                .map(
                    careContext -> {
                      Map<String, Object> modifiedContext = new HashMap<>();
                      modifiedContext.put("referenceNumber", careContext.getReferenceNumber());
                      modifiedContext.put("display", careContext.getDisplay());
                      modifiedContext.put("isLinked", false);
                      return modifiedContext;
                    })
                .collect(Collectors.toList());
        Query query = new Query(Criteria.where("abhaAddress").is(data.getAbhaAddress()));
        Update update = new Update().addToSet("careContext").each(modifiedCareContexts); // TODO
        this.mongoTemplate.updateFirst(query, update, Patient.class);
      }
    } catch (Exception e) {
      log.info("addPatient :" + e);
    }
    return AckRequest.builder().message("Successfully Added Patient").build();
  }

  public AckRequest addPatientInWrapper(Patient data) {
    Patient existingRecord = patientRepo.findByAbhaAddress(data.getAbhaAddress());
    if (existingRecord == null) {
      Patient newRecord = new Patient();
      newRecord.setName(data.getName());
      newRecord.setAbhaAddress(data.getAbhaAddress());
      newRecord.setPatientReference(data.getPatientReference());
      newRecord.setGender(data.getGender());
      newRecord.setDateOfBirth(data.getDateOfBirth());
      newRecord.setDisplay(data.getDisplay());
      newRecord.setPatientMobile(data.getPatientMobile());
      mongoTemplate.save(newRecord);
      log.info("Successfully Added Patient : " + data.toString());

    } else {
      Update update =
          new Update()
              .set("abhaAddress", data.getAbhaAddress())
              .set("name", data.getName())
              .set("gender", data.getGender())
              .set("dateOfBirth", data.getDateOfBirth())
              .set("display", data.getDisplay())
              .set("patientReference", data.getPatientReference())
              .set("patientMobile", data.getPatientMobile());
      Query query = new Query(Criteria.where("abhaAddress").is(data.getAbhaAddress()));
      mongoTemplate.updateFirst(query, update, Patient.class);
      log.info("Successfully Updated Patient : " + data.toString());
      return AckRequest.builder().message("Successfully Updated Patient").build();
    }
    return null;
  }
}
