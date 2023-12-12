package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.nha.abdm.wrapper.hrp.common.Utils;
import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.mongo.tables.PatientTable;
import com.nha.abdm.wrapper.hrp.repository.PatientRepo;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientTableService {
    private static final Logger log = LogManager.getLogger(PatientTableService.class);
    @Autowired
    private final PatientRepo patientRepo;
    @Autowired
    Utils utils;
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    public PatientTableService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    @Transactional
    public void addPatient(LinkRecordsResponse data) {
        PatientTable existingRecord = this.patientRepo.findByAbhaAddress(data.getAbhaAddress());
        List<LinkRecordsResponse.CareContext> careContexts = data.getPatient().getCareContexts();
        if (existingRecord == null) {
            PatientTable newRecord = new PatientTable(data.getAbhaAddress(), data.getName(), data.getGender(), data.getDateOfBirth(), data.getPatient().getReferenceNumber(), data.getPatient().getDisplay(), careContexts, this.utils.getCurrentTimeStamp());
            this.patientRepo.insert(newRecord);
        } else {
            Query query = new Query(Criteria.where("abhaAddress").is(data.getAbhaAddress()));
            Update update = (new Update()).set("careContext", data.getPatient().getCareContexts());
            this.mongoTemplate.updateFirst(query, update, PatientTable.class);
        }

    }

    public List<LinkRecordsResponse.CareContext> getCareContexts(String abhaAddress) {
        PatientTable existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
        return existingRecord != null ? existingRecord.getCareContexts() : null;
    }

    public String getPatientReference(String abhaAddress) {
        PatientTable existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
        return existingRecord != null ? existingRecord.getReferenceNumber() : "";
    }

    public String getPatientDisplay(String abhaAddress) {
        PatientTable existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
        return existingRecord != null ? existingRecord.getDisplay() : "";
    }
}
