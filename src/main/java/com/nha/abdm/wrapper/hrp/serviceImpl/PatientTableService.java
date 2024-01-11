package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.nha.abdm.wrapper.hrp.common.CareContextBuilder;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.InitResponse;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.helpers.InitCareContextList;
import com.nha.abdm.wrapper.hrp.mongo.tables.Patients;
import com.nha.abdm.wrapper.hrp.mongo.tables.RequestLogs;
import com.nha.abdm.wrapper.hrp.repository.LogsRepo;
import com.nha.abdm.wrapper.hrp.repository.PatientRepo;
import java.util.List;
import java.util.Objects;
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
public class PatientTableService {
    private static final Logger log = LogManager.getLogger(PatientTableService.class);
    @Autowired
    private final PatientRepo patientRepo;
    @Autowired
    private LogsRepo logsRepo;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    public PatientTableService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }


    public String getPatientReference(String abhaAddress) {
        Patients existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
        return existingRecord != null ? existingRecord.getPatientReference() : "";
    }

    public String getPatientDisplay(String abhaAddress) {
        Patients existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
        return existingRecord != null ? existingRecord.getDisplay() : "";
    }
    public String getAbhaAddress(String patientReference){
        Patients existingRecord = this.patientRepo.findByPatientReference(patientReference);
        return existingRecord != null ? existingRecord.getAbhaAddress() : "";
    }

    public void updateCareContextStatus(String patientReference, List<CareContextBuilder> careContexts) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Patients.class);

        for (CareContextBuilder updatedCareContext : careContexts) {
            Query query = Query.query(
                    Criteria.where("patientReference").is(patientReference)
                            .and("careContexts.referenceNumber").is(updatedCareContext.getReferenceNumber())
            );

            Update update = new Update().set("careContexts.$.isLinked", true);
            bulkOperations.updateOne(query, update);
        }

        bulkOperations.execute();
    }

    public boolean checkCareContexts(InitResponse data) {
        try {
            List<CareContextBuilder> patientCareContexts = patientRepo.findByPatientReference(data.getPatient().getReferenceNumber()).getCareContexts();
            Set<String> patientReferenceNumbers = patientCareContexts.stream()
                    .map(CareContextBuilder::getReferenceNumber)
                    .collect(Collectors.toSet());
            return data.getPatient().getCareContexts().stream()
                    .allMatch(responseContext -> patientReferenceNumbers.contains(responseContext.getReferenceNumber()));

        }catch (NullPointerException e){
            log.error("Init CareContext verify failed -> mismatch of careContexts" + e);
        }
        return true;
    }
}
