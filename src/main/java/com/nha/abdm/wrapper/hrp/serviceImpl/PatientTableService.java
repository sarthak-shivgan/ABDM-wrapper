package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.nha.abdm.wrapper.hrp.common.CareContextBuilder;
import com.nha.abdm.wrapper.hrp.discoveryLinking.responses.DiscoverResponse;
import com.nha.abdm.wrapper.hrp.mongo.tables.Patients;
import com.nha.abdm.wrapper.hrp.repository.PatientRepo;
import java.util.List;
import java.util.Objects;
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
    MongoTemplate mongoTemplate;

    @Autowired
    public PatientTableService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    public List<CareContextBuilder> getCareContexts(String abhaAddress, Object content) {
        if(Objects.nonNull(abhaAddress)){
            Patients existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
            return existingRecord != null ? existingRecord.getCareContexts() : null;
        }
        if(content==DiscoverResponse.class){
            DiscoverResponse data=(DiscoverResponse) content;
            String patientIdentifier=null;
            if(data.getPatient().getUnverifiedIdentifiers()!=null) patientIdentifier=data.getPatient().getUnverifiedIdentifiers().get(0).getValue();
            String patientName=data.getPatient().getName();
            String patientMobile=data.getPatient().getVerifiedIdentifiers().get(0).getValue();
            if(Objects.nonNull(patientIdentifier)){
                Patients existingRecord = this.patientRepo.findByPatientReference(patientIdentifier);
                return existingRecord != null ? existingRecord.getCareContexts() : null;
            }
            if(Objects.nonNull(patientMobile)){
                Patients existingRecord = this.patientRepo.findByPatientMobile(patientMobile);
                return existingRecord != null ? existingRecord.getCareContexts() : null;
            }
        }
        return null;
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
}
