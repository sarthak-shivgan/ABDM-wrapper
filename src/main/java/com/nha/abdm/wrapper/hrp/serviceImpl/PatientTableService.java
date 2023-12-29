package com.nha.abdm.wrapper.hrp.serviceImpl;

import com.nha.abdm.wrapper.hrp.common.CareContextBuilder;
//import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.AddPatient;
//import com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse;
import com.nha.abdm.wrapper.hrp.mongo.tables.Patients;
import com.nha.abdm.wrapper.hrp.repository.PatientRepo;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;


@Document(collection = "patients")
@Service
public class PatientTableService {
    private static final Logger log = LogManager.getLogger(PatientTableService.class);
    @Autowired
    private final PatientRepo patientRepo;

    @Autowired
    public PatientTableService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    public List<CareContextBuilder> getCareContexts(String abhaAddress) {
        Patients existingRecord = this.patientRepo.findByAbhaAddress(abhaAddress);
        return existingRecord != null ? existingRecord.getCareContexts() : null;
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
}
