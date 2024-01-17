/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.services;

import com.nha.abdm.wrapper.common.models.CareContext;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.link.responses.InitResponse;
import java.util.List;
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

  public void updateCareContextStatus(String patientReference, List<CareContext> careContexts) {
    BulkOperations bulkOperations =
        mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Patient.class);

    for (CareContext updatedCareContext : careContexts) {
      Query query =
          Query.query(
              Criteria.where("patientReference")
                  .is(patientReference)
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
}
