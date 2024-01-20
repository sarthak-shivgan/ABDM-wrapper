/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo.services;

import com.nha.abdm.wrapper.common.models.CareContext;
import com.nha.abdm.wrapper.common.models.FacadeResponse;
import com.nha.abdm.wrapper.hip.hrp.common.requests.CareContextRequest;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.LogsRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.link.hipInitiated.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hip.hrp.link.userInitiated.responses.InitResponse;
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

  /**
   * Fetch of patientReference using abhaAddress
   *
   * @param abhaAddress abhaAddress of patient.
   * @return patientReference.
   */
  public String getPatientReference(String abhaAddress) {
    Patient existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
    return existingRecord != null ? existingRecord.getPatientReference() : "";
  }
  /**
   * Fetch of patientDisplay using abhaAddress
   *
   * @param abhaAddress abhaAddress of patient.
   * @return patientDisplay.
   */
  public String getPatientDisplay(String abhaAddress) {
    Patient existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
    return existingRecord != null ? existingRecord.getDisplay() : "";
  }
  /**
   * Fetch of abhaAddress using abhaAddress
   *
   * @param patientReference patientReference of patient.
   * @return abhaAddress.
   */
  public String getAbhaAddress(String patientReference) {
    Patient existingRecord = this.patientRepo.findByPatientReference(patientReference);
    return existingRecord != null ? existingRecord.getAbhaAddress() : "";
  }

  /**
   * After successful linking of careContext updating the status i.e. isLinked to true or false.
   *
   * @param abhaAddress abhaAddress of patient.
   * @param careContexts List of careContext to update the status.
   */
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

  /**
   * <B>discovery</B>
   *
   * <p>Match the response careContexts with patient careContexts.
   *
   * @param initResponse Response from ABDM gateway to link particular careContexts.
   * @return if careContexts matches returns returns true else false.
   */
  public boolean checkCareContexts(InitResponse initResponse) {
    try {
      List<CareContext> patientCareContexts =
          patientRepo
              .findByPatientReference(initResponse.getPatient().getReferenceNumber())
              .getCareContexts();
      Set<String> patientReferenceNumbers =
          patientCareContexts.stream()
              .map(CareContext::getReferenceNumber)
              .collect(Collectors.toSet());
      return initResponse.getPatient().getCareContexts().stream()
          .allMatch(
              responseContext ->
                  patientReferenceNumbers.contains(responseContext.getReferenceNumber()));

    } catch (NullPointerException e) {
      log.error("Init CareContext verify failed -> mismatch of careContexts" + e);
    }
    return true;
  }

  public FacadeResponse addPatient(LinkRecordsResponse linkRecordsResponse) {
    String abhaAddress = linkRecordsResponse.getAbhaAddress();
    try {
      Patient existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
      if (existingRecord == null) {
        log.error("Adding patient failed -> Patient not found");
      } else {
        List<Map<String, Object>> modifiedCareContexts =
            linkRecordsResponse.getPatient().getCareContexts().stream()
                .map(
                    careContext -> {
                      Map<String, Object> modifiedContext = new HashMap<>();
                      modifiedContext.put("referenceNumber", careContext.getReferenceNumber());
                      modifiedContext.put("display", careContext.getDisplay());
                      modifiedContext.put("isLinked", false);
                      return modifiedContext;
                    })
                .collect(Collectors.toList());
        Query query =
            new Query(Criteria.where("abhaAddress").is(linkRecordsResponse.getAbhaAddress()));
        Update update = new Update().addToSet("careContext").each(modifiedCareContexts);
        this.mongoTemplate.updateFirst(query, update, Patient.class);
      }
    } catch (Exception e) {
      log.info("addPatient :" + e);
    }
    return FacadeResponse.builder().message("Successfully Added Patient").build();
  }

  /**
   * Store/update patient demographic data.
   *
   * @param patient patient demographic details.
   * @return acknowledgement of storing of patient.
   */
  public FacadeResponse addPatientInWrapper(Patient patient) {
    Patient existingRecord = patientRepo.findByAbhaAddress(patient.getAbhaAddress());
    if (existingRecord == null) {
      Patient newRecord = new Patient();
      newRecord.setName(patient.getName());
      newRecord.setAbhaAddress(patient.getAbhaAddress());
      newRecord.setPatientReference(patient.getPatientReference());
      newRecord.setGender(patient.getGender());
      newRecord.setDateOfBirth(patient.getDateOfBirth());
      newRecord.setDisplay(patient.getDisplay());
      newRecord.setPatientMobile(patient.getPatientMobile());
      mongoTemplate.save(newRecord);
      log.info("Successfully Added Patient : " + patient.toString());

    } else {
      Update update =
          new Update()
              .set("abhaAddress", patient.getAbhaAddress())
              .set("name", patient.getName())
              .set("gender", patient.getGender())
              .set("dateOfBirth", patient.getDateOfBirth())
              .set("display", patient.getDisplay())
              .set("patientReference", patient.getPatientReference())
              .set("patientMobile", patient.getPatientMobile());
      Query query = new Query(Criteria.where("abhaAddress").is(patient.getAbhaAddress()));
      mongoTemplate.updateFirst(query, update, Patient.class);
      log.info("Successfully Updated Patient : " + patient.toString());
      return FacadeResponse.builder().message("Successfully Updated Patient").build();
    }
    return null;
  }
}
